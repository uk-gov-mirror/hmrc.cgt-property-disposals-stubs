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

import play.api.libs.json.Json
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.SubscriptionUpdateRequest.SubscriptionUpdateDetails

case class SubscriptionUpdateRequest(
  regime: String,
  subscriptionDetails: SubscriptionUpdateDetails
)

object SubscriptionUpdateRequest {
  final case class SubscriptionUpdateAddressDetails(
    addressLine1: String,
    addressLine2: Option[String],
    addressLine3: Option[String],
    addressLine4: Option[String],
    postalCode: Option[String],
    countryCode: String
  )

  object SubscriptionUpdateAddressDetails {
    implicit val format = Json.format[SubscriptionUpdateAddressDetails]
  }

  final case class SubscriptionUpdateDetails(
    individual: Option[DesIndividual],
    trustee: Option[DesTrustee],
    addressDetails: SubscriptionUpdateAddressDetails,
    contactDetails: DesContactDetails
  )

  object SubscriptionUpdateDetails {
    implicit val format = Json.format[SubscriptionUpdateDetails]
  }

  implicit val format = Json.format[SubscriptionUpdateRequest]

}
