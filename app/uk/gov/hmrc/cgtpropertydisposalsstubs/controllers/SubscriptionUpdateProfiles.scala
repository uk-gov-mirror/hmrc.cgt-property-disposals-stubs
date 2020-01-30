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

import cats.syntax.eq._
import cats.instances.string._
import play.api.mvc.Result
import play.api.mvc.Results._
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.SubscriptionController.SubscriptionUpdateResponse
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.DesErrorResponse.desErrorResponseJson

case class SubscriptionUpdateProfile(
  predicate: String => Boolean,
  subscriptionUpdateResponse: Either[Result, SubscriptionUpdateResponse]
)

object SubscriptionUpdateProfiles {

  def updateSubscriptionDetails(id: String): Option[SubscriptionUpdateProfile] =
    updates.find(_.predicate(id))

  private val updates = List(
    SubscriptionUpdateProfile(
      _ === "XACGTP123456712",
      Left(
        BadRequest(
          desErrorResponseJson("INVALID_REGIME", "Submission has not passed validation. Invalid parameter regimeValue")
        )
      )
    ),
    SubscriptionUpdateProfile(
      _ === "XACGTP123456713",
      Left(
        BadRequest(
          desErrorResponseJson("INVALID_IDTYPE", "Submission has not passed validation. Invalid parameter idType")
        )
      )
    ),
    SubscriptionUpdateProfile(
      _ === "XACGTP123456714",
      Left(
        BadRequest(
          desErrorResponseJson("INVALID_IDVALUE", "Submission has not passed validation. Invalid parameter idValue")
        )
      )
    ),
    SubscriptionUpdateProfile(
      _ === "XACGTP123456715",
      Left(
        BadRequest(
          desErrorResponseJson(
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
          desErrorResponseJson(
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
          desErrorResponseJson(
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
          desErrorResponseJson(
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
          desErrorResponseJson(
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
          desErrorResponseJson(
            "SERVICE_UNAVAILABLE",
            "Dependent systems are currently not responding"
          )
        )
      )
    )
  )

}
