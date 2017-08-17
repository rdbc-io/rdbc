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
import io.rdbc.sapi.SqlNumeric

class NumericConverterSpec
  extends RdbcTypeconvSpec {

  private val converter = NumericConverter

  "NumericConverter" should {
    "convert BigDecimal" in {
      val bd = BigDecimal("42.2")
      converter.fromAny(bd) shouldBe SqlNumeric.Val(bd)
    }

    "convert Double" in {
      val d = 42.2d
      converter.fromAny(d) shouldBe SqlNumeric.Val(BigDecimal(d))
    }

    "convert Float" in {
      val f = 42.2f
      converter.fromAny(f) shouldBe SqlNumeric.Val(BigDecimal(f.toDouble))
    }

    "convert Long" in {
      val l = 42L
      converter.fromAny(l) shouldBe SqlNumeric.Val(BigDecimal(l))
    }

    "convert Int" in {
      val i = 42
      converter.fromAny(i) shouldBe SqlNumeric.Val(BigDecimal(i))
    }

    "convert Short" in {
      val s = 42.toShort
      converter.fromAny(s) shouldBe SqlNumeric.Val(BigDecimal(s.toInt))
    }

    "convert Byte" in {
      val b = 42.toByte
      converter.fromAny(b) shouldBe SqlNumeric.Val(BigDecimal(b.toInt))
    }

    "convert finite SqlNumeric" in {
      val s = SqlNumeric.Val(BigDecimal("42.2"))
      converter.fromAny(s) shouldBe s
    }

    "convert -inf SqlNumeric" in {
      val s = SqlNumeric.NegInfinity
      converter.fromAny(s) shouldBe SqlNumeric.NegInfinity
    }

    "convert +inf SqlNumeric" in {
      val s = SqlNumeric.PosInfinity
      converter.fromAny(s) shouldBe SqlNumeric.PosInfinity
    }

    "convert -inf Double" in {
      val d = Double.NegativeInfinity
      converter.fromAny(d) shouldBe SqlNumeric.NegInfinity
    }

    "convert +inf Double" in {
      val d = Double.PositiveInfinity
      converter.fromAny(d) shouldBe SqlNumeric.PosInfinity
    }

    "convert NaN Double" in {
      val d = Double.NaN
      converter.fromAny(d) shouldBe SqlNumeric.NaN
    }

    "convert -inf Float" in {
      val f = Float.NegativeInfinity
      converter.fromAny(f) shouldBe SqlNumeric.NegInfinity
    }

    "convert +inf Float" in {
      val f = Float.PositiveInfinity
      converter.fromAny(f) shouldBe SqlNumeric.PosInfinity
    }

    "convert NaN Float" in {
      val f = Float.NaN
      converter.fromAny(f) shouldBe SqlNumeric.NaN
    }

    "convert string if it represents a number" in {
      val s = "42.2"
      converter.fromAny(s) shouldBe SqlNumeric.Val(BigDecimal(s))
    }

    "fail for string not representing a number" in {
      val s = "str"
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(s)
      }
      ex.value shouldBe s
      ex.targetType shouldBe classOf[SqlNumeric]
    }

    "fail for any" in {
      val a = new AnyRef
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(a)
      }
      ex.value shouldBe a
      ex.targetType shouldBe classOf[SqlNumeric]
    }
  }
}
