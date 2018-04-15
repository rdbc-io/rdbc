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

package io.rdbc.util

import org.scalatest.Matchers

class PreconditionsSpec
  extends RdbcUtilSpec
    with Matchers {

  "Preconditions not null checking feature" when {
    "notNull check is used" should {
      def test(arg: String): Unit = {
        Preconditions.checkNotNull(arg)
      }

      "throw NPE for null argument" in {
        assertThrows[NullPointerException](test(null))
      }

      "not throw NPE for not-null argument" in {
        noException should be thrownBy test("notnull")
      }
    }
  }

  "Preconditions generic arg checking feature" should {
    val failMessage = "must be 'a'"

    def test(str: String): Unit = {
      Preconditions.check(str, str == "a", failMessage)
    }

    "throw IAE for failing checks" in {
      the[IllegalArgumentException] thrownBy {
        test("b")
      } should have message s"requirement failed: parameter 'str' $failMessage"
    }

    "not throw IAE for succeeding checks" in {
      noException should be thrownBy test("a")
    }
  }

  "Preconditions traversable emptiness checking feature" should {
    def test(vec: Vector[Int]): Unit = {
      Preconditions.checkNonEmpty(vec)
    }

    "throw IAE for empty traversables" in {
      the[IllegalArgumentException] thrownBy {
        test(Vector.empty)
      } should have message "requirement failed: parameter 'vec' cannot be empty"
    }

    "not throw IAE for succeeding checks" in {
      noException should be thrownBy test(Vector(1))
    }
  }

  "Preconditions string emptiness checking feature" should {
    def test(str: String): Unit = {
      Preconditions.checkNonEmptyString(str)
    }

    "throw IAE for empty strings" in {
      the[IllegalArgumentException] thrownBy {
        test("")
      } should have message "requirement failed: parameter 'str' cannot be empty"
    }

    "not throw IAE for succeeding checks" in {
      noException should be thrownBy test("nonempty")
    }
  }
}
