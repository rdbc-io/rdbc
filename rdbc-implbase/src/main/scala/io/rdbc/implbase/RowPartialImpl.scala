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

import io.rdbc.api.exceptions.ConversionException
import io.rdbc.sapi.{Row, TypeConverterRegistry}

import scala.reflect.ClassTag

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
          .getOrElse(throw new ConversionException(raw, cls))
      }
    }
  }

  def col[A: ClassTag](name: String): A = {
    val raw = notConverted(name)
    convert(raw, implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])
  }

  def col[A: ClassTag](idx: Int): A = {
    val raw = notConverted(idx)
    convert(raw, implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])
  }

  def colOpt[A: ClassTag](idx: Int): Option[A] = Option(col[A](idx))

  def colOpt[A: ClassTag](name: String): Option[A] = Option(col[A](name))

  def str(name: String): String = col(name)

  def strOpt(name: String): Option[String] = colOpt(name)

  def bool(name: String): Boolean = col(name)

  def boolOpt(name: String): Option[Boolean] = colOpt(name)

  def char(name: String): Char = col(name)

  def charOpt(name: String): Option[Char] = colOpt(name)

  def short(name: String): Short = col(name)

  def shortOpt(name: String): Option[Short] = colOpt(name)

  def int(name: String): Int = col(name)

  def intOpt(name: String): Option[Int] = colOpt(name)

  def int(idx: Int): Int = col(idx)

  def intOpt(idx: Int): Option[Int] = colOpt(idx)

  def long(name: String): Long = col(name)

  def longOpt(name: String): Option[Long] = colOpt(name)

  def long(idx: Int): Long = col(idx)

  def longOpt(idx: Int): Option[Long] = colOpt(idx)

  def bigDecimal(name: String): BigDecimal = col(name)

  def bigDecimalOpt(name: String): Option[BigDecimal] = colOpt(name)

  def double(name: String): Double = col(name)

  def doubleOpt(name: String): Option[Double] = colOpt(name)

  def float(name: String): Float = col(name)

  def floatOpt(name: String): Option[Float] = colOpt(name)

  def instant(name: String): Instant = col(name)

  def instantOpt(name: String): Option[Instant] = colOpt(name)

  def localDateTime(name: String): LocalDateTime = col(name)

  def localDateTimeOpt(name: String): Option[LocalDateTime] = colOpt(name)

  def zonedDateTime(name: String): ZonedDateTime = col(name)

  def zonedDateTimeOpt(name: String): Option[ZonedDateTime] = colOpt(name)

  def localDate(name: String): LocalDate = col(name)

  def localDateOpt(name: String): Option[LocalDate] = colOpt(name)

  def localTime(name: String): LocalTime = col(name)

  def localTimeOpt(name: String): Option[LocalTime] = colOpt(name)

  def bytes(name: String): Array[Byte] = col(name)

  def bytesOpt(name: String): Option[Array[Byte]] = colOpt(name)

  def uuid(name: String): UUID = col(name)

  def uuidOpt(name: String): Option[UUID] = colOpt(name)

  def uuid(idx: Int): UUID = col(idx)

  def uuidOpt(idx: Int): Option[UUID] = colOpt(idx)
}
