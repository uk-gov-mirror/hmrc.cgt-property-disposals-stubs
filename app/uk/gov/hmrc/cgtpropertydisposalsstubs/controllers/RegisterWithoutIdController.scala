/*
 * Copyright 2019 HM Revenue & Customs
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

import com.google.inject.Inject
import org.scalacheck.Gen
import play.api.libs.json.{JsError, JsSuccess, Json, Reads, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.RegisterWithoutIdController.{RegistrationRequest, Response}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.SapNumber
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext
import scala.util.Random
import scala.util.matching.Regex

class RegisterWithoutIdController @Inject()(
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def registerWithoutId(): Action[AnyContent] = Action { implicit request =>
    request.body.asJson.fold[Result] {
      logger.warn("Could not find JSON in request body for register without id request")
      BadRequest
    } { json =>
      json.validate[RegistrationRequest] match {
        case JsSuccess(registrationRequest, _) =>
          logger.info(s"Received register without id request with body $registrationRequest")
          registrationRequest.address.line1 match {
            case statusRegex(status) => Status(status.toInt)
            case _ => Ok(Json.toJson(Response(randomSapNumber())))
          }

        case JsError(errors) =>
          logger.warn(s"Could not parse JSON in request: $errors")
          BadRequest
      }

    }


  }

  private def randomSapNumber(): SapNumber =
    Gen.listOfN(10, Gen.numChar).map(_.mkString(""))
      .sample
      .map(SapNumber(_))
      .getOrElse(sys.error("Could not generate sap number"))

  private val statusRegex: Regex = """Fail Registration (\d{3})""".r

}

object RegisterWithoutIdController {

  final case class RegistrationRequest(
                                        regime: String,
                                        isAnAgent: Boolean,
                                        isAGroup: Boolean,
                                        individual: RegistrationIndividual,
                                        address: RegistrationAddress,
                                        contactDetails: RegistrationContactDetails
                                      )

  final case class RegistrationIndividual(firstName: String, lastName: String)

  final case class RegistrationContactDetails(emailAddress: String)

  final case class RegistrationAddress(
                                        line1: String,
                                        line2: Option[String],
                                        line3: Option[String],
                                        line4: Option[String],
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
