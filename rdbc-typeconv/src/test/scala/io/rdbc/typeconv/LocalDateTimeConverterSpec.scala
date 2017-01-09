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

import io.rdbc.sapi.exceptions.ConversionException

class LocalDateTimeConverterSpec
  extends RdbcTypeconvSpec {

  private val converter = LocalDateTimeConverter

  "LocalDateTimeConverter" should {

    "convert a LocalDateTime" in {
      val ldt = LocalDateTime.MIN
      converter.fromAny(ldt) shouldBe ldt
    }

    "convert a LocalDate" in {
      val ld = LocalDate.MIN
      converter.fromAny(ld) shouldBe LocalDateTime.of(ld, LocalTime.MIDNIGHT)
    }

    "fail for LocalTime" in {
      val lt = LocalTime.NOON
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(lt)
      }
      ex.value shouldBe lt
      ex.targetType shouldBe classOf[LocalDateTime]
    }

    "fail for Instant" in {
      val i = Instant.EPOCH
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(i)
      }
      ex.value shouldBe i
      ex.targetType shouldBe classOf[LocalDateTime]
    }

    "fail for ISO datetime string" in {
      val s = LocalDateTime.MIN.toString
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(s)
      }
      ex.value shouldBe s
      ex.targetType shouldBe classOf[LocalDateTime]
    }

    "fail for any" in {
      val a = new AnyRef
      val ex = the[ConversionException] thrownBy {
        converter.fromAny(a)
      }
      ex.value shouldBe a
      ex.targetType shouldBe classOf[LocalDateTime]
    }
  }
}
