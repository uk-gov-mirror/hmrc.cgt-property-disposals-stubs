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
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request, Result}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.{NINO, SAUTR, SapNumber}
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import uk.gov.hmrc.smartstub.Enumerable.instances.ninoEnumNoSpaces
import uk.gov.hmrc.smartstub.PatternContext
import uk.gov.hmrc.smartstub._

import scala.util.Random

class BusinessPartnerRecordController @Inject()(cc: ControllerComponents) extends BackendController(cc) with Logging {

  import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController._
  import DesBusinessPartnerRecord._

  implicit val ninoEnumerable: Enumerable[NINO] = ninoEnumNoSpaces.imap(NINO)(_.value)
  implicit val sautrEnumerable: Enumerable[SAUTR] = pattern"9999999999".imap(SAUTR)(_.value)

  implicit def eitherToLong[A,B](implicit a: ToLong[A], b: ToLong[B]): ToLong[Either[A,B]] =
    new ToLong[Either[A, B]] {
      override def asLong(i: Either[A, B]): Long = i.fold(a.asLong, b.asLong)
    }

  def getBusinessPartnerRecord(idType: String, idValue: String): Action[AnyContent] = Action { implicit request =>
    idType match {
      case "nino" => handleRequest(request, Right(NINO(idValue)))
      case "sautr" => handleRequest(request, Left(SAUTR(idValue)))
      case _ =>
        logger.warn(s"Received request for BPR for unsupported id type '$idType' with value '$idValue'")
        BadRequest
    }
  }

  private def handleRequest(request: Request[AnyContent], id: Either[SAUTR,NINO]): Result = {
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
                .getProfile(id)
                .map(_.bprResponse.map(bpr => Ok(Json.toJson(bpr))).merge)
                .getOrElse {
                  val bpr = bprGen(bprRequest.individual).seeded(id).get
                  Ok(Json.toJson(bpr))
                }

            val correlationId = Random.alphanumeric.take(32).mkString("")

            logger.info(
              s"Received BPR request for id $id and request body $bprRequest. Returning result " +
                s"${result.toString()} with correlation id $correlationId"
            )
            result.withHeaders("CorrelationId" -> correlationId)
          }
        )
    }
  }

  def errorResponse(errorCode: String, errorMessage: String): JsValue =
    Json.toJson(DesErrorResponse(errorCode, errorMessage))

  def bprGen(maybeIndividual: Option[Individual]): Gen[DesBusinessPartnerRecord] = {
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
      organisationName <- Gen.company
      forename <- Gen.forename()
      surname <- Gen.surname
    } yield {
      val email = {
        val local = maybeIndividual.map(i => s"${i.firstName}.${i.lastName}").getOrElse(s"$forename.$surname")
        s"$local@email.com"
      }
      val organisation =
        maybeIndividual.fold[Option[DesOrganisation]](Some(DesOrganisation(organisationName)))(_ => None)

      DesBusinessPartnerRecord(address, DesContactDetails(Some(email)), SapNumber(sapNumber), organisation)
    }
  }

}

object BusinessPartnerRecordController {

  final case class Individual(firstName: String, lastName: String, dateOfBirth: Option[LocalDate])

  final case class BprRequest(
    regime: String,
    requiresNameMatch: Boolean,
    isAnIndividual: Boolean,
    individual: Option[Individual]
  )

  final case class DesErrorResponse(code: String, reason: String)

  object DesErrorResponse {
    implicit val desErrorWrites: Writes[DesErrorResponse] = Json.writes[DesErrorResponse]
  }

  import DesBusinessPartnerRecord._

  final case class DesBusinessPartnerRecord(
    address: DesAddress,
    contactDetails: DesContactDetails,
    sapNumber: SapNumber,
    organisation: Option[DesOrganisation]
  )

  object DesBusinessPartnerRecord {

    final case class DesOrganisation(
                                    name: String
                                    )

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
    implicit val organisationWrites: Writes[DesOrganisation]     = Json.writes[DesOrganisation]
    implicit val addressWrites: Writes[DesAddress]               = Json.writes[DesAddress]
    implicit val contactDetailsWrites: Writes[DesContactDetails] = Json.writes[DesContactDetails]
    implicit val bprWrites: Writes[DesBusinessPartnerRecord]     = Json.writes[DesBusinessPartnerRecord]
  }

}
