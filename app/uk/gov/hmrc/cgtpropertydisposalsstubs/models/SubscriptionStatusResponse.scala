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

package uk.gov.hmrc.cgtpropertydisposalsstubs.models

import play.api.libs.json.{JsString, Json, Writes}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.SubscriptionStatusResponse.SubscriptionStatus

final case class SubscriptionStatusResponse(subscriptionStatus: SubscriptionStatus,
                                            idType: Option[String] = None,
                                            idValue: Option[String] = None
                                           )

object SubscriptionStatusResponse {

  sealed trait SubscriptionStatus extends Product with Serializable {
    val value: String
  }

  object SubscriptionStatus {

    final case object NotSubscribed extends SubscriptionStatus {
      val value: String = "NO_FORM_BUNDLE_FOUND"
    }

    final case object Subscribed extends SubscriptionStatus {
      val value: String = "SUCCESSFUL"
    }

    final case object RegistrationFormReceived extends SubscriptionStatus {
      val value: String = "REG_FORM_RECEIVED"
    }

    final case object SentToDs extends SubscriptionStatus {
      val value: String = "SENT_TO_DS"
    }

    final case object DsOutcomeInProgress extends SubscriptionStatus {
      val value: String = "DS_OUTCOME_IN_PROGRESS"
    }

    final case object Rejected extends SubscriptionStatus {
      val value: String = "REJECTED"
    }

    final case object InProcessing extends SubscriptionStatus {
      val value: String = "IN_PROCESSING"
    }

    final case object CreateFailed extends SubscriptionStatus {
      val value: String = "CREATE_FAILED"
    }

    final case object Withdrawal extends SubscriptionStatus {
      val value: String = "WITHDRAWAL"
    }

    final case object SentToRcm extends SubscriptionStatus {
      val value: String = "SENT_TO_RCM"
    }

    final case object ApprovedWithConditions extends SubscriptionStatus {
      val value: String = "APPROVED_WITH_CONDITIONS"
    }

    final case object Revoked extends SubscriptionStatus {
      val value: String = "REVOKED"
    }

    final case object Deregistered extends SubscriptionStatus {
      val value: String = "DE-REGISTERED"
    }

    final case object ContractObjectInactive extends SubscriptionStatus {
      val value: String = "CONTRACT_OBJECT_INACTIVE"
    }

    implicit val writes: Writes[SubscriptionStatus] =
      Writes(status => JsString(status.value)

    )

  }

  implicit val writes: Writes[SubscriptionStatusResponse] = Json.writes[SubscriptionStatusResponse]

}
