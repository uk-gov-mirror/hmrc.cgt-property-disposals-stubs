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

import cats.syntax.either._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.mvc.Results._
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController.DesBusinessPartnerRecord.{DesAddress, DesContactDetails, DesIndividual, DesOrganisation}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController.{DesBusinessPartnerRecord, DesErrorResponse, bprErrorResponse}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.SubscriptionController.SubscriptionResponse
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.{NINO, SAUTR, SapNumber}

case class Profile(
                    predicate: Either[SAUTR,NINO] => Boolean,
                    bprResponse:  Either[Result, DesBusinessPartnerRecord],
                    subscriptionResponse: Option[Either[Result, SubscriptionResponse]]
)

object SubscriptionProfiles {

  implicit class EitherOps[A,B](val e: Either[A,B]) extends AnyVal {

    def isRightAnd(p: B => Boolean): Boolean = e.exists(p)

    def isLeftAnd(p: A => Boolean): Boolean = e.swap.exists(p)

  }

  def getProfile(id: Either[SAUTR,NINO]): Option[Profile] =
    profiles.find(_.predicate(id))

  def getProfile(sapNumber: SapNumber): Option[Profile] =
    profiles.find(_.bprResponse.exists(_.sapNumber == sapNumber))

  private val profiles: List[Profile] = {
    def bpr(sapNumber: SapNumber) = DesBusinessPartnerRecord(
      DesAddress("3rd Wick Street", None, None, None, "JW123ST", "GB"),
      DesContactDetails(Some("testCGT@email.com")),
      sapNumber,
      None,
      Some(DesIndividual("John", "Wick"))
    )

    val subscriptionResponse = SubscriptionResponse("XACGTP123456789")

    val (lukeBishopContactDetails, lukeBishopBpr) = {
      val contactDetails = DesContactDetails(Some("luke.bishop@email.com"))

      contactDetails -> DesBusinessPartnerRecord(
        DesAddress("65 Tuckers Road", Some("North London"), None, None, "NR38 3EX", "GB"),
        contactDetails,
        SapNumber("0100042628"),
        None,
        Some(DesIndividual("Luke", "Bishop"))
      )
    }


    List(
      Profile(
        _ === Right(NINO("CG123456D")),
        Right(bpr(SapNumber("1234567890"))),
        Some(Right(subscriptionResponse))
      ),
      Profile(
        _ === Right(NINO("AB123456C")),
        Right(lukeBishopBpr),
        Some(Right(SubscriptionResponse("XYCGTP001000170")))
      ),
      Profile(
        _.isRightAnd(_.value.startsWith("EM000")),
        Right(lukeBishopBpr.copy(
          contactDetails = lukeBishopContactDetails.copy(emailAddress = None))),
        None
      ),
      Profile(
        _.isLeftAnd(_.value.endsWith("89")),
        Right(lukeBishopBpr.copy(
          contactDetails = lukeBishopContactDetails.copy(emailAddress = None),
          organisation = Some(DesOrganisation("Plip Plop Trusts")),
          individual = None
        )),
        None
      ),
      Profile(
        _.isLeftAnd(_.value.endsWith("99")),
        Right(lukeBishopBpr.copy(
          contactDetails = lukeBishopContactDetails.copy(emailAddress = None)
        )),
        None
      ),
      Profile(
        id => id.isRightAnd(_.value.startsWith("ER400")) || id.isLeftAnd(_.value.endsWith("5400")),
        Left(
          BadRequest(bprErrorResponse("INVALID_NINO", "Submission has not passed validation. Invalid parameter NINO"))
        ),
        None
      ),
      Profile(
        id => id.isRightAnd(_.value.startsWith("ER404")) || id.isLeftAnd(_.value.endsWith("5404")),
        Left(NotFound(bprErrorResponse("NOT_FOUND", "The remote endpoint has indicated that no data can be found"))),
        None
      ),
      Profile(
        id => id.isRightAnd(_.value.startsWith("ER409")) || id.isLeftAnd(_.value.endsWith("5409")),
        Left(Conflict(bprErrorResponse("CONFLICT", "The remote endpoint has indicated Duplicate Submission"))),
        None
      ),
      Profile(
        id => id.isRightAnd(_.value.startsWith("ER500")) || id.isLeftAnd(_.value.endsWith("5500")),
        Left(
          InternalServerError(
            bprErrorResponse(
              "SERVER_ERROR",
              "DES is currently experiencing problems that require live service intervention"
            )
          )
        ),
        None
      ),
      Profile(
        id => id.isRightAnd(_.value.startsWith("ER503")) || id.isLeftAnd(_.value.endsWith("5503")),
        Left(
          ServiceUnavailable(bprErrorResponse("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding"))
        ),
        None
      ),
      Profile(_.isRightAnd(_.value.startsWith("ES400")), Right(bpr(SapNumber("0000000400"))), Some(Left(BadRequest))),
      Profile(_.isRightAnd(_.value.startsWith("ES404")), Right(bpr(SapNumber("0000000404"))), Some(Left(NotFound))),
      Profile(_.isRightAnd(_.value.startsWith("ES409")), Right(bpr(SapNumber("0000000409"))), Some(Left(Conflict))),
      Profile(_.isRightAnd(_.value.startsWith("ES500")), Right(bpr(SapNumber("0000000500"))), Some(Left(InternalServerError))),
      Profile(_.isRightAnd(_.value.startsWith("ES503")), Right(bpr(SapNumber("0000000503"))), Some(Left(ServiceUnavailable)))
    )
  }




}
