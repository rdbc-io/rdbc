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

import java.util.UUID

import io.rdbc.api.exceptions.ConversionException

class UuidConverterSpec
  extends RdbcTypeconvSpec {

  private val converter = UuidConverter

  "UuidConverter" should {

    "convert an UUID" in {
      val u = UUID.fromString("ae323bc6-8455-11e7-98cb-54a050d6642a")
      converter.fromAny(u) shouldBe u
    }

    "convert string representing an UUID" in {
      val s = "ae323bc6-8455-11e7-98cb-54a050d6642a"
      converter.fromAny(s) shouldBe UUID.fromString(s)
    }

    "fail for strings not representing UUID" in {
      val s = "XXXXXX"
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(s)
      }
      ex.value shouldBe s
      ex.targetType shouldBe classOf[UUID]
    }

    "fail for any" in {
      val a = new AnyRef
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(a)
      }
      ex.value shouldBe a
      ex.targetType shouldBe classOf[UUID]
    }
  }
}
