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

package uk.gov.hmrc.cgtpropertydisposalsstubs.models

import java.time.{LocalDate, LocalDateTime}

import play.api.libs.json.{Format, Json}

final case class PPDReturnResponseDetails(
  chargeType: Option[String],
  chargeReference: Option[String],
  amount: Option[BigDecimal],
  dueDate: Option[LocalDate],
  formBundleNumber: Option[String],
  cgtReferenceNumber: Option[String]
)

final case class DesReturnResponse(
  processingDate: LocalDateTime,
  ppdReturnResponseDetails: PPDReturnResponseDetails
)

object DesReturnResponse {
  implicit val ppdReturnResponseDetailsFormat: Format[PPDReturnResponseDetails] = Json.format[PPDReturnResponseDetails]
  implicit val desReturnResponseFormat: Format[DesReturnResponse]               = Json.format[DesReturnResponse]
}
