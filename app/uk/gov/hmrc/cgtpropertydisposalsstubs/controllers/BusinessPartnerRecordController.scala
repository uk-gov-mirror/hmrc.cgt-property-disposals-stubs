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

import akka.stream.Materializer
import cats.instances.string._
import cats.syntax.either._
import cats.syntax.eq._
import com.google.inject.Inject
import org.scalacheck.Gen
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import play.api.mvc._
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.{NINO, SAUTR, SapNumber}
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import uk.gov.hmrc.smartstub.Enumerable.instances.ninoEnumNoSpaces
import uk.gov.hmrc.smartstub.{PatternContext, _}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import scala.util.Random

class BusinessPartnerRecordController @Inject()(cc: ControllerComponents)(
  implicit mat: Materializer,
  ec: ExecutionContext
) extends BackendController(cc)
    with Logging {

  import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController._
  import DesBusinessPartnerRecord._

  implicit val ninoEnumerable: Enumerable[NINO]   = ninoEnumNoSpaces.imap(NINO(_))(_.value)
  implicit val sautrEnumerable: Enumerable[SAUTR] = pattern"9999999999".imap(SAUTR(_))(_.value)

  implicit def eitherToLong[A, B](implicit a: ToLong[A], b: ToLong[B]): ToLong[Either[A, B]] =
    new ToLong[Either[A, B]] {
      override def asLong(i: Either[A, B]): Long = i.fold(a.asLong, b.asLong)
    }

  def getBusinessPartnerRecord(entityType: String, idType: String, idValue: String): Action[AnyContent] = Action {
    implicit request =>
      (entityType, idType) match {
        case ("individual", "nino")    => handleRequest(request, Right(NINO(idValue)), true)
        case ("individual", "sautr")   => handleRequest(request, Left(SAUTR(idValue)), true)
        case ("organisation", "sautr") => handleRequest(request, Left(SAUTR(idValue)), false)
        case _ =>
          logger.warn(
            s"Received request for BPR for unsupported combination of entity type $entityType and " +
              s"id type '$idType' with value '$idValue'"
          )
          BadRequest
      }
  }

  private def handleRequest(request: Request[AnyContent], id: Either[SAUTR, NINO], isAnIndividual: Boolean): Result =
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
                  val bpr = bprGen(isAnIndividual, id).seeded(id).get

                  if (bprRequest.requiresNameMatch) {
                    doNameMatch(bprRequest.individual, bpr)
                  } else {
                    Ok(Json.toJson(bpr))
                  }

                }

            val correlationId = Random.alphanumeric.take(32).mkString("")
            val body          = Await.result(result.body.consumeData.map(_.utf8String), 1.second)
            logger.info(
              s"Received BPR request for id $id and request body $bprRequest. Returning result " +
                s"${result.toString()} with body $body with correlation id $correlationId"
            )
            result.withHeaders("CorrelationId" -> correlationId)
          }
        )
    }

  def doNameMatch(requestIndividual: Option[Individual], bpr: DesBusinessPartnerRecord): Result =
    requestIndividual.fold(
      BadRequest(bprErrorResponse("BAD_REQUEST", "requiresNameMatch was true but could not find name"))
    )(
      individual =>
        if (bpr.individual.exists(
              individualFound =>
                individualFound.firstName === individual.firstName && individualFound.lastName === individual.lastName
            )) {
          Ok(Json.toJson(bpr))
        } else {
          NotFound(bprErrorResponse("NOT_FOUND", "The remote endpoint has indicated that no data can be found"))
        }
    )

  def errorResponse(errorCode: String, errorMessage: String): JsValue =
    Json.toJson(DesErrorResponse(errorCode, errorMessage))

  def bprGen(isAnIndividual: Boolean, id: Either[SAUTR, NINO]): Gen[DesBusinessPartnerRecord] = {
    val addressGen: Gen[DesAddress] = for {
      addressLines <- Gen.ukAddress
      postcode     <- Gen.postcode
    } yield {
      val (l1, l2) = addressLines match {
        case Nil           => ("1 the Street", None)
        case a1 :: Nil     => (a1, None)
        case a1 :: a2 :: _ => (a1, Some(a2))
      }

      val (l3, countryCode) = id match {
        case Right(nino) if nino.value.contains("111111") => Some("Somewhere outside of the UK") -> nino.value.take(2)
        case _                                            => None                                -> "GB"
      }

      DesAddress(l1, l2, l3, None, postcode, countryCode)
    }

    for {
      address          <- addressGen
      sapNumber        <- Gen.listOfN(10, Gen.numChar).map(_.mkString(""))
      organisationName <- Gen.company
      forename         <- Gen.forename()
      surname          <- Gen.surname
    } yield {
      val email = {
        val local =
          if (isAnIndividual) s"${forename.toLowerCase}.${surname.toLowerCase}"
          else organisationName.replaceAllLiterally(" ", ".").toLowerCase
        s"$local@email.com"
      }
      val organisation = if (isAnIndividual) None else Some(DesOrganisation(organisationName))
      val individual   = if (isAnIndividual) Some(DesIndividual(forename, surname)) else None
      DesBusinessPartnerRecord(address, DesContactDetails(Some(email)), SapNumber(sapNumber), organisation, individual)
    }
  }

}

object BusinessPartnerRecordController {

  final case class Individual(firstName: String, lastName: String)

  final case class BprRequest(
    regime: String,
    requiresNameMatch: Boolean,
    isAnAgent: Boolean,
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
    organisation: Option[DesOrganisation],
    individual: Option[DesIndividual]
  )

  object DesBusinessPartnerRecord {

    final case class DesOrganisation(
      name: String
    )

    final case class DesIndividual(
      firstName: String,
      lastName: String
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
    implicit val individualWrites: Writes[DesIndividual]         = Json.writes[DesIndividual]
    implicit val addressWrites: Writes[DesAddress]               = Json.writes[DesAddress]
    implicit val contactDetailsWrites: Writes[DesContactDetails] = Json.writes[DesContactDetails]
    implicit val bprWrites: Writes[DesBusinessPartnerRecord]     = Json.writes[DesBusinessPartnerRecord]
  }

  def bprErrorResponse(errorCode: String, errorMessage: String): JsValue =
    Json.toJson(DesErrorResponse(errorCode, errorMessage))

}
