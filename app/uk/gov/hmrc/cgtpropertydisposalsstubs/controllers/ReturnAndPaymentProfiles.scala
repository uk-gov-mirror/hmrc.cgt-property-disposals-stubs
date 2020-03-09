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
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.{DesAddressDetails, DesFinancialTransactionItem, FinancialTransaction}

object ReturnAndPaymentProfiles {

  final case class ReturnProfile(returnSummary: ReturnSummary, financialData: List[FinancialTransaction])

  final case class AccountProfile(cgtReferencePredicate: String => Boolean, returns: List[ReturnProfile])

  val account1: AccountProfile = {
    val return1 = {
      val chargeReference = "XCRG1111111111"
      ReturnProfile(
        ReturnSummary(
          "000000000001",
          LocalDate.of(2020, 2, 1),
          LocalDate.of(2020, 1, 25),
          None,
          "2019",
          DesAddressDetails("1 Similar Place", Some("Don't know what I'm doing"), None, None, "SA12 1AX", "GB"),
          List(
            Charge("CGT PPD Return UK Resident", LocalDate.of(2020, 2, 24), chargeReference)
          )
        ),
        List(
          FinancialTransaction(
            chargeReference,
            BigDecimal("23520"),
            BigDecimal("0"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("23520"),
                  "TPS RECEIPTS BY DEBIT CARD",
                  LocalDate.of(2020, 2, 23)
                )
              )
            )
          )
        )
      )
    }

    val return2 = {
      val chargeReference = "XCRG1111111112"
      ReturnProfile(
        ReturnSummary(
          "000000000002",
          LocalDate.of(2020, 2, 1),
          LocalDate.of(2020, 1, 25),
          None,
          "2019",
          DesAddressDetails("1 Similar Place", Some("Don't know what I'm doing"), None, None, "SA12 1AX", "GB"),
          List(
            Charge("CGT PPD Return UK Resident", LocalDate.of(2020, 2, 24), chargeReference)
          )
        ),
        List(
          FinancialTransaction(
            chargeReference,
            BigDecimal("23520"),
            BigDecimal("23520"),
            None
          )
        )
      )
    }

    val return3 = {
      val originalChargeReference = "XCRG3333333333"
      val penaltyChargeReference  = "XCRG4444444444"

      ReturnProfile(
        ReturnSummary(
          "000000000003",
          LocalDate.of(2020, 2, 1),
          LocalDate.of(2020, 1, 24),
          None,
          "2019",
          DesAddressDetails("14 Something Something Something", Some("That Other Place"), None, None, "ZZ0 0ZZ", "GB"),
          List(
            Charge(
              "CGT PPD Return UK Resident",
              LocalDate.of(2020, 10, 5),
              originalChargeReference
            ),
            Charge(
              "CGT PPD Late Filing Penalty",
              LocalDate.of(2022, 1, 31),
              penaltyChargeReference
            )
          )
        ),
        List(
          FinancialTransaction(
            originalChargeReference,
            BigDecimal("1000"),
            BigDecimal("1000"),
            Some(
              List(
                )
            )
          ),
          FinancialTransaction(
            penaltyChargeReference,
            BigDecimal("680"),
            BigDecimal("680"),
            Some(List())
          )
        )
      )
    }

    val return4 = {
      val originalChargeReference = "XCRG3333333334"
      val penaltyChargeReference  = "XCRG4444444445"

      ReturnProfile(
        ReturnSummary(
          "000000000004",
          LocalDate.of(2020, 2, 1),
          LocalDate.of(2020, 1, 24),
          None,
          "2019",
          DesAddressDetails("14 Something Something Something", Some("That Other Place"), None, None, "ZZ0 0ZZ", "GB"),
          List(
            Charge(
              "CGT PPD Return UK Resident",
              LocalDate.of(2020, 10, 5),
              originalChargeReference
            ),
            Charge(
              "CGT PPD Late Filing Penalty",
              LocalDate.of(2022, 1, 31),
              penaltyChargeReference
            )
          )
        ),
        List(
          FinancialTransaction(
            originalChargeReference,
            BigDecimal("1000"),
            BigDecimal("350"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("650"),
                  "TPS RECEIPTS BY DEBIT CARD",
                  LocalDate.of(2020, 2, 23)
                )
              )
            )
          ),
          FinancialTransaction(
            penaltyChargeReference,
            BigDecimal("680"),
            BigDecimal("680"),
            Some(List())
          )
        )
      )
    }

    val return5 = {
      val originalChargeReference = "XCRG3333333335"
      val penaltyChargeReference  = "XCRG4444444446"

      ReturnProfile(
        ReturnSummary(
          "000000000005",
          LocalDate.of(2020, 2, 1),
          LocalDate.of(2020, 1, 24),
          None,
          "2019",
          DesAddressDetails("14 Something Something Something", Some("That Other Place"), None, None, "ZZ0 0ZZ", "GB"),
          List(
            Charge(
              "CGT PPD Return UK Resident",
              LocalDate.of(2020, 10, 5),
              originalChargeReference
            ),
            Charge(
              "CGT PPD Late Filing Penalty",
              LocalDate.of(2022, 1, 31),
              penaltyChargeReference
            )
          )
        ),
        List(
          FinancialTransaction(
            originalChargeReference,
            BigDecimal("1000"),
            BigDecimal("0"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("1000"),
                  "TPS RECEIPTS BY DEBIT CARD",
                  LocalDate.of(2020, 2, 23)
                )
              )
            )
          ),
          FinancialTransaction(
            penaltyChargeReference,
            BigDecimal("680"),
            BigDecimal("680"),
            Some(List())
          )
        )
      )
    }

    val return6 = {
      val originalChargeReference = "XCRG3333333336"
      val penaltyChargeReference  = "XCRG4444444447"

      ReturnProfile(
        ReturnSummary(
          "000000000006",
          LocalDate.of(2020, 2, 1),
          LocalDate.of(2020, 1, 24),
          None,
          "2019",
          DesAddressDetails("14 Something Something Something", Some("That Other Place"), None, None, "ZZ0 0ZZ", "GB"),
          List(
            Charge(
              "CGT PPD Return UK Resident",
              LocalDate.of(2020, 10, 5),
              originalChargeReference
            ),
            Charge(
              "CGT PPD Late Filing Penalty",
              LocalDate.of(2022, 1, 31),
              penaltyChargeReference
            )
          )
        ),
        List(
          FinancialTransaction(
            originalChargeReference,
            BigDecimal("1000"),
            BigDecimal("0"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("1000"),
                  "TPS RECEIPTS BY DEBIT CARD",
                  LocalDate.of(2020, 2, 23)
                )
              )
            )
          ),
          FinancialTransaction(
            penaltyChargeReference,
            BigDecimal("680"),
            BigDecimal("0"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("680"),
                  "TPS RECEIPTS BY DEBIT CARD",
                  LocalDate.of(2020, 2, 23)
                )
              )
            )
          )
        )
      )
    }

    val return7 = {
      val originalChargeReference = "XCRG3333333337"

      ReturnProfile(
        ReturnSummary(
          "000000000007",
          LocalDate.of(2020, 2, 1),
          LocalDate.of(2020, 1, 24),
          None,
          "2019",
          DesAddressDetails("14 Something Something Something", Some("That Other Place"), None, None, "ZZ0 0ZZ", "GB"),
          List(
            Charge(
              "CGT PPD Return UK Resident",
              LocalDate.of(2020, 10, 5),
              originalChargeReference
            )
          )
        ),
        List(
          FinancialTransaction(
            originalChargeReference,
            BigDecimal("0"),
            BigDecimal("0"),
            None
          )
        )
      )
    }

    AccountProfile(_.endsWith("1"), List(return1, return2, return3, return4, return5, return6, return7))
  }

  val account2: AccountProfile = {
    val return1 = {
      val chargeReference = "XCRG1111111111"
      ReturnProfile(
        ReturnSummary(
          "000000000001",
          LocalDate.of(2020, 2, 1),
          LocalDate.of(2020, 1, 25),
          None,
          "2019",
          DesAddressDetails("2 Not sure Where", Some("Don't know what I'm doing"), None, None, "ZZ0 0ZZ", "GB"),
          List(
            Charge("CGT PPD Return UK Resident", LocalDate.of(2020, 2, 24), chargeReference)
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
    AccountProfile(_.endsWith("0"), List(return1))

  }

  private val profiles: List[AccountProfile] = List(account1)

  def getProfile(cgtReference: String): Option[AccountProfile] =
    profiles.find(_.cgtReferencePredicate(cgtReference))

}
