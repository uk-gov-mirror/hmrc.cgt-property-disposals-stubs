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

import java.time.LocalDateTime

import cats.implicits._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.mvc.Results._
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.BusinessPartnerRecordController.DesErrorResponse
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.SubscriptionController.SubscriptionUpdateResponse

case class SubscriptionUpdateProfile(
  predicate: String => Boolean,
  subscriptionUpdateResponse: Either[Result, SubscriptionUpdateResponse]
)

object SubscriptionUpdateProfiles {

  def updateSubscriptionDetails(id: String): Option[SubscriptionUpdateProfile] =
    updates.find(_.predicate(id))

  val desUpdate = SubscriptionUpdateResponse(
    "CGT",
    LocalDateTime.of(2019, 10, 23, 11, 34, 23).toString,
    "01234567891",
    "XACGTP123456701",
    "GB",
    Some("TF34NT")
  )

  private val updates = List(
    SubscriptionUpdateProfile(
      _ === "XACGTP123456711",
      Right(desUpdate)
    ),
    SubscriptionUpdateProfile(
      _ === "XACGTP123456712",
      Left(
        BadRequest(
          desErrorResponse("INVALID_REGIME", "Submission has not passed validation. Invalid parameter regimeValue")
        )
      )
    ),
    SubscriptionUpdateProfile(
      _ === "XACGTP123456713",
      Left(
        BadRequest(desErrorResponse("INVALID_IDTYPE", "Submission has not passed validation. Invalid parameter idType"))
      )
    ),
    SubscriptionUpdateProfile(
      _ === "XACGTP123456714",
      Left(
        BadRequest(
          desErrorResponse("INVALID_IDVALUE", "Submission has not passed validation. Invalid parameter idValue")
        )
      )
    ),
    SubscriptionUpdateProfile(
      _ === "XACGTP123456715",
      Left(
        BadRequest(
          desErrorResponse(
            "INVALID_REQUEST",
            "Submission has not passed validation. Your request contains inconsistent data"
          )
        )
      )
    ),
    SubscriptionUpdateProfile(
      _ === "XACGTP123456716",
      Left(
        BadRequest(
          desErrorResponse(
            "INVALID_CORRELATIONID",
            "Submission has not passed validation. Invalid header CorrelationId"
          )
        )
      )
    ),
    SubscriptionUpdateProfile(
      _ === "XACGTP123456717",
      Left(
        BadRequest(
          desErrorResponse(
            "INVALID_PAYLOAD",
            "Submission has not passed validation. Invalid PAYLOAD"
          )
        )
      )
    ),
    SubscriptionUpdateProfile(
      _ === "XACGTP123456718",
      Left(
        NotFound(
          desErrorResponse(
            "NOT_FOUND",
            "Data not found for the provided Registration Number"
          )
        )
      )
    ),
    SubscriptionUpdateProfile(
      _ === "XACGTP123456719",
      Left(
        InternalServerError(
          desErrorResponse(
            "SERVER_ERROR",
            "DES is currently experiencing problems that require live service intervention"
          )
        )
      )
    ),
    SubscriptionUpdateProfile(
      _ === "XACGTP123456720",
      Left(
        ServiceUnavailable(
          desErrorResponse(
            "SERVICE_UNAVAILABLE",
            "Dependent systems are currently not responding"
          )
        )
      )
    )
  )

  private def desErrorResponse(code: String, reason: String): JsValue = Json.toJson(DesErrorResponse(code, reason))
}
