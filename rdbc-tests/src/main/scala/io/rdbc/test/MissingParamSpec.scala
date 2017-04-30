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

import io.rdbc.api.exceptions.MissingParamValException
import io.rdbc.sapi.{Connection, KeyColumns, Statement, StatementOptions}

import scala.concurrent.Future

trait MissingParamSpec extends RdbcSpec {

  private val missingParam = "yparam"
  private val otherParam = "xparam"
  private val any: Any = 0

  "Statement should " - {
    "return a MissingParamValException" - {
      "when not all params are provided" - {
        "when binding parameters" - {
          of("a select", _.statement(s"select * from tbl where x = :$otherParam, y = :$missingParam"))
          of("an insert", _.statement(s"insert into tbl values(:$otherParam, :$missingParam)"))
          of("an update", _.statement(s"update tbl set x = :$otherParam, y = :$missingParam"))
          of("a returning insert",
            _.statement(s"insert into tbl values(:$otherParam, :$missingParam)",
            StatementOptions.ReturnGenKeys)
          )
          of("a delete", _.statement(s"delete from tbl where x = :$otherParam and y = :$missingParam)"))
        }
      }
    }
  }

  private def of(stmtType: String, statement: Connection => Future[Statement]): Unit = {
    s"of $stmtType" - {
      "synchronously" - {
        "by name" in { c =>
          assertMissingParamThrown(c, _.bind(otherParam -> any))
        }

        "by index" in { c =>
          assertMissingParamThrown(c, _.bindByIdx(any))
        }

        "providing no params" in { c =>
          assertAnyMissingParamThrown(c, _.noParams)
        }
      }

      "asynchronously" - {
        "by name" in { c =>
          assertMissingParamThrown(c, _.bindF(otherParam -> any).get)
        }

        "by index" in { c =>
          assertMissingParamThrown(c, _.bindByIdxF(any).get)
        }

        "providing no params" in { c =>
          assertAnyMissingParamThrown(c, _.noParamsF.get)
        }
      }
    }

    def assertMissingParamThrown(c: Connection, binder: Statement => Any): Unit = {
      val e = intercept[MissingParamValException] {
        binder.apply(statement(c).get)
      }
      e.missingParam shouldBe missingParam
    }

    def assertAnyMissingParamThrown(c: Connection, binder: Statement => Any): Unit = {
      val e = intercept[MissingParamValException] {
        binder.apply(statement(c).get)
      }
      e.missingParam should (equal(missingParam) or equal(otherParam))
    }
  }
}
