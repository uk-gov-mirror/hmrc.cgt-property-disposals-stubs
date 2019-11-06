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
import cats.syntax.eq._
import cats.instances.string._
import play.api.mvc.Result
import play.api.mvc.Results.{BadRequest, InternalServerError, NotFound, ServiceUnavailable}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.SubscriptionController.{DesSubscriptionDisplayDetails, SubscriptionDetails}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.{DesAddressDetails, DesContactDetails, DesIndividual, DesTrustee, TypeOfPersonDetails}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.DesErrorResponse.desErrorResponseJson

case class SubscriptionDisplay(
  predicate: String => Boolean,
  subscriptionDisplayResponse: Either[Result, DesSubscriptionDisplayDetails]
)

object SubscriptionDisplayProfiles {

  def individualSubscriptionDisplayDetails(registeredWithId: Boolean) = DesSubscriptionDisplayDetails(
    regime = "CGT",
    subscriptionDetails = SubscriptionDetails(
        TypeOfPersonDetails(
          "Individual",
          Some("Luke"),
          Some("Bishop"),
          None
        ),
      registeredWithId,
      DesAddressDetails(
        "100 Sutton Street",
        Some("Wokingham"),
        Some("Surrey"),
        Some("London"),
        "DH14EJ",
        "GB"
      ),
      DesContactDetails(
        "Stephen Wood",
        Some("(+013)32752856"),
        Some("(+44)7782565326"),
        Some("01332754256"),
        Some("stephen@abc.co.uk")
      )
    )
  )

  def trusteeSubscriptionDisplayDetails(registeredWithId: Boolean) = DesSubscriptionDisplayDetails(
    regime = "CGT",
    subscriptionDetails = SubscriptionDetails(
      TypeOfPersonDetails(
        "Trustee",
        None,
        None,
        Some("ABC Trust")
      ),
      registeredWithId,
      DesAddressDetails(
        "101 Kiwi Street",
        None,
        None,
        Some("Christchurch"),
        "",
        "NZ"
      ),
      DesContactDetails(
        "Stephen Wood",
        Some("(+013)32752856"),
        Some("(+44)7782565326"),
        Some("01332754256"),
        Some("stephen@abc.co.uk")
      )
    )
  )

  def getDisplayDetails(id: String): Option[SubscriptionDisplay] =
    subscriptionDisplayProfiles.find(_.predicate(id))

  private val subscriptionDisplayProfiles = List(
    SubscriptionDisplay(
      _ === "XLCGTP212487578",
      Right(individualSubscriptionDisplayDetails(registeredWithId = false))
    ),
    SubscriptionDisplay(
      _ === "XLCGTP212487579",
      Right(individualSubscriptionDisplayDetails(registeredWithId = true))
    ),
    SubscriptionDisplay(
      _ === "XACGTP123456701",
      Right(trusteeSubscriptionDisplayDetails(registeredWithId = false))
    ),
    SubscriptionDisplay(
      _ === "XACGTP123456702",
      Right(trusteeSubscriptionDisplayDetails(registeredWithId = true))
    ),
    SubscriptionDisplay(
      _ === "XACGTP123456703",
      Left(
        BadRequest(
          desErrorResponseJson("INVALID_REGIME", "Submission has not passed validation. Invalid parameter regimeValue")
        )
      )
    ),
    SubscriptionDisplay(
      _ === "XACGTP123456704",
      Left(
        BadRequest(
          desErrorResponseJson("INVALID_IDTYPE", "Submission has not passed validation. Invalid parameter idType.")
        )
      )
    ),
    SubscriptionDisplay(
      _ === "XACGTP123456705",
      Left(
        BadRequest(
          desErrorResponseJson(
            "INVALID_REQUEST",
            "Submission has not passed validation. Request not implemented by the backend."
          )
        )
      )
    ),
    SubscriptionDisplay(
      _ === "XACGTP123456706",
      Left(
        BadRequest(
          desErrorResponseJson("INVALID_CORRELATIONID", "Submission has not passed validation. Invalid CorrelationId.")
        )
      )
    ),
    SubscriptionDisplay(
      _ === "XACGTP123456707",
      Left(
        NotFound(desErrorResponseJson("NOT_FOUND", "Data not found for the provided Registration Number"))
      )
    ),
    SubscriptionDisplay(
      _ === "XACGTP123456708",
      Left(
        InternalServerError(
          desErrorResponseJson(
            "SERVER_ERROR",
            "DES is currently experiencing problems that require live service intervention"
          )
        )
      )
    ),
    SubscriptionDisplay(
      _ === "XACGTP123456709",
      Left(
        ServiceUnavailable(
          desErrorResponseJson("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding")
        )
      )
    )
  )

}
