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

import io.rdbc.sapi.exceptions.ConversionException
import io.rdbc.sapi.{DecimalNumber, TypeConverter}

object NumericConverter extends TypeConverter[DecimalNumber] {
  val cls = classOf[DecimalNumber]

  override def fromAny(any: Any): DecimalNumber = any match {
    case n: DecimalNumber => n
    case bd: BigDecimal => DecimalNumber.Val(bd)
    case d: Double =>
      if (d.isNaN) DecimalNumber.NaN
      else if (d.isPosInfinity) DecimalNumber.PosInfinity
      else if (d.isNegInfinity) DecimalNumber.NegInfinity
      else DecimalNumber.Val(BigDecimal(d))

    case f: Float =>
      if (f.isNaN) DecimalNumber.NaN
      else if (f.isPosInfinity) DecimalNumber.PosInfinity
      else if (f.isNegInfinity) DecimalNumber.NegInfinity
      else DecimalNumber.Val(BigDecimal(f.toDouble))

    case l: Long => DecimalNumber.Val(BigDecimal(l))
    case i: Int => DecimalNumber.Val(BigDecimal(i))
    case s: Short => DecimalNumber.Val(BigDecimal(s.toInt))
    case b: Byte => DecimalNumber.Val(BigDecimal(b.toInt))

    case str: String =>
      try {
        DecimalNumber.Val(BigDecimal.exact(str))
      } catch {
        case _: NumberFormatException => throw new ConversionException(any, classOf[DecimalNumber])
      }

    case _ => throw new ConversionException(any, classOf[DecimalNumber])
  }
}
