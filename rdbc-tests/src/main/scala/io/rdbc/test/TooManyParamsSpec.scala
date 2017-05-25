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

import io.rdbc.api.exceptions.TooManyParamsException
import io.rdbc.sapi.{Connection, Statement, StatementOptions}

import scala.concurrent.Future

trait TooManyParamsSpec extends RdbcSpec {

  private val any: Any = 0

  "Bindable should " - {
    "throw a TooManyParamsException" - {
      "when too many params are provided" - {
        "when binding parameters by index" - {
          of("a select", _.statement("select * from tbl where x = :xparam"))
          of("an insert", _.statement("insert into tbl values(:xparam)"))
          of("an update", _.statement("update tbl set x = :xparam"))
          of("a returning insert", _.statement("insert into tbl values(:xparam)", StatementOptions.ReturnGenKeys))
          of("a delete", _.statement("delete from tbl where x = :xparam"))
        }
      }
    }
  }

  private def of(stmtType: String, bindable: Connection => Statement): Unit = {
    s"of $stmtType" in { c =>
        assertTooManyParamsThrown(c, _.bindByIdx(any, any))
    }

    def assertTooManyParamsThrown(c: Connection, binder: Statement => Any): Unit = {
      val e = intercept[TooManyParamsException] {
        binder.apply(bindable(c))
      }
      e.provided shouldBe 2
      e.expected shouldBe 1
    }
  }
}
