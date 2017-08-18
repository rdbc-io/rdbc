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

import java.time.{Instant, LocalDate, LocalDateTime, LocalTime}

import io.rdbc.api.exceptions.ConversionException

class LocalTimeConverterSpec
  extends RdbcTypeconvSpec {

  private val converter = LocalTimeConverter

  "LocalTimeConverter" should {

    "convert a LocalTime" in {
      val lt = LocalTime.NOON
      converter.fromAny(lt) shouldBe lt
    }

    "convert a LocalDateTime" in {
      val lt = LocalTime.NOON
      val ldt = LocalDateTime.of(LocalDate.MIN, lt)
      converter.fromAny(ldt) shouldBe lt
    }

    "fail for LocalDate" in {
      val ld = LocalDate.MIN
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(ld)
      }
      ex.value shouldBe ld
      ex.targetType shouldBe classOf[LocalTime]
    }

    "fail for Instant" in {
      val i = Instant.EPOCH
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(i)
      }
      ex.value shouldBe i
      ex.targetType shouldBe classOf[LocalTime]
    }

    "fail for ISO time string" in {
      val s = LocalTime.NOON.toString
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(s)
      }
      ex.value shouldBe s
      ex.targetType shouldBe classOf[LocalTime]
    }

    "fail for any" in {
      val a = new AnyRef
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(a)
      }
      ex.value shouldBe a
      ex.targetType shouldBe classOf[LocalTime]
    }
  }
}
