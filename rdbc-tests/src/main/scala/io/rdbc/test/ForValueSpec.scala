/*
 * Copyright 2016-2017 Krzysztof Pado
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

package io.rdbc.test

import io.rdbc.api.exceptions.ConversionException
import io.rdbc.sapi._

trait ForValueSpec
  extends RdbcSpec
    with TableSpec
    with TxSpec {

  protected def intDataTypeName: String
  override protected def columnsDefinition = s"col $intDataTypeName"

  //TODO if any test fails next tests fail too - fix this
  "Value returning feature should" - {
    "return None on empty tables" - {
      withAndWithoutTx { (c, t) =>
        val stmt = c.statement(sql"select col from #$t")
        stmt.executeForValue(_.int("col")).get shouldBe None
      }
    }

    "return value from first row on non-empty tables" - {
      withAndWithoutTx { (c, t) =>
        val range = 1 to 10
        for {i <- range} yield {
          c.statement(sql"insert into #$t(col) values ($i)").execute().get
        }
        val stmt = c.statement(sql"select col from #$t where col >= 5 order by col")
        stmt.executeForValue(_.int("col")).get should contain(5)
      }
    }

    "throw an exception when non null-safe getter is used" - {
      withAndWithoutTx { (c, t) =>
        c.statement(sql"insert into #$t(col) values (null)").execute().get
        val stmt = c.statement(sql"select col from #$t")
        val e = intercept[ConversionException] {
          stmt.executeForValue(_.int("col")).get
        }
        e.value shouldBe None
        e.targetType shouldBe classOf[Int]
      }
    }
  }

  //TODO tests for "connection should be in idle state when any method's future completes"

}
