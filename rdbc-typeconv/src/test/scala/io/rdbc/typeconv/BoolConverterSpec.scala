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

class BoolConverterSpec
  extends RdbcTypeconvSpec {

  private val converter = BoolConverter

  "BoolConverter" should {
    "convert false as false" in {
      converter.fromAny(false) shouldBe false
    }

    "convert true as true" in {
      converter.fromAny(true) shouldBe true
    }

    "convert 0 Long as false" in {
      converter.fromAny(0L) shouldBe false
    }

    "convert 1 Long as true" in {
      converter.fromAny(1L) shouldBe true
    }

    "convert 0 Int as false" in {
      converter.fromAny(0) shouldBe false
    }

    "convert 1 Int as true" in {
      converter.fromAny(1) shouldBe true
    }

    "convert 0 Short as false" in {
      converter.fromAny(0.toShort) shouldBe false
    }

    "convert 1 Short as true" in {
      converter.fromAny(1.toShort) shouldBe true
    }

    "convert 0 Byte as false" in {
      converter.fromAny(0.toByte) shouldBe false
    }

    "convert 1 Byte as true" in {
      converter.fromAny(1.toByte) shouldBe true
    }

    "convert 0 SqlNumeric as false" in {
      converter.fromAny(SqlNumeric.Val(BigDecimal(0))) shouldBe false
    }

    "convert 1 SqlNumeric as true" in {
      converter.fromAny(SqlNumeric.Val(BigDecimal(1))) shouldBe true
    }

    "convert 0 BigDecimal as false" in {
      converter.fromAny(BigDecimal(0)) shouldBe false
    }

    "convert 1 BigDecimal as true" in {
      converter.fromAny(BigDecimal(1)) shouldBe true
    }

    "convert 'N' as false" in {
      converter.fromAny('N') shouldBe false
      converter.fromAny("N") shouldBe false
    }

    "convert 'Y' as true" in {
      converter.fromAny('Y') shouldBe true
      converter.fromAny("Y") shouldBe true
    }

    "convert 'T' as true" in {
      converter.fromAny('T') shouldBe true
      converter.fromAny("T") shouldBe true
    }

    "convert 'F' as false" in {
      converter.fromAny('F') shouldBe false
      converter.fromAny("F") shouldBe false
    }

    "convert '0' as false" in {
      converter.fromAny('0') shouldBe false
      converter.fromAny("0") shouldBe false
    }

    "convert '1' as true" in {
      converter.fromAny('1') shouldBe true
      converter.fromAny("1") shouldBe true
    }

    "fail for int greater than 1" in {
      val i = 42
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(i)
      }
      ex.value shouldBe i
      ex.targetType shouldBe classOf[Boolean]
    }

    "fail for non convertible Char" in {
      val c = 'X'
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(c)
      }
      ex.value shouldBe c
      ex.targetType shouldBe classOf[Boolean]
    }

    "fail for strings of size > 1" in {
      val s = "TT"
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(s)
      }
      ex.value shouldBe s
      ex.targetType shouldBe classOf[Boolean]
    }

    "fail for any" in {
      val a = new AnyRef
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(a)
      }
      ex.value shouldBe a
      ex.targetType shouldBe classOf[Boolean]
    }
  }
}
