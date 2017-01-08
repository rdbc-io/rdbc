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
import io.rdbc.sapi.{Row, SqlNumeric, TypeConverterRegistry}

import scala.reflect.ClassTag

trait RowPartialImpl extends Row {

  protected def typeConverters: TypeConverterRegistry

  protected def any(name: String): Any

  protected def any(idx: Int): Any

  protected def convertType[A](any: Any, cls: Class[A]): A = {
    if (any == null) null.asInstanceOf[A]
    else {
      if (cls.isInstance(any)) {
        any.asInstanceOf[A]
      } else {
        typeConverters.getByClass(cls)
          .map(converter => converter.fromAny(any))
          .getOrElse(throw new ConversionException(any, cls))
      }
    }
  }

  def col[A: ClassTag](name: String): A = {
    val notTyped = any(name)
    convertType(notTyped, implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])
  }

  def col[A: ClassTag](idx: Int): A = {
    val notTyped = any(idx)
    convertType(notTyped, implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])
  }

  def colOpt[A: ClassTag](idx: Int): Option[A] = Option(col[A](idx))

  def colOpt[A: ClassTag](name: String): Option[A] = Option(col[A](name))

  def str(name: String): String = col[String](name)

  def strOpt(name: String): Option[String] = colOpt[String](name)

  def str(idx: Int): String = col[String](idx)

  def strOpt(idx: Int): Option[String] = colOpt[String](idx)

  def bool(name: String): Boolean = col[Boolean](name)

  def boolOpt(name: String): Option[Boolean] = colOpt[Boolean](name)

  def bool(idx: Int): Boolean = col[Boolean](idx)

  def boolOpt(idx: Int): Option[Boolean] = colOpt[Boolean](idx)

  def char(name: String): Char = col[Char](name)

  def charOpt(name: String): Option[Char] = colOpt[Char](name)

  def char(idx: Int): Char = col[Char](idx)

  def charOpt(idx: Int): Option[Char] = colOpt[Char](idx)

  def short(name: String): Short = col[Short](name)

  def shortOpt(name: String): Option[Short] = colOpt[Short](name)

  def short(idx: Int): Short = col[Short](idx)

  def shortOpt(idx: Int): Option[Short] = colOpt[Short](idx)

  def int(name: String): Int = col[Int](name)

  def intOpt(name: String): Option[Int] = colOpt[Int](name)

  def int(idx: Int): Int = col[Int](idx)

  def intOpt(idx: Int): Option[Int] = colOpt[Int](idx)

  def long(name: String): Long = col[Long](name)

  def longOpt(name: String): Option[Long] = colOpt[Long](name)

  def long(idx: Int): Long = col[Long](idx)

  def longOpt(idx: Int): Option[Long] = colOpt[Long](idx)

  def bigDecimal(name: String): BigDecimal = col[BigDecimal](name)

  def bigDecimalOpt(name: String): Option[BigDecimal] = colOpt[BigDecimal](name)

  def bigDecimal(idx: Int): BigDecimal = col[BigDecimal](idx)

  def bigDecimalOpt(idx: Int): Option[BigDecimal] = colOpt[BigDecimal](idx)

  def numeric(name: String): SqlNumeric = col[SqlNumeric](name)

  def numericOpt(name: String): Option[SqlNumeric] = colOpt[SqlNumeric](name)

  def numeric(idx: Int): SqlNumeric = col[SqlNumeric](idx)

  def numericOpt(idx: Int): Option[SqlNumeric] = colOpt[SqlNumeric](idx)

  def double(name: String): Double = col[Double](name)

  def doubleOpt(name: String): Option[Double] = colOpt[Double](name)

  def double(idx: Int): Double = col[Double](idx)

  def doubleOpt(idx: Int): Option[Double] = colOpt[Double](idx)

  def float(name: String): Float = col[Float](name)

  def floatOpt(name: String): Option[Float] = colOpt[Float](name)

  def float(idx: Int): Float = col[Float](idx)

  def floatOpt(idx: Int): Option[Float] = colOpt[Float](idx)

  def instant(name: String): Instant = col[Instant](name)

  def instantOpt(name: String): Option[Instant] = colOpt[Instant](name)

  def instant(idx: Int): Instant = col[Instant](idx)

  def instantOpt(idx: Int): Option[Instant] = colOpt[Instant](idx)

  def localDateTime(name: String): LocalDateTime = col[LocalDateTime](name)

  def localDateTimeOpt(name: String): Option[LocalDateTime] = colOpt[LocalDateTime](name)

  def localDateTime(idx: Int): LocalDateTime = col[LocalDateTime](idx)

  def localDateTimeOpt(idx: Int): Option[LocalDateTime] = colOpt[LocalDateTime](idx)

  def localDate(name: String): LocalDate = col[LocalDate](name)

  def localDateOpt(name: String): Option[LocalDate] = colOpt[LocalDate](name)

  def localDate(idx: Int): LocalDate = col[LocalDate](idx)

  def localDateOpt(idx: Int): Option[LocalDate] = colOpt[LocalDate](idx)

  def localTime(name: String): LocalTime = col[LocalTime](name)

  def localTimeOpt(name: String): Option[LocalTime] = colOpt[LocalTime](name)

  def localTime(idx: Int): LocalTime = col[LocalTime](idx)

  def localTimeOpt(idx: Int): Option[LocalTime] = colOpt[LocalTime](idx)

  def bytes(name: String): Array[Byte] = col[Array[Byte]](name)

  def bytesOpt(name: String): Option[Array[Byte]] = colOpt[Array[Byte]](name)

  def bytes(idx: Int): Array[Byte] = col[Array[Byte]](idx)

  def bytesOpt(idx: Int): Option[Array[Byte]] = colOpt[Array[Byte]](idx)

  def uuid(name: String): UUID = col[UUID](name)

  def uuidOpt(name: String): Option[UUID] = colOpt[UUID](name)

  def uuid(idx: Int): UUID = col[UUID](idx)

  def uuidOpt(idx: Int): Option[UUID] = colOpt[UUID](idx)

}
