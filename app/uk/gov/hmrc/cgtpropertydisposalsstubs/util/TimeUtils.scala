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

package uk.gov.hmrc.cgtpropertydisposalsstubs.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal._
import scala.util.Try

object TimeUtils {

  def withFromAndToDate(fromDate: String, toDate: String): (LocalDate, LocalDate) = {
    def parseDate(string: String): Option[LocalDate] =
      Try(LocalDate.parse(string, DateTimeFormatter.ISO_DATE)).toOption

    parseDate(fromDate) -> parseDate(toDate) match {
      case (None, None) =>
        throw new RuntimeException(s"Could not parse fromDate ('$fromDate') or toDate ('$toDate') ")

      case (None, Some(_)) =>
        throw new RuntimeException(s"Could not parse fromDate ('$fromDate')")

      case (Some(_), None) =>
        throw new RuntimeException(s"Could not parse toDate ('$toDate')")

      case (Some(from), Some(to)) =>

        val daysDuration =  ChronoUnit.DAYS.between(from, to)
        if( daysDuration <= 366)
          (from, to)
        else
        throw new RuntimeException("Financial data api date range over one year")

    }
  }
}
