/*
 * Copyright 2016 Krzysztof Pado
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

package io.rdbc.sapi

import java.time._
import java.util.UUID

trait Row {

  def obj[A](idx: Int, cls: Class[A]): A

  def objOpt[A](idx: Int, cls: Class[A]): Option[A]

  def obj[A](name: String, cls: Class[A]): A

  def objOpt[A](name: String, cls: Class[A]): Option[A]

  def str(name: String): String

  def strOpt(name: String): Option[String]

  def bool(name: String): Boolean

  def boolOpt(name: String): Option[Boolean]

  def char(name: String): Char

  def charOpt(name: String): Option[Char]

  def short(name: String): Short

  def shortOpt(name: String): Option[Short]

  def int(name: String): Int

  def intOpt(name: String): Option[Int]

  def int(idx: Int): Int

  def intOpt(idx: Int): Option[Int]

  def long(name: String): Long

  def longOpt(name: String): Option[Long]

  def long(idx: Int): Long

  def longOpt(idx: Int): Option[Long]

  def bigDecimal(name: String): BigDecimal

  def bigDecimalOpt(name: String): Option[BigDecimal]

  def double(name: String): Double

  def doubleOpt(name: String): Option[Double]

  def float(name: String): Float

  def floatOpt(name: String): Option[Float]

  def instant(name: String): Instant

  def instantOpt(name: String): Option[Instant]

  def localDateTime(name: String): LocalDateTime

  def localDateTimeOpt(name: String): Option[LocalDateTime]

  def zonedDateTime(name: String): ZonedDateTime

  def zonedDateTimeOpt(name: String): Option[ZonedDateTime]

  def localDate(name: String): LocalDate

  def localDateOpt(name: String): Option[LocalDate]

  def localTime(name: String): LocalTime

  def localTimeOpt(name: String): Option[LocalTime]

  def bytes(name: String): Array[Byte]

  def bytesOpt(name: String): Option[Array[Byte]]

  def uuid(name: String): UUID

  def uuidOpt(name: String): Option[UUID]

  def uuid(idx: Int): UUID

  def uuidOpt(idx: Int): Option[UUID]
  //TODO support arrays?
}
