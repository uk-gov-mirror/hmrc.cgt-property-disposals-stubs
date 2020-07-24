/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.cgtpropertydisposalsstubs.controllers

import com.eclipsesource.schema.drafts.Version4
import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import com.google.inject.Inject
import org.scalacheck.Gen
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.RegisterWithoutIdController.{RegistrationRequest, Response}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.SapNumber
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import Version4._
import scala.io.Source
import scala.util.matching.Regex

class RegisterWithoutIdController @Inject() (
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging {

  lazy val schemaToBeValidated = Json
    .fromJson[SchemaType](
      Json.parse(
        Source
          .fromInputStream(
            this.getClass.getResourceAsStream("/resources/register-without-id-des-schema-4.json")
          )
          .mkString
      )
    )
    .get

  def registerWithoutId(): Action[AnyContent] = Action { implicit request =>
    request.body.asJson.fold[Result] {
      logger.warn("Could not find JSON in request body for register without id request")
      BadRequest
    } { json =>
      SchemaValidator(Some(Version4)).validate(schemaToBeValidated, json)

      json.validate[RegistrationRequest] match {
        case JsSuccess(registrationRequest, _) =>
          logger.info(s"Received register without id request with body $registrationRequest")
          registrationRequest.address.addressLine1 match {
            case registrationStatusRegex(status) => Status(status.toInt)
            case subscriptionStatusRegex(status) =>
              Ok(Json.toJson(Response(SubscriptionProfiles.sapNumberForSubscriptionStatus(status.toInt))))
            case _ => Ok(Json.toJson(Response(randomSapNumber())))
          }

        case JsError(errors) =>
          logger.warn(s"Could not validate or parse JSON in request: $errors")
          BadRequest
      }

    }

  }

  private def randomSapNumber(): SapNumber =
    Gen
      .listOfN(10, Gen.numChar)
      .map(_.mkString(""))
      .sample
      .map(SapNumber(_))
      .getOrElse(sys.error("Could not generate sap number"))

  private val registrationStatusRegex: Regex = """Fail Registration (\d{3})""".r

  private val subscriptionStatusRegex: Regex = """Fail Subscription (\d{3})""".r

}

object RegisterWithoutIdController {

  final case class RegistrationRequest(
    regime: String,
    acknowledgementReference: String,
    isAnAgent: Boolean,
    isAGroup: Boolean,
    individual: RegistrationIndividual,
    address: RegistrationAddress,
    contactDetails: RegistrationContactDetails
  )

  final case class RegistrationIndividual(firstName: String, lastName: String)

  final case class RegistrationContactDetails(emailAddress: String)

  final case class RegistrationAddress(
    addressLine1: String,
    addressLine2: Option[String],
    addressLine3: Option[String],
    addressLine4: Option[String],
    postalCode: Option[String],
    countryCode: String
  )

  final case class Response(sapNumber: SapNumber)

  implicit val individualReads: Reads[RegistrationIndividual]         = Json.reads[RegistrationIndividual]
  implicit val addressReads: Reads[RegistrationAddress]               = Json.reads[RegistrationAddress]
  implicit val contactDetailsReads: Reads[RegistrationContactDetails] = Json.reads[RegistrationContactDetails]
  implicit val requestReads: Reads[RegistrationRequest]               = Json.reads[RegistrationRequest]
  implicit val responseWrites: Writes[Response]                       = Json.writes[Response]

}
