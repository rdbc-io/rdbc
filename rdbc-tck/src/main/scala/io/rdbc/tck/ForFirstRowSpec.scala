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

package io.rdbc.tck

import io.rdbc.sapi._

trait ForFirstRowSpec
  extends RdbcSpec
    with TableSpec
    with TxSpec {

  protected def intDataTypeName: String
  private def columnsDefinition = s"col $intDataTypeName"

  //TODO if any test fails next tests fail too - fix this
  "First row returning feature should" - {
    "return None on empty tables" - {
      withAndWithoutTx(columnsDefinition) { (c, t) =>
        val stmt = c.statement(sql"select col from #$t")
        stmt.executeForFirstRow().get shouldBe None
      }
    }

    "return first row on non-empty tables" - {
      withAndWithoutTx(columnsDefinition) { (c, t) =>
        val range = 1 to 10
        for {i <- range} yield {
          c.statement(sql"insert into #$t(col) values ($i)").execute().get
        }
        val stmt = c.statement(sql"select col from #$t where col >= 5 order by col")
        val maybeRs = stmt.executeForFirstRow().get
        maybeRs shouldBe defined
        maybeRs.get.int("col") shouldBe 5
      }
    }
  }

  //TODO insert into #$t(col) values (:v) error test
  //TODO tests for "connection should be in idle state when any method's future completes (with exceptions too)"
}
