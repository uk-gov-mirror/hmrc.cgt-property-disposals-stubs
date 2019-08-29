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

import java.time.LocalDate

import cats.syntax.either._
import com.google.inject.Inject
import org.scalacheck.Gen
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import uk.gov.hmrc.smartstub.Enumerable.instances.ninoEnumNoSpaces
import uk.gov.hmrc.smartstub._

import scala.util.Random

class BusinessPartnerRecordController @Inject()(cc: ControllerComponents) extends BackendController(cc) with Logging {

  import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController._
  import DesBusinessPartnerRecord._

  def getBusinessPartnerRecord(nino: String): Action[AnyContent] = Action { implicit request =>
    request.body.asJson.fold[Result] {
      logger.warn("Could not find JSON in request body for BPR request")
      BadRequest
    } { json =>
      json
        .validate[BprRequest]
        .fold(
          { e =>
            logger.warn(s"Could not read JSON in BPR request: $e")
            BadRequest
          }, { bprRequest =>
            val result =
              SubscriptionProfiles
                .getProfile(Left(nino))
                .map(_.bprResponse.map(bpr => Ok(Json.toJson(bpr))).merge)
                .getOrElse {
                  val bpr = bprAutoGen(bprRequest.individual.firstName, bprRequest.individual.lastName).seeded(nino).get
                  Ok(Json.toJson(bpr))
                }

            val id = Random.alphanumeric.take(32).mkString("")

            logger.info(
              s"Received BPR request for NINO $nino and request body $bprRequest. Returning result ${result.toString()} with correlation id $id"
            )
            result.withHeaders("CorrelationId" -> id)
          }
        )
    }
  }

  def errorResponse(errorCode: String, errorMessage: String): JsValue =
    Json.toJson(DesErrorResponse(errorCode, errorMessage))

  def bprAutoGen(forename: String, surname: String): Gen[DesBusinessPartnerRecord] = {
    val addressGen: Gen[DesAddress] = for {
      addressLines <- Gen.ukAddress
      postcode     <- Gen.postcode
    } yield {
      val (l1, l2) = addressLines match {
        case Nil           => ("1 the Street", None)
        case a1 :: Nil     => (a1, None)
        case a1 :: a2 :: _ => (a1, Some(a2))
      }
      DesAddress(l1, l2, None, None, postcode, "GB")
    }

    for {
      address   <- addressGen
      sapNumber <- Gen.listOfN(10, Gen.numChar).map(_.mkString(""))
    } yield {
      val email = s"${forename.toLowerCase}.${surname.toLowerCase}@email.com"
      DesBusinessPartnerRecord(address, DesContactDetails(Some(email)), sapNumber)
    }
  }

}

object BusinessPartnerRecordController {

  final case class Individual(firstName: String, lastName: String, dateOfBirth: LocalDate)

  final case class BprRequest(
    regime: String,
    requiresNameMatch: Boolean,
    isAnIndividual: Boolean,
    individual: Individual
  )

  final case class DesErrorResponse(code: String, reason: String)

  object DesErrorResponse {
    implicit val desErrorWrites: Writes[DesErrorResponse] = Json.writes[DesErrorResponse]
  }

  import DesBusinessPartnerRecord._

  final case class DesBusinessPartnerRecord(
    address: DesAddress,
    contactDetails: DesContactDetails,
    sapNumber: String
  )

  object DesBusinessPartnerRecord {

    final case class DesAddress(
      addressLine1: String,
      addressLine2: Option[String],
      addressLine3: Option[String],
      addressLine4: Option[String],
      postalCode: String,
      countryCode: String
    )

    final case class DesContactDetails(emailAddress: Option[String])

    implicit val individualReads: Reads[Individual]              = Json.reads[Individual]
    implicit val bprRequestReads: Reads[BprRequest]              = Json.reads[BprRequest]
    implicit val addressWrites: Writes[DesAddress]               = Json.writes[DesAddress]
    implicit val contactDetailsWrites: Writes[DesContactDetails] = Json.writes[DesContactDetails]
    implicit val bprWrites: Writes[DesBusinessPartnerRecord]     = Json.writes[DesBusinessPartnerRecord]
  }

}
