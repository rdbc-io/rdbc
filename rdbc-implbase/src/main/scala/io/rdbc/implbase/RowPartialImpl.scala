/*
 * Copyright 2016 rdbc contributors
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

package io.rdbc.implbase

import java.time._
import java.util.UUID

import io.rdbc.sapi.exceptions.ConversionException
import io.rdbc.sapi.{DecimalNumber, Row}
import io.rdbc.util.Preconditions.checkNotNull

import scala.reflect.ClassTag

trait RowPartialImpl extends Row {

  override def col[A: ClassTag](name: String): A = {
    checkNotNull(name)
    colOpt(name).getOrElse {
      throw nullConversionException(implicitly[ClassTag[A]].runtimeClass)
    }
  }

  override def col[A: ClassTag](idx: Int): A = {
    checkNotNull(idx)
    colOpt(idx).getOrElse {
      throw nullConversionException(implicitly[ClassTag[A]].runtimeClass)
    }
  }

  private def nullConversionException(target: Class[_]): ConversionException = {
    new ConversionException(
      msg = s"SQL NULL cannot be represented by $target, use *Opt method instead",
      value = None,
      targetType = target,
      maybeCause = None
    )
  }

  override def str(name: String): String = col[String](name)

  override def strOpt(name: String): Option[String] = colOpt[String](name)

  override def str(idx: Int): String = col[String](idx)

  override def strOpt(idx: Int): Option[String] = colOpt[String](idx)

  override def bool(name: String): Boolean = col[Boolean](name)

  override def boolOpt(name: String): Option[Boolean] = colOpt[Boolean](name)

  override def bool(idx: Int): Boolean = col[Boolean](idx)

  override def boolOpt(idx: Int): Option[Boolean] = colOpt[Boolean](idx)

  override def char(name: String): Char = col[Char](name)

  override def charOpt(name: String): Option[Char] = colOpt[Char](name)

  override def char(idx: Int): Char = col[Char](idx)

  override def charOpt(idx: Int): Option[Char] = colOpt[Char](idx)

  override def short(name: String): Short = col[Short](name)

  override def shortOpt(name: String): Option[Short] = colOpt[Short](name)

  override def short(idx: Int): Short = col[Short](idx)

  override def shortOpt(idx: Int): Option[Short] = colOpt[Short](idx)

  override def int(name: String): Int = col[Int](name)

  override def intOpt(name: String): Option[Int] = colOpt[Int](name)

  override def int(idx: Int): Int = col[Int](idx)

  override def intOpt(idx: Int): Option[Int] = colOpt[Int](idx)

  override def long(name: String): Long = col[Long](name)

  override def longOpt(name: String): Option[Long] = colOpt[Long](name)

  override def long(idx: Int): Long = col[Long](idx)

  override def longOpt(idx: Int): Option[Long] = colOpt[Long](idx)

  override def bigDecimal(name: String): BigDecimal = col[BigDecimal](name)

  override def bigDecimalOpt(name: String): Option[BigDecimal] = colOpt[BigDecimal](name)

  override def bigDecimal(idx: Int): BigDecimal = col[BigDecimal](idx)

  override def bigDecimalOpt(idx: Int): Option[BigDecimal] = colOpt[BigDecimal](idx)

  override def decimal(name: String): DecimalNumber = col[DecimalNumber](name)

  override def decimalOpt(name: String): Option[DecimalNumber] = colOpt[DecimalNumber](name)

  override def decimal(idx: Int): DecimalNumber = col[DecimalNumber](idx)

  override def decimalOpt(idx: Int): Option[DecimalNumber] = colOpt[DecimalNumber](idx)

  override def double(name: String): Double = col[Double](name)

  override def doubleOpt(name: String): Option[Double] = colOpt[Double](name)

  override def double(idx: Int): Double = col[Double](idx)

  override def doubleOpt(idx: Int): Option[Double] = colOpt[Double](idx)

  override def float(name: String): Float = col[Float](name)

  override def floatOpt(name: String): Option[Float] = colOpt[Float](name)

  override def float(idx: Int): Float = col[Float](idx)

  override def floatOpt(idx: Int): Option[Float] = colOpt[Float](idx)

  override def instant(name: String): Instant = col[Instant](name)

  override def instantOpt(name: String): Option[Instant] = colOpt[Instant](name)

  override def instant(idx: Int): Instant = col[Instant](idx)

  override def instantOpt(idx: Int): Option[Instant] = colOpt[Instant](idx)

  override def instant(name: String, zoneId: ZoneId): Instant = {
    localDateTimeToInstant(localDateTime(name), zoneId)
  }

  override def instantOpt(name: String, zoneId: ZoneId): Option[Instant] = {
    localDateTimeOpt(name).map(localDateTimeToInstant(_, zoneId))
  }

  override def instant(idx: Int, zoneId: ZoneId): Instant = {
    localDateTimeToInstant(localDateTime(idx), zoneId)
  }

  override def instantOpt(idx: Int, zoneId: ZoneId): Option[Instant] = {
    localDateTimeOpt(idx).map(localDateTimeToInstant(_, zoneId))
  }

  override def localDateTime(name: String): LocalDateTime = col[LocalDateTime](name)

  override def localDateTimeOpt(name: String): Option[LocalDateTime] = colOpt[LocalDateTime](name)

  override def localDateTime(idx: Int): LocalDateTime = col[LocalDateTime](idx)

  override def localDateTimeOpt(idx: Int): Option[LocalDateTime] = colOpt[LocalDateTime](idx)

  override def localDate(name: String): LocalDate = col[LocalDate](name)

  override def localDateOpt(name: String): Option[LocalDate] = colOpt[LocalDate](name)

  override def localDate(idx: Int): LocalDate = col[LocalDate](idx)

  override def localDateOpt(idx: Int): Option[LocalDate] = colOpt[LocalDate](idx)

  override def localTime(name: String): LocalTime = col[LocalTime](name)

  override def localTimeOpt(name: String): Option[LocalTime] = colOpt[LocalTime](name)

  override def localTime(idx: Int): LocalTime = col[LocalTime](idx)

  override def localTimeOpt(idx: Int): Option[LocalTime] = colOpt[LocalTime](idx)

  override def bytes(name: String): Array[Byte] = col[Array[Byte]](name)

  override def bytesOpt(name: String): Option[Array[Byte]] = colOpt[Array[Byte]](name)

  override def bytes(idx: Int): Array[Byte] = col[Array[Byte]](idx)

  override def bytesOpt(idx: Int): Option[Array[Byte]] = colOpt[Array[Byte]](idx)

  override def uuid(name: String): UUID = col[UUID](name)

  override def uuidOpt(name: String): Option[UUID] = colOpt[UUID](name)

  override def uuid(idx: Int): UUID = col[UUID](idx)

  override def uuidOpt(idx: Int): Option[UUID] = colOpt[UUID](idx)

  override def zonedDateTime(name: String): ZonedDateTime = col[ZonedDateTime](name)

  override def zonedDateTimeOpt(name: String): Option[ZonedDateTime] = colOpt[ZonedDateTime](name)

  override def zonedDateTime(idx: Int): ZonedDateTime = col[ZonedDateTime](idx)

  override def zonedDateTimeOpt(idx: Int): Option[ZonedDateTime] = colOpt[ZonedDateTime](idx)

  private def localDateTimeToInstant(ldt: LocalDateTime, zoneId: ZoneId): Instant = {
    ldt.toInstant(zoneId.getRules.getOffset(ldt))
  }

}
