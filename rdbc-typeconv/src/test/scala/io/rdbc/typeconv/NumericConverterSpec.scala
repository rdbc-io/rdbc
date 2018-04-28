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

import io.rdbc.sapi.DecimalNumber
import io.rdbc.sapi.exceptions.ConversionException

class NumericConverterSpec
  extends RdbcTypeconvSpec {

  private val converter = NumericConverter

  "NumericConverter" should {
    "convert BigDecimal" in {
      val bd = BigDecimal("42.2")
      converter.fromAny(bd) shouldBe DecimalNumber.Val(bd)
    }

    "convert Double" in {
      val d = 42.2d
      converter.fromAny(d) shouldBe DecimalNumber.Val(BigDecimal(d))
    }

    "convert Float" in {
      val f = 42.2f
      converter.fromAny(f) shouldBe DecimalNumber.Val(BigDecimal(f.toDouble))
    }

    "convert Long" in {
      val l = 42L
      converter.fromAny(l) shouldBe DecimalNumber.Val(BigDecimal(l))
    }

    "convert Int" in {
      val i = 42
      converter.fromAny(i) shouldBe DecimalNumber.Val(BigDecimal(i))
    }

    "convert Short" in {
      val s = 42.toShort
      converter.fromAny(s) shouldBe DecimalNumber.Val(BigDecimal(s.toInt))
    }

    "convert Byte" in {
      val b = 42.toByte
      converter.fromAny(b) shouldBe DecimalNumber.Val(BigDecimal(b.toInt))
    }

    "convert finite DecimalNumber" in {
      val s = DecimalNumber.Val(BigDecimal("42.2"))
      converter.fromAny(s) shouldBe s
    }

    "convert -inf DecimalNumber" in {
      val s = DecimalNumber.NegInfinity
      converter.fromAny(s) shouldBe DecimalNumber.NegInfinity
    }

    "convert +inf DecimalNumber" in {
      val s = DecimalNumber.PosInfinity
      converter.fromAny(s) shouldBe DecimalNumber.PosInfinity
    }

    "convert -inf Double" in {
      val d = Double.NegativeInfinity
      converter.fromAny(d) shouldBe DecimalNumber.NegInfinity
    }

    "convert +inf Double" in {
      val d = Double.PositiveInfinity
      converter.fromAny(d) shouldBe DecimalNumber.PosInfinity
    }

    "convert NaN Double" in {
      val d = Double.NaN
      converter.fromAny(d) shouldBe DecimalNumber.NaN
    }

    "convert -inf Float" in {
      val f = Float.NegativeInfinity
      converter.fromAny(f) shouldBe DecimalNumber.NegInfinity
    }

    "convert +inf Float" in {
      val f = Float.PositiveInfinity
      converter.fromAny(f) shouldBe DecimalNumber.PosInfinity
    }

    "convert NaN Float" in {
      val f = Float.NaN
      converter.fromAny(f) shouldBe DecimalNumber.NaN
    }

    "convert string if it represents a number" in {
      val s = "42.2"
      converter.fromAny(s) shouldBe DecimalNumber.Val(BigDecimal(s))
    }

    "fail for string not representing a number" in {
      val s = "str"
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(s)
      }
      ex.value shouldBe s
      ex.targetType shouldBe classOf[DecimalNumber]
    }

    "fail for any" in {
      val a = new AnyRef
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(a)
      }
      ex.value shouldBe a
      ex.targetType shouldBe classOf[DecimalNumber]
    }
  }
}
