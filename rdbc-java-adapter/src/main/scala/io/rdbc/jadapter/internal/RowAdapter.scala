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

package io.rdbc.jadapter.internal

import java.lang.
{
  Boolean => JBoolean,
  Character => JChar,
  Double => JDouble,
  Float => JFloat,
  Integer => JInt,
  Long => JLong,
  Short => JShort
}
import java.math.{BigDecimal => JBigDec}
import java.time._
import java.util._

import io.rdbc.japi.{Row, SqlNumeric}
import io.rdbc.jadapter.internal.Conversions._
import io.rdbc.sapi
import io.rdbc.util.Preconditions.checkNotNull

import scala.compat.java8.OptionConverters._
import scala.reflect.ClassTag

private[jadapter] class RowAdapter(val underlying: sapi.Row)
                                  (implicit exConversion: ExceptionConversion)
  extends Row {

  import exConversion._

  def getCol[A](idx: Int, cls: Class[A]): A = {
    checkNotNull(idx)
    checkNotNull(cls)
    convertExceptions {
      underlying.col(idx)(ClassTag(cls)).asInstanceOf[A]
    }
  }

  def getColOpt[A](idx: Int, cls: Class[A]): Optional[A] = {
    checkNotNull(idx)
    checkNotNull(cls)
    convertExceptions {
      underlying.colOpt(idx)(ClassTag(cls)).asJava
    }
  }

  private def getColOptScala[A](idx: Int, cls: Class[A]): Option[A] = {
    checkNotNull(idx)
    checkNotNull(cls)
    convertExceptions {
      underlying.colOpt(idx)(ClassTag(cls))
    }
  }

  def getCol[A](name: String, cls: Class[A]): A = {
    checkNotNull(name)
    checkNotNull(cls)
    convertExceptions {
      underlying.col(name)(ClassTag(cls)).asInstanceOf[A]
    }
  }

  def getColOpt[A](name: String, cls: Class[A]): Optional[A] = {
    checkNotNull(name)
    checkNotNull(cls)
    convertExceptions {
      underlying.colOpt(name)(ClassTag(cls)).asJava
    }
  }

  private def getColOptScala[A](name: String, cls: Class[A]): Option[A] = {
    checkNotNull(name)
    checkNotNull(cls)
    convertExceptions {
      underlying.colOpt(name)(ClassTag(cls))
    }
  }

  def getStr(name: String): String = {
    getCol(name, classOf[String])
  }

  def getStrOpt(name: String): Optional[String] = {
    getColOptScala(name, classOf[String]).asJava
  }

  def getStr(idx: Int): String = {
    getCol(idx, classOf[String])
  }

  def getStrOpt(idx: Int): Optional[String] = {
    getColOptScala(idx, classOf[String]).asJava
  }

  def getBool(name: String): JBoolean = {
    getCol(name, classOf[JBoolean])
  }

  def getBoolOpt(name: String): Optional[JBoolean] = {
    getColOptScala(name, classOf[JBoolean]).asJava
  }

  def getBool(idx: Int): JBoolean = {
    getCol(idx, classOf[JBoolean])
  }

  def getBoolOpt(idx: Int): Optional[JBoolean] = {
    getColOptScala(idx, classOf[JBoolean]).asJava
  }

  def getChar(name: String): JChar = {
    getCol(name, classOf[JChar])
  }

  def getCharOpt(name: String): Optional[JChar] = {
    getColOptScala(name, classOf[JChar]).asJava
  }

  def getChar(idx: Int): JChar = {
    getCol(idx, classOf[JChar])
  }

  def getCharOpt(idx: Int): Optional[JChar] = {
    getColOptScala(idx, classOf[JChar]).asJava
  }

  def getShort(name: String): JShort = {
    getCol(name, classOf[JShort])
  }

  def getShortOpt(name: String): Optional[JShort] = {
    getColOptScala(name, classOf[JShort]).asJava
  }

  def getShort(idx: Int): JShort = {
    getCol(idx, classOf[JShort])
  }

  def getShortOpt(idx: Int): Optional[JShort] = {
    getColOptScala(idx, classOf[JShort]).asJava
  }

  def getInt(name: String): JInt = {
    getCol(name, classOf[JInt])
  }

  def getIntOpt(name: String): Optional[JInt] = {
    getColOptScala(name, classOf[JInt]).asJava
  }

  def getInt(idx: Int): JInt = {
    getCol(idx, classOf[JInt])
  }

  def getIntOpt(idx: Int): Optional[JInt] = {
    getColOptScala(idx, classOf[JInt]).asJava
  }

  def getLong(name: String): JLong = {
    getCol(name, classOf[JLong])
  }

  def getLongOpt(name: String): Optional[JLong] = {
    getColOptScala(name, classOf[JLong]).asJava
  }

  def getLong(idx: Int): JLong = {
    getCol(idx, classOf[JLong])
  }

  def getLongOpt(idx: Int): Optional[JLong] = {
    getColOptScala(idx, classOf[JLong]).asJava
  }

  def getBigDecimal(name: String): JBigDec = {
    getCol(name, classOf[BigDecimal]).bigDecimal
  }

  def getBigDecimalOpt(name: String): Optional[JBigDec] = {
    getColOptScala(name, classOf[BigDecimal]).map(_.bigDecimal).asJava
  }

  def getBigDecimal(idx: Int): JBigDec = {
    getCol(idx, classOf[BigDecimal]).bigDecimal
  }

  def getBigDecimalOpt(idx: Int): Optional[JBigDec] = {
    getColOptScala(idx, classOf[BigDecimal]).map(_.bigDecimal).asJava
  }

  def getNumeric(name: String): SqlNumeric = {
    getCol(name, classOf[sapi.SqlNumeric]).asJava
  }

  def getNumericOpt(name: String): Optional[SqlNumeric] = {
    getColOptScala(name, classOf[sapi.SqlNumeric]).map(_.asJava).asJava
  }

  def getNumeric(idx: Int): SqlNumeric = {
    getCol(idx, classOf[sapi.SqlNumeric]).asJava
  }

  def getNumericOpt(idx: Int): Optional[SqlNumeric] = {
    getColOptScala(idx, classOf[sapi.SqlNumeric]).map(_.asJava).asJava
  }

  def getDouble(name: String): JDouble = {
    getCol(name, classOf[JDouble])
  }

  def getDoubleOpt(name: String): Optional[JDouble] = {
    getColOptScala(name, classOf[JDouble]).asJava
  }

  def getDouble(idx: Int): JDouble = {
    getCol(idx, classOf[JDouble])
  }

  def getDoubleOpt(idx: Int): Optional[JDouble] = {
    getColOptScala(idx, classOf[JDouble]).asJava
  }

  def getFloat(name: String): JFloat = {
    getCol(name, classOf[JFloat])
  }

  def getFloatOpt(name: String): Optional[JFloat] = {
    getColOptScala(name, classOf[JFloat]).asJava
  }

  def getFloat(idx: Int): JFloat = {
    getCol(idx, classOf[JFloat])
  }

  def getFloatOpt(idx: Int): Optional[JFloat] = {
    getColOptScala(idx, classOf[JFloat]).asJava
  }

  def getInstant(name: String): Instant = {
    getCol(name, classOf[Instant])
  }

  def getInstantOpt(name: String): Optional[Instant] = {
    getColOptScala(name, classOf[Instant]).asJava
  }

  def getInstant(idx: Int): Instant = {
    getCol(idx, classOf[Instant])
  }

  def getInstantOpt(idx: Int): Optional[Instant] = {
    getColOptScala(idx, classOf[Instant]).asJava
  }

  def getLocalDateTime(name: String): LocalDateTime = {
    getCol(name, classOf[LocalDateTime])
  }

  def getLocalDateTimeOpt(name: String): Optional[LocalDateTime] = {
    getColOptScala(name, classOf[LocalDateTime]).asJava
  }

  def getLocalDateTime(idx: Int): LocalDateTime = {
    getCol(idx, classOf[LocalDateTime])
  }

  def getLocalDateTimeOpt(idx: Int): Optional[LocalDateTime] = {
    getColOptScala(idx, classOf[LocalDateTime]).asJava
  }

  def getLocalDate(name: String): LocalDate = {
    getCol(name, classOf[LocalDate])
  }

  def getLocalDateOpt(name: String): Optional[LocalDate] = {
    getColOptScala(name, classOf[LocalDate]).asJava
  }

  def getLocalDate(idx: Int): LocalDate = {
    getCol(idx, classOf[LocalDate])
  }

  def getLocalDateOpt(idx: Int): Optional[LocalDate] = {
    getColOptScala(idx, classOf[LocalDate]).asJava
  }

  def getLocalTime(name: String): LocalTime = {
    getCol(name, classOf[LocalTime])
  }

  def getLocalTimeOpt(name: String): Optional[LocalTime] = {
    getColOptScala(name, classOf[LocalTime]).asJava
  }

  def getLocalTime(idx: Int): LocalTime = {
    getCol(idx, classOf[LocalTime])
  }

  def getLocalTimeOpt(idx: Int): Optional[LocalTime] = {
    getColOptScala(idx, classOf[LocalTime]).asJava
  }

  def getBytes(name: String): Array[Byte] = {
    getCol(name, classOf[Array[Byte]])
  }

  def getBytesOpt(name: String): Optional[Array[Byte]] = {
    getColOptScala(name, classOf[Array[Byte]]).asJava
  }

  def getBytes(idx: Int): Array[Byte] = {
    getCol(idx, classOf[Array[Byte]])
  }

  def getBytesOpt(idx: Int): Optional[Array[Byte]] = {
    getColOptScala(idx, classOf[Array[Byte]]).asJava
  }

  def getUuid(name: String): UUID = {
    getCol(name, classOf[UUID])
  }

  def getUuidOpt(name: String): Optional[UUID] = {
    getColOptScala(name, classOf[UUID]).asJava
  }

  def getUuid(idx: Int): UUID = {
    getCol(idx, classOf[UUID])
  }

  def getUuidOpt(idx: Int): Optional[UUID] = {
    getColOptScala(idx, classOf[UUID]).asJava
  }

  override def toString: String = underlying.toString
}
