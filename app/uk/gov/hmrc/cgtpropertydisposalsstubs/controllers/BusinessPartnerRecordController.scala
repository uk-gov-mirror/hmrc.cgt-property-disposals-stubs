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

import akka.stream.Materializer
import cats.instances.string._
import cats.syntax.eq._
import com.google.inject.Inject
import org.scalacheck.Gen
import play.api.libs.json._
import play.api.mvc._
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController.DesBusinessPartnerRecord.{DesIndividual, DesOrganisation}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.{DesAddressDetails, NINO, SAUTR, SapNumber, TRN}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.DesErrorResponse.desErrorResponseJson
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import uk.gov.hmrc.smartstub.Enumerable.instances.ninoEnumNoSpaces
import uk.gov.hmrc.smartstub._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.util.Random

class BusinessPartnerRecordController @Inject() (cc: ControllerComponents)(
  implicit mat: Materializer,
  ec: ExecutionContext
) extends BackendController(cc)
    with Logging {

  import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController._
  import DesBusinessPartnerRecord._

  implicit val ninoToLong: ToLong[NINO]   = ninoEnumNoSpaces.imap(NINO(_))(_.value)
  implicit val sautrToLong: ToLong[SAUTR] = pattern"9999999999".imap(SAUTR(_))(_.value)
  implicit val trnToLong: ToLong[TRN] = new ToLong[TRN] {
    override def asLong(i: TRN): Long = i.value.filter(_.isDigit).toLong
  }

  implicit def eitherToLong[A, B](implicit a: ToLong[A], b: ToLong[B]): ToLong[Either[A, B]] =
    new ToLong[Either[A, B]] {
      override def asLong(i: Either[A, B]): Long = i.fold(a.asLong, b.asLong)
    }

  def getBusinessPartnerRecord(entityType: String, idType: String, idValue: String): Action[AnyContent] = Action {
    implicit request =>
      (entityType, idType) match {
        case ("individual", "nino")  => handleRequest(request, Right(NINO(idValue)), true)
        case ("individual", "utr")   => handleRequest(request, Left(Right(SAUTR(idValue))), true)
        case ("organisation", "utr") => handleRequest(request, Left(Right(SAUTR(idValue))), false)
        case ("organisation", "trn") => handleRequest(request, Left(Left(TRN(idValue))), false)
        case _ =>
          logger.warn(
            s"Received request for BPR for unsupported combination of entity type $entityType and " +
              s"id type '$idType' with value '$idValue'"
          )
          BadRequest
      }
  }

  private def handleRequest(
    request: Request[AnyContent],
    id: Either[Either[TRN, SAUTR], NINO],
    isAnIndividual: Boolean
  ): Result = {
    def getResult(bpr: DesBusinessPartnerRecord, bprRequest: BprRequest): Result =
      if (bprRequest.requiresNameMatch) {
        doNameMatch(bprRequest, isAnIndividual, bpr)
      } else {
        Ok(Json.toJson(bpr))
      }

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
            val result: Result =
              SubscriptionProfiles
                .getProfile(id)
                .map(_.bprResponse.map{ bpr =>
                  getResult(bpr, bprRequest)
                }.merge)
                .getOrElse {
                  val bpr = bprGen(isAnIndividual, id).seeded(id).get
                  getResult(bpr, bprRequest)
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
  }

  def doNameMatch(bprRequest: BprRequest, isAnIndividual: Boolean, bpr: DesBusinessPartnerRecord): Result = {
    def doNameMatch[A](requestField: Option[A])(nameMatches: A => Boolean): Result =
      requestField.fold(
        BadRequest(desErrorResponseJson("BAD_REQUEST", "requiresNameMatch was true but could not find name"))
      )(f =>
        if (nameMatches(f)) {
          Ok(Json.toJson(bpr))
        } else {
          NotFound(desErrorResponseJson("NOT_FOUND", "The remote endpoint has indicated that no data can be found"))
        }
      )

    if (isAnIndividual) {
      doNameMatch(bprRequest.individual)(requestIndividual =>
        bpr.individual.exists { individualFound =>
          val matches = individualFound.firstName === requestIndividual.firstName && individualFound.lastName === requestIndividual.lastName
          if (!matches)
            logger.info(
              s"Individual name in BPR request " +
                s"[firstName: '${requestIndividual.firstName}' lastName: '${requestIndividual.lastName}'] " +
                s"did not match the individual name in the BPR found " +
                s"[firstName: '${individualFound.firstName}', lastName: '${individualFound.lastName}']"
            )
          matches
        }
      )
    } else {
      doNameMatch(bprRequest.organisation) { requestOrganisation =>

        println(requestOrganisation)
        println(bprRequest.organisation)

        bpr.organisation.exists { organisationFound =>
          val matches = organisationFound.organisationName === requestOrganisation.organisationName
          println(matches)
          if (!matches)
            logger.info(
              s"Organisation name in BPR request '${requestOrganisation.organisationName}' " +
                s"did not match the organisation name in the BPR found ${organisationFound.organisationName}"
            )
          matches
        }
      }
    }
  }

  def bprGen(isAnIndividual: Boolean, id: Either[Either[TRN, SAUTR], NINO]): Gen[DesBusinessPartnerRecord] = {
    val addressGen: Gen[DesAddressDetails] = for {
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

      DesAddressDetails(l1, l2, l3, None, postcode, countryCode)
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

  final case class BprRequest(
    regime: String,
    requiresNameMatch: Boolean,
    isAnAgent: Boolean,
    individual: Option[DesIndividual],
    organisation: Option[DesOrganisation]
  )

  import DesBusinessPartnerRecord._

  final case class DesBusinessPartnerRecord(
    address: DesAddressDetails,
    contactDetails: DesContactDetails,
    sapNumber: SapNumber,
    organisation: Option[DesOrganisation],
    individual: Option[DesIndividual]
  )

  object DesBusinessPartnerRecord {

    final case class DesOrganisation(
      organisationName: String
    )

    final case class DesIndividual(
      firstName: String,
      lastName: String
    )

    final case class DesContactDetails(emailAddress: Option[String])

    implicit val organisationWrites: Format[DesOrganisation]     = Json.format[DesOrganisation]
    implicit val individualWrites: Format[DesIndividual]         = Json.format[DesIndividual]
    implicit val contactDetailsWrites: Writes[DesContactDetails] = Json.writes[DesContactDetails]
    implicit val bprWrites: Writes[DesBusinessPartnerRecord]     = Json.writes[DesBusinessPartnerRecord]
    implicit val bprRequestReads: Reads[BprRequest]              = Json.reads[BprRequest]
  }

}
