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

trait ResultSetSpec
  extends RdbcSpec
    with TableSpec
    with TxSpec {

  protected def intDataTypeName: String
  protected def intDataTypeId: String

  private def columnsDefinition = s"col $intDataTypeName"

  //TODO if any test fails next tests fail too - fix this
  "Set returning feature should" - {
    "work on empty tables" - {
      withAndWithoutTx(columnsDefinition) { (c, t) =>
        c.statement(sql"select col from #$t").executeForSet().get.rows shouldBe empty
      }
    }

    "be able to fetch all rows at once" - {
      withAndWithoutTx(columnsDefinition) { (c, t) =>
        val range = 1 to 10
        for {i <- range} yield {
          c.statement(sql"insert into #$t(col) values ($i)").execute().get
        }
        val rows = c.statement(sql"select col from #$t order by col").executeForSet().get.rows
        rows should have size range.size.toLong
        rows.map(_.int("col")) should contain theSameElementsInOrderAs range
      }
    }

    "return metadata" - {
      "about rows affected" - {
        withAndWithoutTx(columnsDefinition) { (c, t) =>
          val range = 1 to 10
          for {i <- range} yield {
            c.statement(sql"insert into #$t(col) values ($i)").execute().get
          }
          val rs = c.statement(sql"update #$t set col = null where col >= 6").executeForSet().get
          rs.rowsAffected shouldBe 5L
        }
      }

      "about columns" - {
        withAndWithoutTx(columnsDefinition) { (c, t) =>
          val range = 1 to 10
          for {i <- range} yield {
            c.statement(sql"insert into #$t(col) values ($i)").execute().get
          }
          val rs = c.statement(sql"select col from #$t").executeForSet().get
          rs.metadata.columns should have size 1

          val colMetadata = rs.metadata.columns.head
          colMetadata.name shouldBe "col"
          colMetadata.dbTypeId shouldBe intDataTypeId
        }
      }
    }
  }
}
