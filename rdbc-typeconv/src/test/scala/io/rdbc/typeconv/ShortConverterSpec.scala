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
import org.scalatest.Inside

class ShortConverterSpec
  extends RdbcTypeconvSpec
    with Inside {

  private val converter = ShortConverter

  "ShortConverter" should {
    "convert BigDecimal" in {
      val bd = BigDecimal("42.2")
      converter.fromAny(bd) shouldBe 42.toShort
    }

    "convert Double" in {
      val d = 42.2d
      converter.fromAny(d) shouldBe 42.toShort
    }

    "convert Float" in {
      val f = 42.2f
      converter.fromAny(f) shouldBe 42.toShort
    }

    "convert Long" in {
      val l = 42L
      converter.fromAny(l) shouldBe 42.toShort
    }

    "convert Int" in {
      val i = 42
      converter.fromAny(i) shouldBe 42.toShort
    }

    "convert Short" in {
      val s = 42.toShort
      converter.fromAny(s) shouldBe 42.toShort
    }

    "convert Byte" in {
      val b = 42.toByte
      converter.fromAny(b) shouldBe 42.toShort
    }

    "convert finite SqlNumeric" in {
      val bd = BigDecimal("42.2")
      val s = SqlNumeric.Val(bd)
      converter.fromAny(s) shouldBe 42.toShort
    }

    "fail for -inf SqlNumeric" in {
      val s = SqlNumeric.NegInfinity
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(s)
      }
      ex.value shouldBe s
      ex.targetType shouldBe classOf[Short]
    }

    "fail for +inf SqlNumeric" in {
      val s = SqlNumeric.PosInfinity
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(s)
      }
      ex.value shouldBe s
      ex.targetType shouldBe classOf[Short]
    }

    "fail for NaN SqlNumeric" in {
      val s = SqlNumeric.NaN
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(s)
      }
      ex.value shouldBe s
      ex.targetType shouldBe classOf[Short]
    }

    "fail for -inf Float" in {
      val f = Float.NegativeInfinity
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(f)
      }
      ex.value shouldBe f
      ex.targetType shouldBe classOf[Short]
    }

    "fail for +inf Float" in {
      val f = Float.PositiveInfinity
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(f)
      }
      ex.value shouldBe f
      ex.targetType shouldBe classOf[Short]
    }

    "fail for NaN Float" in {
      val f = Float.NaN
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(f)
      }
      inside(ex.value) { case f: Float => f.isNaN === true }
      ex.targetType shouldBe classOf[Short]
    }

    "fail for -inf Double" in {
      val d = Double.NegativeInfinity
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(d)
      }
      ex.value shouldBe d
      ex.targetType shouldBe classOf[Short]
    }

    "fail for +inf Double" in {
      val d = Double.PositiveInfinity
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(d)
      }
      ex.value shouldBe d
      ex.targetType shouldBe classOf[Short]
    }

    "fail for NaN Double" in {
      val d = Double.NaN
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(d)
      }
      inside(ex.value) { case d: Double => d.isNaN === true }
      ex.targetType shouldBe classOf[Short]
    }

    "fail for string" in {
      val s = "42"
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(s)
      }
      ex.value shouldBe s
      ex.targetType shouldBe classOf[Short]
    }
  }
}
