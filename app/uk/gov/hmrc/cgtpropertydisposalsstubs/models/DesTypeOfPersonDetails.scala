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

import play.api.libs.json.{JsValue, Json, OFormat, OWrites, Reads}

sealed trait DesTypeOfPersonDetails extends Product with Serializable

object DesTypeOfPersonDetails {

  final case class DesIndividual(firstName: String, lastName: String, typeOfPerson: String)
    extends DesTypeOfPersonDetails

  final case class DesTrustee(organisationName: String, typeOfPerson: String) extends DesTypeOfPersonDetails

  val individualFormat: OFormat[DesIndividual] = Json.format[DesIndividual]

  val trusteeFormat: OFormat[DesTrustee] = Json.format[DesTrustee]

  implicit val format: OFormat[DesTypeOfPersonDetails] =
    OFormat(
      Reads[DesTypeOfPersonDetails]{
        j => individualFormat.reads(j).orElse(trusteeFormat.reads(j))
      },
      OWrites[DesTypeOfPersonDetails] {
        case i: DesIndividual =>individualFormat.writes(i)
        case t: DesTrustee => trusteeFormat.writes(t)
      }
    )

}
