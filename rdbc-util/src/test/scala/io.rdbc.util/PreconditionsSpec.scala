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

class PreconditionsSpec extends RdbcUtilSpec {

  "Preconditions not null checking feature" when {
    "executed for single argument" should {

      def test(arg: String): Unit = {
        Preconditions.notNull(arg)
      }

      "throw NPE for null argument" in {
        assertThrows[NullPointerException](test(null))
      }

      "not throw NPE for not-null argument" in {
        test("notnull")
      }
    }

    "executed for multiple arguments" should {

      def test(arg1: String, arg2: String): Unit = {
        Preconditions.notNull(arg1, arg2)
      }

      "throw NPE for all null arguments" in {
        assertThrows[NullPointerException](test(null, null))
      }

      "throw NPE for first null argument" in {
        assertThrows[NullPointerException](test(null, "notnull"))
      }

      "throw NPE for second null argument" in {
        assertThrows[NullPointerException](test("notnull", null))
      }

      "not throw NPE for not-null arguments" in {
        test("notnull1", "notnull2")
      }
    }
  }
}
