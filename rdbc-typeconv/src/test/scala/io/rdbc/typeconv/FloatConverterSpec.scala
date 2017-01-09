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

import io.rdbc.sapi.SqlNumeric
import io.rdbc.sapi.exceptions.ConversionException
import org.scalatest.Inside

class FloatConverterSpec
  extends RdbcTypeconvSpec
    with Inside {

  private val converter = FloatConverter

  "FloatConverter" should {
    "convert BigDecimal" in {
      val bd = BigDecimal("42.2")
      converter.fromAny(bd) shouldEqual 42.2f
    }

    "convert Double" in {
      val d = 42.2d
      converter.fromAny(d) shouldEqual 42.2f
    }

    "convert Float" in {
      val f = 42.2f
      converter.fromAny(f) shouldEqual 42.2f
    }

    "convert Long" in {
      val l = 42L
      converter.fromAny(l) shouldEqual 42.0f
    }

    "convert Int" in {
      val i = 42
      converter.fromAny(i) shouldEqual 42.0f
    }

    "convert Short" in {
      val s = 42.toShort
      converter.fromAny(s) shouldEqual 42.0f
    }

    "convert Byte" in {
      val b = 42.toByte
      converter.fromAny(b) shouldEqual 42.0f
    }

    "convert finite SqlNumeric" in {
      val bd = BigDecimal("42.2")
      val s = SqlNumeric.Val(bd)
      converter.fromAny(s) shouldEqual 42.2f
    }

    "convert -inf SqlNumeric" in {
      val s = SqlNumeric.NegInfinity
      converter.fromAny(s) shouldBe Float.NegativeInfinity
    }

    "convert +inf SqlNumeric" in {
      val s = SqlNumeric.PosInfinity
      converter.fromAny(s) shouldBe Float.PositiveInfinity
    }

    "convert NaN SqlNumeric" in {
      val s = SqlNumeric.NaN
      inside(converter.fromAny(s)) { case f: Float =>
        f.isNaN === true
      }
    }

    "convert -inf Double" in {
      val d = Double.NegativeInfinity
      converter.fromAny(d) shouldBe Float.NegativeInfinity
    }

    "convert +inf Double" in {
      val d = Double.PositiveInfinity
      converter.fromAny(d) shouldBe Float.PositiveInfinity
    }

    "convert NaN Double" in {
      val d = Double.NaN
      inside(converter.fromAny(d)) { case f: Float =>
        f.isNaN === true
      }
    }

    "convert -inf Float" in {
      val f = Float.NegativeInfinity
      converter.fromAny(f) shouldBe Float.NegativeInfinity
    }

    "convert +inf Float" in {
      val f = Float.PositiveInfinity
      converter.fromAny(f) shouldBe Float.PositiveInfinity
    }

    "convert NaN Float" in {
      val f = Float.NaN
      inside(converter.fromAny(f)) { case f: Float =>
        f.isNaN === true
      }
    }

    "fail for string" in {
      val s = "42.2"
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(s)
      }
      ex.value shouldEqual s
      ex.targetType shouldEqual classOf[Float]
    }
  }
}
