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

package io.rdbc.typeconv

import io.rdbc.api.exceptions.ConversionException
import io.rdbc.sapi.{SqlNumeric, TypeConverter}

object NumericConverter extends TypeConverter[SqlNumeric] {
  val cls = classOf[SqlNumeric]

  override def fromAny(any: Any): SqlNumeric = any match {
    case n: SqlNumeric => n
    case bd: BigDecimal => SqlNumeric.Val(bd)
    case d: Double =>
      if (d.isNaN) SqlNumeric.NaN
      else if (d.isPosInfinity) SqlNumeric.PosInfinity
      else if (d.isNegInfinity) SqlNumeric.NegInfinity
      else SqlNumeric.Val(BigDecimal(d))

    case f: Float =>
      if (f.isNaN) SqlNumeric.NaN
      else if (f.isPosInfinity) SqlNumeric.PosInfinity
      else if (f.isNegInfinity) SqlNumeric.NegInfinity
      else SqlNumeric.Val(BigDecimal(f.toDouble))

    case l: Long => SqlNumeric.Val(BigDecimal(l))
    case i: Int => SqlNumeric.Val(BigDecimal(i))
    case s: Short => SqlNumeric.Val(BigDecimal(s.toInt))
    case b: Byte => SqlNumeric.Val(BigDecimal(b.toInt))

    case str: String =>
      try {
        SqlNumeric.Val(BigDecimal.exact(str))
      } catch {
        case _: NumberFormatException => throw new ConversionException(any, classOf[SqlNumeric])
      }

    case _ => throw new ConversionException(any, classOf[SqlNumeric])
  }
}
