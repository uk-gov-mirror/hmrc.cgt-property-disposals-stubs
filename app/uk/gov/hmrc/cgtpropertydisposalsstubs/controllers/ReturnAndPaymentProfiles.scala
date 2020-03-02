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

import java.time.LocalDate

import uk.gov.hmrc.cgtpropertydisposalsstubs.models.DesListReturnsResponse.{Charge, ReturnSummary}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.{DesAddressDetails, DesFinancialTransactionItem, FinancialDataResponse, FinancialTransaction}

object ReturnAndPaymentProfiles {

  final case class Profile(returnSummary: ReturnSummary, financialData: List[FinancialTransaction])

  private val profile1 = {
    val chargeReference = "XCRG1111111111"
    Profile(
      ReturnSummary(
        "000000000001",
        LocalDate.of(2020, 2, 1),
        LocalDate.of(2020, 1, 25),
        None,
        "2019",
        None,
        BigDecimal("14.99"),
        BigDecimal("14.99"),
        DesAddressDetails("2 Not sure Where", Some("Don't know what I'm doing"), None, None, "ZZ0 0ZZ", "GB"),
        List(
          Charge("CGT PPD Return UK Resident", BigDecimal("14.99"), LocalDate.of(2020, 2, 24), chargeReference)
        )
      ),
      List(
        FinancialTransaction(
          chargeReference,
          BigDecimal("14.99"),
          BigDecimal("14.99"),
          None
        )
      )
    )
  }

  private val profile2 = {
    val chargeReference = "XCRG2222222222"

    Profile(
      ReturnSummary(
        "000000000002",
        LocalDate.of(2020, 2, 1),
        LocalDate.of(2020, 1, 24),
        None,
        "2019",
        None,
        BigDecimal("9.99"),
        BigDecimal("0"),
        DesAddressDetails("14 Something Something Something", Some("That Other Place"), None, None, "ZZ0 0ZZ", "GB"),
        List(
          Charge(
            "CGT PPD Return UK Resident",
            BigDecimal("0"),
            LocalDate.of(2020, 2, 23),
            chargeReference
          )
        )
      ),
      List(
        FinancialTransaction(
          chargeReference,
          BigDecimal("0"),
          BigDecimal("0"),
          None
        )
      )
    )
  }

  private val profile3 = {
    val originalChargeReference = "XCRG3333333333"
    val penaltyChargeReference  = "XCRG4444444444"

    Profile(
      ReturnSummary(
        "000000000003",
        LocalDate.of(2020, 2, 1),
        LocalDate.of(2020, 1, 24),
        None,
        "2019",
        None,
        BigDecimal("9.99"),
        BigDecimal("1000000"),
        DesAddressDetails("14 Something Something Something", Some("That Other Place"), None, None, "ZZ0 0ZZ", "GB"),
        List(
          Charge(
            "CGT PPD Return UK Resident",
            BigDecimal("9.99"),
            LocalDate.of(2020, 2, 23),
            originalChargeReference
          ),
          Charge(
            "CGT PPD Late Filing penalty",
            BigDecimal("1000005"),
            LocalDate.of(2020, 2, 27),
            penaltyChargeReference
          )
        )
      ),
      List(
        FinancialTransaction(
          originalChargeReference,
          BigDecimal("9.99"),
          BigDecimal("5"),
          Some(
            List(
              DesFinancialTransactionItem(
                BigDecimal("4.99"),
                LocalDate.of(2020, 2, 23)
              )
            )
          )
        ),
        FinancialTransaction(
          penaltyChargeReference,
          BigDecimal("1000005"),
          BigDecimal("1000000"),
          Some(
            List(
              DesFinancialTransactionItem(
                BigDecimal("2"),
                LocalDate.of(2020, 2, 24)
              ),
              DesFinancialTransactionItem(
                BigDecimal("3"),
                LocalDate.of(2020, 2, 23)
              )
            )
          )
        )
      )
    )
  }

  val profiles = List(profile1, profile2, profile3)

}
