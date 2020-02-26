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

import java.time.LocalDate

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.DesReturn._

case class DesReturn(
  returnType: String,
  returnDetails: ReturnDetails,
  representedPersonDetails: Option[RepresentedPersonDetails],
  disposalDetails: List[DisposalDetails],
  lossSummaryDetails: LossSummaryDetails,
  incomeAllowanceDetails: IncomeAllowanceDetails,
  reliefDetails: ReliefDetails
)

object DesReturn {

  final case class ValueAtTaxBandDetails(
    taxRate: BigDecimal,
    valueAtTaxRate: BigDecimal
  )

  final case class ReturnDetails(
    customerType: String,
    completionDate: LocalDate,
    isUKResident: Boolean,
    numberDisposals: Int,
    totalTaxableGain: BigDecimal,
    totalLiability: BigDecimal,
    totalYTDLiability: BigDecimal,
    estimate: Boolean,
    repayment: Boolean,
    attachmentUpload: Boolean,
    declaration: Boolean,
    countryResidence: Option[String],
    attachmentID: Option[String],
    entrepreneursRelief: Option[BigDecimal],
    valueAtTaxBandDetails: Option[List[ValueAtTaxBandDetails]],
    totalNetLoss: Option[BigDecimal],
    adjustedAmount: Option[BigDecimal]
  )

  final case class RepresentedPersonDetails(
    capacitorPersonalRep: String,
    firstName: String,
    lastName: String,
    idType: String,
    idValue: String,
    dateOfBirth: Option[String],
    trustCessationDate: Option[String],
    trustTerminationDate: Option[String],
    addressDetails: Option[DesAddressDetails],
    email: Option[String]
  )

  final case class ReliefDetails(
    reliefs: Boolean,
    privateResRelief: Option[BigDecimal],
    lettingsRelief: Option[BigDecimal],
    giftHoldOverRelief: Option[BigDecimal],
    otherRelief: Option[String],
    otherReliefAmount: Option[BigDecimal]
  )

  final case class LossSummaryDetails(
    inYearLoss: Boolean,
    preYearLoss: Boolean,
    inYearLossUsed: Option[BigDecimal],
    preYearLossUsed: Option[BigDecimal]
  )

  final case class IncomeAllowanceDetails(
    annualExemption: BigDecimal,
    estimatedIncome: Option[BigDecimal],
    personalAllowance: Option[BigDecimal],
    threshold: Option[BigDecimal]
  )

  final case class DisposalDetails(
    disposalDate: LocalDate,
    addressDetails: DesAddressDetails,
    assetType: String,
    acquisitionType: String,
    landRegistry: Boolean,
    acquisitionPrice: BigDecimal,
    rebased: Boolean,
    disposalPrice: BigDecimal,
    improvements: Boolean,
    percentOwned: Option[BigDecimal],
    acquiredDate: Option[LocalDate],
    rebasedAmount: Option[BigDecimal],
    disposalType: Option[String],
    improvementCosts: Option[BigDecimal],
    acquisitionFees: Option[BigDecimal],
    disposalFees: Option[BigDecimal],
    initialGain: Option[BigDecimal],
    initialLoss: Option[BigDecimal]
  )

  implicit val representedPersonDetailsFormat: OFormat[RepresentedPersonDetails] = Json.format
  implicit val valueAtTaxBandDetailsFormat: OFormat[ValueAtTaxBandDetails]       = Json.format
  implicit val returnDetailsFormat: OFormat[ReturnDetails]                       = Json.format
  implicit val reliefDetailsFormat: OFormat[ReliefDetails]                       = Json.format
  implicit val lossSummaryDetailsFormat: OFormat[LossSummaryDetails]             = Json.format
  implicit val incomeAllowanceDetailsFormat: OFormat[IncomeAllowanceDetails]     = Json.format
  implicit val disposalDetailsFormat: OFormat[DisposalDetails]                   = Json.format
  implicit val desReturnFormat: OFormat[DesReturn]                               = Json.format

}
