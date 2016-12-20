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

import scala.reflect.ClassTag

/** Represents a row of a result returned by a database engine.
  *
  * This class defines a set of methods that can be used to get values from the row either by a column name
  * or by a column index. Each method has a version returning an [[scala.Option Option]] to allow null-safe
  * handling of SQL `null` values.
  *
  * @define exceptions
  *  Throws:
  *  - [[io.rdbc.api.exceptions.ConversionException ConversionException]] when database value could not be converted to the desired type
  * @define nullSafetyNote
  *  For SQL `null` values, `null` is returned. For null-safety consider using corresponding `*Opt` method.
  * @define returningNone
  *  For SQL `null` values [[scala.None None]] is returned.
  * @define boolValues
  *  A single 'T', 'Y' or '1' character values or `1` numeric value are considered `true`.
  *  A single 'F', 'N' or '0' character values or `0` numeric value are considered `false`.
  */
trait Row {

  /** Returns an object of type `A` from column with `idx` index.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def col[A: ClassTag](idx: Int): A

  /** Returns an object of type `A` from column with `idx` index.
    *
    * $returningNone
    *
    * $exceptions
    */
  def colOpt[A: ClassTag](idx: Int): Option[A]

  /** Returns an object of type `A` from column with `name` name.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def col[A: ClassTag](name: String): A

  /** Returns an object of type `A` from column with `name` name.
    *
    * $returningNone
    *
    * $exceptions
    */
  def colOpt[A: ClassTag](name: String): Option[A]

  /** Returns a [[String]] from column with `name` name.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def str(name: String): String

  /** Returns a [[String]] from column with `name` name.
    *
    * $returningNone
    *
    * $exceptions
    */
  def strOpt(name: String): Option[String]

  /** Returns a boolean value from column with `name` name.
    *
    * $boolValues
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def bool(name: String): Boolean

  /** Returns a boolean value from column with `name` name.
    *
    * $boolValues
    *
    * $returningNone
    *
    * $exceptions
    */
  def boolOpt(name: String): Option[Boolean]

  /** Returns a character from column with `name` name.
    *
    * Varchar types with a single character are convertible to a [[Char]].
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def char(name: String): Char

  /** Returns a character from column with `name` name.
    *
    * Varchar types with a single character are convertible to a [[Char]].
    *
    * $returningNone
    *
    * $exceptions
    */
  def charOpt(name: String): Option[Char]

  /** Returns a [[Short]] from column with `name` name.
    *
    * All numeric types can be converted to [[Short]], but some conversions may involve rounding or truncation.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def short(name: String): Short

  /** Returns a [[Short]] from column with `name` name.
    *
    * All numeric types can be converted to [[Short]], but some conversions may involve rounding or truncation.
    *
    * $returningNone
    *
    * $exceptions
    */
  def shortOpt(name: String): Option[Short]

  /** Returns an [[Int]] from column with `name` name.
    *
    * All numeric types can be converted to [[Int]], but some conversions may involve rounding or truncation.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def int(name: String): Int

  /** Returns an [[Int]] from column with `name` name.
    *
    * All numeric types can be converted to [[Int]], but some conversions may involve rounding or truncation.
    *
    * $returningNone
    *
    * $exceptions
    */
  def intOpt(name: String): Option[Int]

  /** Returns an [[Int]] from column with `idx` index.
    *
    * All numeric types can be converted to [[Int]], but some conversions may involve rounding or truncation.
    *
    * $returningNone
    *
    * $exceptions
    */
  def int(idx: Int): Int

  /** Returns an [[Int]] from column with `idx` index.
    *
    * All numeric types can be converted to [[Int]], but some conversions may involve rounding or truncation.
    *
    * $returningNone
    *
    * $exceptions
    */
  def intOpt(idx: Int): Option[Int]

  /** Returns a [[Long]] from column with `name` name.
    *
    * All numeric types can be converted to [[Long]], but some conversions may involve rounding or truncation.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def long(name: String): Long

  /** Returns a [[Long]] from column with `name` name.
    *
    * All numeric types can be converted to [[Long]], but some conversions may involve rounding or truncation.
    *
    * $returningNone
    *
    * $exceptions
    */
  def longOpt(name: String): Option[Long]

  /** Returns a [[Long]] from column with `idx` index.
    *
    * All numeric types can be converted to [[Long]], but some conversions may involve rounding or truncation.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def long(idx: Int): Long

  /** Returns a [[Long]] from column with `idx` index.
    *
    * All numeric types can be converted to [[Long]], but some conversions may involve rounding or truncation.
    *
    * $returningNone
    *
    * $exceptions
    */
  def longOpt(idx: Int): Option[Long]

  /** Returns a [[BigDecimal]] from column with `name` name.
    *
    * All numeric types can be converted to [[BigDecimal]], not however that NaN value is not representable
    * by a [[BigDecimal]]. If you expect values to be NaN use `numeric` method instead.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def bigDecimal(name: String): BigDecimal

  /** Returns a [[BigDecimal]] from column with `name` name.
    *
    * All numeric types can be converted to [[BigDecimal]], not however that NaN value is not representable
    * by a [[BigDecimal]]. If you expect values to be NaN use `numeric` method instead.
    *
    * $returningNone
    *
    * $exceptions
    */
  def bigDecimalOpt(name: String): Option[BigDecimal]

