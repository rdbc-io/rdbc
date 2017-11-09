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

class CharConverterSpec
  extends RdbcTypeconvSpec {

  private val converter = CharConverter

  "CharConverter" should {

    "convert a Char" in {
      val c = 'c'
      converter.fromAny(c) shouldBe c
    }

    "convert one-char string" in {
      converter.fromAny("c") shouldBe 'c'
    }

    "fail for strings of size > 1" in {
      val s = "cc"
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(s)
      }
      ex.value shouldBe s
      ex.targetType shouldBe classOf[Char]
    }

    "fail for any" in {
      val a = new AnyRef
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(a)
      }
      ex.value shouldBe a
      ex.targetType shouldBe classOf[Char]
    }
  }
}
