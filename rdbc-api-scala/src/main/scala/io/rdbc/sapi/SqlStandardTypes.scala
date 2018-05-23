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

package io.rdbc.sapi

import java.time._

import io.rdbc.ImmutSeq

import scala.reflect.ClassTag

/** SQL CHARACTER */
final case class SqlChar(value: String)

/** SQL CHARACTER VARYING */
final case class SqlVarchar(value: String)

/** SQL CHARACTER LARGE OBJECT */
final case class SqlClob(value: String)

/** SQL NATIONAL CHARACTER */
final case class SqlNChar(value: String)

/** SQL NATIONAL CHARACTER VARYING */
final case class SqlNVarchar(value: String)

/** SQL NATIONAL CHARACTER LARGE OBJECT */
final case class SqlNClob(value: String)

/** SQL BINARY */
final case class SqlBinary(value: ImmutSeq[Byte])

/** SQL BINARY VARYING */
final case class SqlVarbinary(value: ImmutSeq[Byte])

/** SQL BINARY LARGE OBJECT */
final case class SqlBlob(value: ImmutSeq[Byte])

/** SQL NUMERIC type */
final case class SqlNumeric(value: DecimalNumber)

/** SQL DECIMAL type */
final case class SqlDecimal(value: DecimalNumber)

/** SQL SMALLINT type */
final case class SqlSmallInt(value: Short)

/** SQL INTEGER type */
final case class SqlInteger(value: Int)

/** SQL BIGINT type */
final case class SqlBigInt(value: Long)

/** SQL FLOAT type */
final case class SqlFloat(value: Float)

/** SQL REAL type */
final case class SqlReal(value: Float)

/** SQL DOUBLE PRECISION type */
final case class SqlDouble(value: Double)

/** SQL BOOLEAN type */
final case class SqlBoolean(value: Boolean)

/** SQL DATE type */
final case class SqlDate(value: LocalDate)

/** SQL TIME WITHOUT TIME ZONE type */
final case class SqlTime(value: LocalTime)

/** SQL TIMESTAMP type */
final case class SqlTimestamp(value: LocalDateTime)

/** SQL TIMESTAMP WITH TIME ZONE type */
final case class SqlTimestampTz(value: OffsetDateTime)

/** SQL INTERVAL type */
final case class SqlInterval(value: Period)

object SqlNull {
  def of[A: ClassTag]: SqlNull[A] = {
    SqlNull(implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]])
  }
}

final case class SqlNull[T](cls: Class[T])
