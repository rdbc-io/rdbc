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

import io.rdbc.api.exceptions.NoSuchParamException
import io.rdbc.sapi.{Connection, Statement, StatementOptions}

import scala.concurrent.Future

trait NoSuchParamSpec extends RdbcSpec {

  private val superfluousParam = "yparam"
  private val param = "xparam"
  private val any: Any = 0

  "Statement should " - {
    "return a NoSuchParamException" - {
      "when too many params are provided" - {
        "when binding parameters by name" - {
          of("a select", _.statement(s"select * from tbl where x = :$param"))
          of("an insert", _.statement(s"insert into tbl values(:$param)"))
          of("an update", _.statement(s"update tbl set x = :$param"))
          of("a returning insert", _.statement(s"insert into tbl values(:$param)", StatementOptions.ReturnGenKeys))
          of("a delete", _.statement(s"delete from tbl where x = :$param"))
        }
      }
    }
  }

  private def of(stmtType: String, statement: Connection => Future[Statement]): Unit = {
    s"of $stmtType" - {
      "synchronously" in { c =>
        assertNoSuchParamThrown(c, _.bind(param -> any, superfluousParam -> any))
      }

      "asynchronously" in { c =>
        assertNoSuchParamThrown(c, _.bindF(param -> any, superfluousParam -> any).get)
      }
    }

    def assertNoSuchParamThrown(c: Connection, binder: Statement => Any): Unit = {
      val e = intercept[NoSuchParamException] {
        binder.apply(statement(c).get)
      }
      e.param shouldBe superfluousParam
    }
  }
}
