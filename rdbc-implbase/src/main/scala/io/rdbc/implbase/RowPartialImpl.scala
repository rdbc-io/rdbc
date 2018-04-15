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
import io.rdbc.sapi.{Row, SqlNumeric, TypeConverterRegistry}
import io.rdbc.util.Preconditions.checkNotNull

import scala.reflect.ClassTag

trait RowPartialImpl extends Row {

  protected def typeConverters: TypeConverterRegistry

  protected def any(name: String): Option[Any]

  protected def any(idx: Int): Option[Any]

  private[this] def convertType[A](maybeAny: Option[Any], cls: Class[A]): Option[A] = {
    maybeAny.map { any =>
      if (cls.isInstance(any)) {
        any.asInstanceOf[A]
      } else {
        typeConverters.getByClass(cls)
          .map(converter => converter.fromAny(any))
          .getOrElse(throw new ConversionException(any, cls))
      }
    }
  }

  private[this] def colOpt[A: ClassTag](maybeAny: Option[Any]): Option[A] = {
    checkNotNull(maybeAny)
    convertType(maybeAny, implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])
  }

  private[this] def col[A: ClassTag](maybeAny: Option[Any]): A = {
    checkNotNull(maybeAny)
    colOpt(maybeAny).getOrElse {
      throw new ConversionException(None, implicitly[ClassTag[A]].runtimeClass)
    }
  }

  override def col[A: ClassTag](name: String): A = col(any(name))

  override def col[A: ClassTag](idx: Int): A = col(any(idx))

  override def colOpt[A: ClassTag](idx: Int): Option[A] = colOpt(any(idx))

  override def colOpt[A: ClassTag](name: String): Option[A] = colOpt(any(name))

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

  override def numeric(name: String): SqlNumeric = col[SqlNumeric](name)

  override def numericOpt(name: String): Option[SqlNumeric] = colOpt[SqlNumeric](name)

  override def numeric(idx: Int): SqlNumeric = col[SqlNumeric](idx)

  override def numericOpt(idx: Int): Option[SqlNumeric] = colOpt[SqlNumeric](idx)

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

}
