/*
 * Copyright 2021 HM Revenue & Customs
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
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 25),
          None,
          "2020",
          DesAddressDetails("1 Similar Place", Some("Random Avenue"), Some("Ipswich"), None, Some("IP12 1AX"), "GB"),
          BigDecimal("23520"),
          Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(2020, 6, 24), chargeReference)
            )
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
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2020, 6, 24))
                ),
                DesFinancialTransactionItem(
                  BigDecimal("23520"),
                  Some("TPS RECEIPTS BY DEBIT CARD"),
                  Some(LocalDate.of(2020, 5, 25)),
                  Some("Reversal"),
                  Some(LocalDate.of(2020, 6, 24))
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
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 25),
          None,
          "2020",
          DesAddressDetails("Acme Ltd", Some("1 Similar Place"), Some("Southampton"), None, Some("S12 1AX"), "GB"),
          BigDecimal("23520"),
          Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(2020, 6, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference,
            BigDecimal("23520"),
            BigDecimal("23520"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("23520"),
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2020, 6, 24))
                )
              )
            )
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
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 24),
          None,
          "2020",
          DesAddressDetails(
            "14 Something Something Something",
            Some("That Other Place"),
            None,
            None,
            Some("ZZ0 0ZZ"),
            "GB"
          ),
          BigDecimal("1680"),
          Some(
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
          )
        ),
        List(
          FinancialTransaction(
            originalChargeReference,
            BigDecimal("1000"),
            BigDecimal("1000"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("1000"),
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2020, 10, 5))
                )
              )
            )
          ),
          FinancialTransaction(
            penaltyChargeReference,
            BigDecimal("680"),
            BigDecimal("680"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("680"),
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2022, 1, 31))
                )
              )
            )
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
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 24),
          None,
          "2020",
          DesAddressDetails(
            "14 Something Something Something",
            Some("That Other Place"),
            None,
            None,
            Some("ZZ0 0ZZ"),
            "GB"
          ),
          BigDecimal("1680"),
          Some(
            List(
              Charge(
                "CGT PPD Return UK Resident",
                LocalDate.of(2020, 10, 5),
                originalChargeReference
              ),
              Charge(
                "CGT PPD Late Filing Penalty",
                LocalDate.of(2022, 5, 31),
                penaltyChargeReference
              )
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
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2020, 10, 5))
                ),
                DesFinancialTransactionItem(
                  BigDecimal("650"),
                  Some("TPS RECEIPTS BY DEBIT CARD"),
                  Some(LocalDate.of(2020, 5, 25)),
                  Some("Outgoing Payment"),
                  Some(LocalDate.of(2020, 10, 5))
                )
              )
            )
          ),
          FinancialTransaction(
            penaltyChargeReference,
            BigDecimal("680"),
            BigDecimal("680"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("680"),
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2022, 5, 31))
                )
              )
            )
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
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 24),
          None,
          "2020",
          DesAddressDetails(
            "14 Something Something Something",
            Some("That Other Place"),
            None,
            None,
            Some("ZZ0 0ZZ"),
            "GB"
          ),
          BigDecimal("1680"),
          Some(
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
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2020, 10, 5))
                ),
                DesFinancialTransactionItem(
                  BigDecimal("1000"),
                  Some("TPS RECEIPTS BY DEBIT CARD"),
                  Some(LocalDate.of(2020, 5, 25)),
                  Some("Some Unknown Clearing Reason"),
                  Some(LocalDate.of(2020, 10, 5))
                )
              )
            )
          ),
          FinancialTransaction(
            penaltyChargeReference,
            BigDecimal("680"),
            BigDecimal("680"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("680"),
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2022, 1, 31))
                )
              )
            )
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
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 24),
          None,
          "2020",
          DesAddressDetails(
            "14 Something Something Something",
            Some("That Other Place"),
            None,
            None,
            Some("ZZ0 0ZZ"),
            "GB"
          ),
          BigDecimal("1680"),
          Some(
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
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2020, 10, 5))
                ),
                DesFinancialTransactionItem(
                  BigDecimal("1000"),
                  Some("TPS RECEIPTS BY DEBIT CARD"),
                  Some(LocalDate.of(2020, 5, 25)),
                  Some("Mass Write-Off"),
                  Some(LocalDate.of(2020, 10, 5))
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
                  Some("TPS RECEIPTS BY DEBIT CARD"),
                  Some(LocalDate.of(2020, 5, 25)),
                  Some("Automatic Clearing"),
                  Some(LocalDate.of(2022, 1, 31))
                )
              )
            )
          )
        )
      )
    }

    val return7 =
      ReturnProfile(
        ReturnSummary(
          "000000000007",
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 24),
          None,
          "2020",
          DesAddressDetails(
            "14 Something Something Something",
            Some("That Other Place"),
            None,
            None,
            Some("ZZ0 0ZZ"),
            "GB"
          ),
          BigDecimal("0"),
          None
        ),
        List.empty
      )

    val return8 = {
      val chargeReference = "XCRG9999999999"
      ReturnProfile(
        ReturnSummary(
          "000000000001",
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 25),
          None,
          "2020",
          DesAddressDetails("2 Similar Place", Some("Random Avenue"), Some("Ipswich"), None, Some("IP12 1AX"), "GB"),
          BigDecimal("43520"),
          Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(2020, 6, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference,
            BigDecimal("43520"),
            BigDecimal("0"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("43520"),
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2020, 6, 24))
                ),
                DesFinancialTransactionItem(
                  BigDecimal("43520"),
                  Some("Invalid Payment Method"),
                  Some(LocalDate.of(2020, 5, 25)),
                  Some("Write-Off"),
                  Some(LocalDate.of(2020, 6, 24))
                )
              )
            )
          )
        )
      )
    }

    AccountProfile(_.endsWith("1"), List(return1, return2, return3, return4, return5, return6, return7, return8))
  }

  val account2: AccountProfile = {
    val chargeReference = "XCRG1111111110"

    val return1 =
      ReturnProfile(
        ReturnSummary(
          "000000000001",
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 25),
          Some(LocalDate.of(2020, 6, 2)),
          "2020",
          DesAddressDetails("2 Not sure Where", Some("Don't know what I'm doing"), None, None, Some("ZZ0 0ZZ"), "GB"),
          BigDecimal("1725"),
          Some(
            List(
              Charge(
                "CGT PPD Return UK Resident",
                LocalDate.of(2022, 1, 31),
                chargeReference
              )
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference,
            BigDecimal(1000),
            BigDecimal(1000),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal(1000),
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2020, 7, 30))
                )
              )
            )
          ),
          FinancialTransaction(
            chargeReference,
            BigDecimal(725),
            BigDecimal(725),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal(725),
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2022, 1, 31))
                )
              )
            )
          )
        )
      )
    AccountProfile(_.startsWith("XD"), List(return1))

  }

  val account3: AccountProfile = {
    val return1 = {
      val chargeReference = "XCRG1111111111"
      ReturnProfile(
        ReturnSummary(
          "000000000001",
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 25),
          None,
          "2020",
          DesAddressDetails("1 Similar Place", Some("Random Avenue"), Some("Ipswich"), None, Some("IP12 1AX"), "GB"),
          BigDecimal("23520"),
          Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(2020, 6, 24), chargeReference)
            )
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
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2020, 6, 24))
                ),
                DesFinancialTransactionItem(
                  BigDecimal("23520"),
                  Some("TPS RECEIPTS BY DEBIT CARD"),
                  Some(LocalDate.of(2020, 5, 25)),
                  Some("Reversal"),
                  Some(LocalDate.of(2020, 6, 24))
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
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 25),
          None,
          "2020",
          DesAddressDetails("Acme Ltd", Some("1 Similar Place"), Some("Southampton"), None, Some("S12 1AX"), "GB"),
          BigDecimal("23520"),
          Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(2020, 6, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference,
            BigDecimal("23520"),
            BigDecimal("23520"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("23520"),
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2020, 6, 24))
                )
              )
            )
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
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 24),
          None,
          "2020",
          DesAddressDetails(
            "14 Something Something Something",
            Some("That Other Place"),
            None,
            None,
            Some("ZZ0 0ZZ"),
            "GB"
          ),
          BigDecimal("1680"),
          Some(
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
          )
        ),
        List(
          FinancialTransaction(
            originalChargeReference,
            BigDecimal("1000"),
            BigDecimal("1000"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("1000"),
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2020, 10, 5))
                )
              )
            )
          ),
          FinancialTransaction(
            penaltyChargeReference,
            BigDecimal("680"),
            BigDecimal("680"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("680"),
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2022, 1, 31))
                )
              )
            )
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
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 24),
          None,
          "2020",
          DesAddressDetails(
            "14 Something Something Something",
            Some("That Other Place"),
            None,
            None,
            Some("ZZ0 0ZZ"),
            "GB"
          ),
          BigDecimal("1680"),
          Some(
            List(
              Charge(
                "CGT PPD Return UK Resident",
                LocalDate.of(2020, 10, 19),
                originalChargeReference
              ),
              Charge(
                "CGT PPD Late Filing Penalty",
                LocalDate.of(2022, 5, 31),
                penaltyChargeReference
              )
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
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2020, 10, 19))
                ),
                DesFinancialTransactionItem(
                  BigDecimal("650"),
                  Some("TPS RECEIPTS BY DEBIT CARD"),
                  Some(LocalDate.of(2020, 5, 25)),
                  Some("Outgoing Payment"),
                  Some(LocalDate.of(2020, 10, 19))
                )
              )
            )
          ),
          FinancialTransaction(
            penaltyChargeReference,
            BigDecimal("680"),
            BigDecimal("680"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("680"),
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2022, 5, 31))
                )
              )
            )
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
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 24),
          None,
          "2020",
          DesAddressDetails(
            "14 Something Something Something",
            Some("That Other Place"),
            None,
            None,
            Some("ZZ0 0ZZ"),
            "GB"
          ),
          BigDecimal("1680"),
          Some(
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
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2020, 10, 5))
                ),
                DesFinancialTransactionItem(
                  BigDecimal("1000"),
                  Some("TPS RECEIPTS BY DEBIT CARD"),
                  Some(LocalDate.of(2020, 5, 25)),
                  Some("Some Unknown Clearing Reason"),
                  Some(LocalDate.of(2020, 10, 5))
                )
              )
            )
          ),
          FinancialTransaction(
            penaltyChargeReference,
            BigDecimal("680"),
            BigDecimal("680"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("680"),
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2022, 1, 31))
                )
              )
            )
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
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 24),
          None,
          "2020",
          DesAddressDetails(
            "14 Something Something Something",
            Some("That Other Place"),
            None,
            None,
            Some("ZZ0 0ZZ"),
            "GB"
          ),
          BigDecimal("1680"),
          Some(
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
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2020, 10, 5))
                ),
                DesFinancialTransactionItem(
                  BigDecimal("1000"),
                  Some("TPS RECEIPTS BY DEBIT CARD"),
                  Some(LocalDate.of(2020, 5, 25)),
                  Some("Mass Write-Off"),
                  Some(LocalDate.of(2020, 10, 5))
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
                  Some("TPS RECEIPTS BY DEBIT CARD"),
                  Some(LocalDate.of(2020, 5, 25)),
                  Some("Automatic Clearing"),
                  Some(LocalDate.of(2022, 1, 31))
                )
              )
            )
          )
        )
      )
    }

    val return7 =
      ReturnProfile(
        ReturnSummary(
          "000000000007",
          LocalDate.of(2020, 6, 1),
          LocalDate.of(2020, 5, 24),
          None,
          "2020",
          DesAddressDetails(
            "14 Something Something Something",
            Some("That Other Place"),
            None,
            None,
            Some("ZZ0 0ZZ"),
            "GB"
          ),
          BigDecimal("0"),
          None
        ),
        List.empty
      )

    val return8 = {
      val chargeReference = "XCRG9999999991"
      ReturnProfile(
        ReturnSummary(
          "000000000011",
          LocalDate.of(2021, 6, 1),
          LocalDate.of(2021, 5, 25),
          None,
          "2021",
          DesAddressDetails("2 Similar Place", Some("Random Avenue"), Some("Ipswich"), None, Some("IP12 1AX"), "GB"),
          BigDecimal("43520"),
          Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(2021, 6, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference,
            BigDecimal("43520"),
            BigDecimal("0"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("43520"),
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2021, 6, 24))
                ),
                DesFinancialTransactionItem(
                  BigDecimal("43520"),
                  Some("Invalid Payment Method"),
                  Some(LocalDate.of(2021, 5, 25)),
                  Some("Write-Off"),
                  Some(LocalDate.of(2021, 6, 24))
                )
              )
            )
          )
        )
      )
    }

    val return9 = {
      val chargeReference = "XCRG9999999992"
      ReturnProfile(
        ReturnSummary(
          "000000000012",
          LocalDate.of(2021, 7, 1),
          LocalDate.of(2021, 6, 25),
          None,
          "2021",
          DesAddressDetails("2 Similar Place Second", Some("Random Avenue"), Some("Ipswich"), None, Some("IP12 1AX"), "GB"),
          BigDecimal("47520"),
          Some(
            List(
              Charge("CGT PPD Return UK Resident", LocalDate.of(2021, 7, 24), chargeReference)
            )
          )
        ),
        List(
          FinancialTransaction(
            chargeReference,
            BigDecimal("47520"),
            BigDecimal("47520"),
            Some(
              List(
                DesFinancialTransactionItem(
                  BigDecimal("47520"),
                  None,
                  None,
                  None,
                  Some(LocalDate.of(2021, 7, 24))
                )
              )
            )
          )
        )
      )
    }

    AccountProfile(_.equals("XDCGTP123456702"), List(return1, return2, return3, return4, return5, return6, return7, return8, return9))
  }

  private val profiles: List[AccountProfile] = List(account3, account2, account1)

  def getProfile(cgtReference: String): Option[AccountProfile] =
    profiles.find(_.cgtReferencePredicate(cgtReference))

}
