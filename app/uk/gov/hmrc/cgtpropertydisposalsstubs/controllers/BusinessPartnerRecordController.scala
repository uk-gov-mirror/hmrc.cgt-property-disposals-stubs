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

import com.google.inject.Inject
import org.scalacheck.Gen
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import uk.gov.hmrc.smartstub._
import uk.gov.hmrc.smartstub.AutoGen
import uk.gov.hmrc.smartstub.Enumerable.instances.ninoEnumNoSpaces

import scala.util.Random

class BusinessPartnerRecordController @Inject()(cc: ControllerComponents) extends BackendController(cc) {

  import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController._
  import DesBusinessPartnerRecord._

  def getBusinessPartnerRecord(nino: String): Action[AnyContent] = Action { implicit request =>
    val result = if (nino.startsWith("ER400")) {
      BadRequest(errorResponse("INVALID_NINO", "Submission has not passed validation. Invalid parameter NINO"))
    } else if (nino.startsWith("ER404")) {
      NotFound(errorResponse("NOT_FOUND", "The remote endpoint has indicated that no data can be found"))
    } else if (nino.startsWith("ER409")) {
      Conflict(errorResponse("CONFLICT", "The remote endpoint has indicated Duplicate Submission"))
    } else if (nino.startsWith("ER500")) {
      InternalServerError(errorResponse("SERVER_ERROR", "DES is currently experiencing problems that require live service intervention"))
    } else if (nino.startsWith("ER503")) {
      ServiceUnavailable(errorResponse("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding"))
    } else if (nino.equals("CG123456D")) {
      Ok(Json.toJson(DesBusinessPartnerRecord(
        DesIndividual("John", "Wick", LocalDate.of(2000, 1, 1)),
        DesAddress("3rd Wick Street", None, None, None, "JW123ST", "GB"),
        DesContactDetails(Some("testCGT@email.com"))
      )))
    } else {
      Ok(Json.toJson(bprAutoGen.seeded(nino).get))
    }

    result.withCorrelationId()
  }

  def errorResponse(errorCode: String, errorMessage: String): JsValue =
    Json.toJson(DesErrorResponse(errorCode, errorMessage))

  val bprAutoGen: Gen[DesBusinessPartnerRecord] = {
    val addressGen: Gen[DesAddress] = for{
      addressLines <- Gen.ukAddress
      postcode <- Gen.postcode
    } yield {
      val (l1, l2) = addressLines match {
        case Nil => ("1 the Street", None)
        case a1 :: Nil => (a1, None)
        case a1 :: a2 :: _ => (a1, Some(a2))
      }
      DesAddress(l1, l2, None, None, postcode, "GB")
    }

    for {
      individual <- AutoGen[DesIndividual]
      address <- addressGen
    } yield {
      val email = s"${individual.firstName.toLowerCase}.${individual.lastName.toLowerCase}@email.com"
      DesBusinessPartnerRecord(individual, address, DesContactDetails(Some(email)))
    }
  }



}

object BusinessPartnerRecordController {

  implicit class ResultOps(val r: Result) extends AnyVal {

    def withCorrelationId(): Result = r.withHeaders("CorrelationId" -> Random.alphanumeric.take(32).mkString(""))

  }

  final case class DesErrorResponse(code: String, reason: String)

  object DesErrorResponse {
    implicit val desErrorWrites: Writes[DesErrorResponse] = Json.writes[DesErrorResponse]
  }

  import DesBusinessPartnerRecord._

  final case class DesBusinessPartnerRecord(
                                             individual: DesIndividual,
                                             address: DesAddress,
                                             contactDetails: DesContactDetails
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

    final case class DesIndividual(
                                    firstName: String,
                                    lastName: String,
                                    dateOfBirth: LocalDate
                                  )

    final case class DesContactDetails(emailAddress: Option[String])

    implicit val addressWrites: Writes[DesAddress] = Json.writes[DesAddress]
    implicit val individualWrites: Writes[DesIndividual] = Json.writes[DesIndividual]
    implicit val contactDetailsWrites: Writes[DesContactDetails] = Json.writes[DesContactDetails]
    implicit val bprWrites: Writes[DesBusinessPartnerRecord] = Json.writes[DesBusinessPartnerRecord]

  }


}