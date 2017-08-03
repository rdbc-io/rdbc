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

package io.rdbc

import io.rdbc.sapi.SqlInterpolator._

class SqlWithParamsSpec extends RdbcSpec {

  "SqlWithParams" should {
    "handle concatenation" when {

      "no params + no params" in {
        val part1 = sql"select * from "
        val part2 = sql"table where x = 1"

        val res = part1 + part2
        res.sql shouldBe "select * from table where x = 1"
        res.params shouldBe empty
      }

      "params + params" in {
        val (p1, p2) = (1, 2)
        val part1 = sql"select $p1 from "
        val part2 = sql"table where x = $p2"

        val res = part1 + part2
        res.sql shouldBe s"select ? from table where x = ?"
        res.params shouldBe Seq(p1, p2)
      }

      "dynamic SQL is used" in {
        val tbl = "table"
        val (p1, p2) = (1, 2)
        val part1 = sql"select $p1 from "
        val part2 = sql"#$tbl where x = $p2"

        val res = part1 + part2
        res.sql shouldBe s"select ? from table where x = ?"
        res.params shouldBe Seq(p1, p2)
      }

    }
  }

}
