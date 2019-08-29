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
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController.DesBusinessPartnerRecord.{DesAddress, DesContactDetails}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController.{DesBusinessPartnerRecord, DesErrorResponse}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.SubscriptionController.SubscriptionResponse
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.SubscriptionProfiles.NINO

case class Profile(
  ninoPredicate: NINO => Boolean,
  bprResponse: Either[Result, DesBusinessPartnerRecord],
  subscriptionResponse: Option[Either[Result, SubscriptionResponse]]
)

object SubscriptionProfiles {

  type NINO = String

  type SapNumber = String

  def getProfile(id: Either[NINO, SapNumber]): Option[Profile] = id match {
    case Left(nino)       => profiles.find(_.ninoPredicate(nino))
    case Right(sapNumber) => profiles.find(_.bprResponse.exists(_.sapNumber == sapNumber))
  }

  private val profiles: List[Profile] = {
    def bpr(sapNumber: String) = DesBusinessPartnerRecord(
      DesAddress("3rd Wick Street", None, None, None, "JW123ST", "GB"),
      DesContactDetails(Some("testCGT@email.com")),
      sapNumber
    )

    val subscriptionResponse = SubscriptionResponse("XACGTP123456789")

    val (lukeBishopContactDetails, lukeBishopBpr) = {
      val contactDetails = DesContactDetails(Some("luke.bishop@email.com"))

      contactDetails -> DesBusinessPartnerRecord(
        DesAddress("65 Tuckers Road", Some("North London"), None, None, "NR38 3EX", "GB"),
        contactDetails,
        "0100042628"
      )
    }

    def bprErrorResponse(errorCode: String, errorMessage: String): JsValue =
      Json.toJson(DesErrorResponse(errorCode, errorMessage))

    List(
      Profile(_ == "CG123456D", Right(bpr("1234567890")), Some(Right(subscriptionResponse))),
      Profile(_ == "AB123456C", Right(lukeBishopBpr), Some(Right(SubscriptionResponse("XYCGTP001000170")))),
      Profile(
        _.startsWith("EM000"),
        Right(lukeBishopBpr.copy(contactDetails = lukeBishopContactDetails.copy(emailAddress = None))),
        None
      ),
      Profile(
        _.startsWith("ER400"),
        Left(
          BadRequest(bprErrorResponse("INVALID_NINO", "Submission has not passed validation. Invalid parameter NINO"))
        ),
        None
      ),
      Profile(
        _.startsWith("ER404"),
        Left(NotFound(bprErrorResponse("NOT_FOUND", "The remote endpoint has indicated that no data can be found"))),
        None
      ),
      Profile(
        _.startsWith("ER409"),
        Left(Conflict(bprErrorResponse("CONFLICT", "The remote endpoint has indicated Duplicate Submission"))),
        None
      ),
      Profile(
        _.startsWith("ER500"),
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
        _.startsWith("ER503"),
        Left(
          ServiceUnavailable(bprErrorResponse("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding"))
        ),
        None
      ),
      Profile(_.startsWith("ES400"), Right(bpr("0000000400")), Some(Left(BadRequest))),
      Profile(_.startsWith("ES404"), Right(bpr("0000000404")), Some(Left(NotFound))),
      Profile(_.startsWith("ES409"), Right(bpr("0000000409")), Some(Left(Conflict))),
      Profile(_.startsWith("ES500"), Right(bpr("0000000500")), Some(Left(InternalServerError))),
      Profile(_.startsWith("ES503"), Right(bpr("0000000503")), Some(Left(ServiceUnavailable)))
    )
  }
}