  /** Returns a [[SqlNumeric]] from column with `name` name.
    *
    * All numeric types can be converted to [[SqlNumeric]].
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def numeric(name: String): SqlNumeric

  /** Returns a [[SqlNumeric]] from column with `name` name.
    *
    * All numeric types can be converted to [[SqlNumeric]].
    *
    * $returningNone
    *
    * $exceptions
    */
  def numericOpt(name: String): Option[SqlNumeric]

  /** Returns a [[Double]] from column with `name` name.
    *
    * All numeric types can be converted to [[Double]], but some conversions may involve rounding or truncation.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def double(name: String): Double

  /** Returns a [[Double]] from column with `name` name.
    *
    * All numeric types can be converted to [[Double]], but some conversions may involve rounding or truncation.
    *
    * $returningNone
    *
    * $exceptions
    */
  def doubleOpt(name: String): Option[Double]

  /** Returns a [[Float]] from column with `name` name.
    *
    * All numeric types can be converted to [[Float]], but some conversions may involve rounding or truncation.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def float(name: String): Float

  /** Returns a [[Float]] from column with `name` name.
    *
    * All numeric types can be converted to [[Float]], but some conversions may involve rounding or truncation.
    *
    * $returningNone
    *
    * $exceptions
    */
  def floatOpt(name: String): Option[Float]

  /** Returns an [[Instant]] from column with `name` name.
    *
    * Note that regular timestamp values are not convertible to an [[Instant]] because timestamp values
    * do not hold a time zone information.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def instant(name: String): Instant

  /** Returns an [[Instant]] from column with `name` name.
    *
    * Note that regular timestamp values are not convertible to an [[Instant]] because timestamp values
    * do not hold a time zone information.
    *
    * $returningNone
    *
    * $exceptions
    */
  def instantOpt(name: String): Option[Instant]

  /** Returns a [[LocalDateTime]] from column with `name` name.
    *
    * For SQL date type that does not hold a time, [[LocalDateTime]] at start of day is returned.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def localDateTime(name: String): LocalDateTime

  /** Returns a [[LocalDateTime]] from column with `name` name.
    *
    * For SQL date type that does not hold a time, [[LocalDateTime]] at start of day is returned.
    *
    * $returningNone
    *
    * $exceptions
    */
  def localDateTimeOpt(name: String): Option[LocalDateTime]

  /** Returns a [[ZonedDateTime]] from column with `name` name.
    *
    * If a database value represents a time instant, [[ZonedDateTime]] at default system time zone will be returned.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def zonedDateTime(name: String): ZonedDateTime

  /** Returns a [[ZonedDateTime]] from column with `name` name.
    *
    * If a database value represents a time instant, [[ZonedDateTime]] at default system time zone will be returned.
    *
    * $returningNone
    *
    * $exceptions
    */
  def zonedDateTimeOpt(name: String): Option[ZonedDateTime]

  /** Returns a [[LocalDate]] from column with `name` name.
    *
    * SQL types that represent a date with a time are convertible to [[LocalDate]] - a time part is truncated.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def localDate(name: String): LocalDate

  /** Returns a [[LocalDate]] from column with `name` name.
    *
    * SQL types that represent a date with a time are convertible to [[LocalDate]] - a time part is truncated.
    *
    * $returningNone
    *
    * $exceptions
    */
  def localDateOpt(name: String): Option[LocalDate]

  /** Returns a [[LocalDate]] from column with `name` name.
    *
    * SQL types that represent a date with a time are convertible to [[LocalTime]] - a date part is truncated.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def localTime(name: String): LocalTime

  /** Returns a [[LocalDate]] from column with `name` name.
    *
    * SQL types that represent a date with a time are convertible to [[LocalTime]] - a date part is truncated.
    *
    * $returningNone
    *
    * $exceptions
    */
  def localTimeOpt(name: String): Option[LocalTime]

  /** Returns a byte array from column with `name` name.
    *
    * Note that this method cannot be used to fetch raw value of any type from the database, it should be used
    * only to fetch binary data.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def bytes(name: String): Array[Byte]

  /** Returns a byte array from column with `name` name.
    *
    * Note that this method cannot be used to fetch raw value of any type from the database, it should be used
    * only to fetch binary data.
    *
    * $returningNone
    *
    * $exceptions
    */
  def bytesOpt(name: String): Option[Array[Byte]]

  /** Returns an [[UUID]] from column with `name` name.
    *
    * A string type with a standard UUID representation as defined by [[UUID.fromString]] is convertible to UUID.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def uuid(name: String): UUID

  /** Returns an [[UUID]] from column with `name` name.
    *
    * A string type with a standard UUID representation as defined by [[UUID.fromString]] is convertible to UUID.
    *
    * $returningNone
    *
    * $exceptions
    */
  def uuidOpt(name: String): Option[UUID]

  /** Returns an [[UUID]] from column with `idx` index.
    *
    * A string type with a standard UUID representation as defined by [[UUID.fromString]] is convertible to UUID.
    *
    * $nullSafetyNote
    *
    * $exceptions
    */
  def uuid(idx: Int): UUID

  /** Returns an [[UUID]] from column with `idx` index.
    *
    * A string type with a standard UUID representation as defined by [[UUID.fromString]] is convertible to UUID.
    *
    * $returningNone
    *
    * $exceptions
    */
  def uuidOpt(idx: Int): Option[UUID]
  //TODO support arrays?
  //TODO support streaming LOBs
}
