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

package io.rdbc.implbase

import java.time._
import java.util.UUID

import io.rdbc.api.exceptions.NoSuitableConverterFoundException
import io.rdbc.sapi.{Row, TypeConverterRegistry}

trait RowPartialImpl extends Row {

  protected def typeConverterRegistry: TypeConverterRegistry

  protected def notConverted(name: String): Any

  protected def notConverted(idx: Int): Any

  protected def convert[A](raw: Any, cls: Class[A]): A = {
    if (raw == null) null.asInstanceOf[A]
    else {
      if (cls.isInstance(raw)) {
        raw.asInstanceOf[A]
      } else {
        typeConverterRegistry.converters.get(cls)
          .map(converter => converter.fromAny(raw).asInstanceOf[A])
          .getOrElse(throw NoSuitableConverterFoundException(raw))
      }
    }
  }

  def obj[A](name: String, cls: Class[A]): A = {
    val raw = notConverted(name)
    convert(raw, cls)
  }

  def obj[A](idx: Int, cls: Class[A]): A = {
    val raw = notConverted(idx)
    convert(raw, cls)
  }

  def objOpt[A](idx: Int, cls: Class[A]): Option[A] = Option(obj[A](idx, cls))

  def objOpt[A](name: String, cls: Class[A]): Option[A] = Option(obj[A](name, cls))

  def str(name: String): String = obj(name, classOf[String])

  def strOpt(name: String): Option[String] = objOpt(name, classOf[String])

  def bool(name: String): Boolean = obj(name, classOf[Boolean])

  def boolOpt(name: String): Option[Boolean] = objOpt(name, classOf[Boolean])

  def char(name: String): Char = obj(name, classOf[Char])

  def charOpt(name: String): Option[Char] = objOpt(name, classOf[Char])

  def short(name: String): Short = obj(name, classOf[Short])

  def shortOpt(name: String): Option[Short] = objOpt(name, classOf[Short])

  def int(name: String): Int = obj(name, classOf[Int])

  def intOpt(name: String): Option[Int] = objOpt(name, classOf[Int])

  def int(idx: Int): Int = obj(idx, classOf[Int])

  def intOpt(idx: Int): Option[Int] = objOpt(idx, classOf[Int])

  def long(name: String): Long = obj(name, classOf[Long])

  def longOpt(name: String): Option[Long] = objOpt(name, classOf[Long])

  def long(idx: Int): Long = obj(idx, classOf[Long])

  def longOpt(idx: Int): Option[Long] = objOpt(idx, classOf[Long])

  def bigDecimal(name: String): BigDecimal = obj(name, classOf[BigDecimal])

  def bigDecimalOpt(name: String): Option[BigDecimal] = objOpt(name, classOf[BigDecimal])

  def double(name: String): Double = obj(name, classOf[Double])

  def doubleOpt(name: String): Option[Double] = objOpt(name, classOf[Double])

  def float(name: String): Float = obj(name, classOf[Float])

  def floatOpt(name: String): Option[Float] = objOpt(name, classOf[Float])

  def instant(name: String): Instant = obj(name, classOf[Instant])

  def instantOpt(name: String): Option[Instant] = objOpt(name, classOf[Instant])

  def localDateTime(name: String): LocalDateTime = obj(name, classOf[LocalDateTime])

  def localDateTimeOpt(name: String): Option[LocalDateTime] = objOpt(name, classOf[LocalDateTime])

  def zonedDateTime(name: String): ZonedDateTime = obj(name, classOf[ZonedDateTime])

  def zonedDateTimeOpt(name: String): Option[ZonedDateTime] = objOpt(name, classOf[ZonedDateTime])

  def localDate(name: String): LocalDate = obj(name, classOf[LocalDate])

  def localDateOpt(name: String): Option[LocalDate] = objOpt(name, classOf[LocalDate])

  def localTime(name: String): LocalTime = obj(name, classOf[LocalTime])

  def localTimeOpt(name: String): Option[LocalTime] = objOpt(name, classOf[LocalTime])

  def bytes(name: String): Array[Byte] = obj(name, classOf[Array[Byte]])

  def bytesOpt(name: String): Option[Array[Byte]] = objOpt(name, classOf[Array[Byte]])

  def uuid(name: String): UUID = obj(name, classOf[UUID])

  def uuidOpt(name: String): Option[UUID] = objOpt(name, classOf[UUID])

  def uuid(idx: Int): UUID = obj(idx, classOf[UUID])

  def uuidOpt(idx: Int): Option[UUID] = objOpt(idx, classOf[UUID])
}
