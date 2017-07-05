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

import io.rdbc.sapi.Interpolators._

class SqlInterpolatorSpec extends RdbcSpec {

  "SQL string interpolator" should {
    "work without parameters" in {
      val res = sql"select * from table"
      res.sql shouldBe "select * from table"
      res.params shouldBe empty
    }

    "insert parameter placeholders at the beginning" in {
      val p1 = 1
      val res = sql"$p1 rest of sql"
      res.sql shouldBe "? rest of sql"
      res.params shouldBe Seq(p1)
    }

    "insert parameter placeholders at the end" in {
      val p1 = 1
      val res = sql"select * from table where x = $p1"
      res.sql shouldBe "select * from table where x = ?"
      res.params shouldBe Seq(p1)
    }

    "insert parameter placeholders in the middle" in {
      val p1 = 1
      val res = sql"select * from table where x = $p1 and y = 1"
      res.sql shouldBe "select * from table where x = ? and y = 1"
      res.params shouldBe Seq(p1)
    }

    "work for multiple parameters" in {
      val (p1, p2, p3) = (1, 2, 3)
      val res = sql"select $p1 from table where x = $p2 and y = $p3"
      res.sql shouldBe "select ? from table where x = ? and y = ?"
      res.params shouldBe Seq(p1, p2, p3)
    }

    "handle #$ syntax for dynamic SQL" in {
      val table = "tbl"
      val (p1, p2, p3) = (1, 2, 3)
      val res = sql"select $p1 from #$table where x = $p2 and y = $p3"
      res.sql shouldBe s"select ? from $table where x = ? and y = ?"
      res.params shouldBe Seq(p1, p2, p3)
    }

  }

}
