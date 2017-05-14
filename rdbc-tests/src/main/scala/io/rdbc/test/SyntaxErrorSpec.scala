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

import io.rdbc.api.exceptions.InvalidQueryException
import io.rdbc.sapi._
import io.rdbc.test.util.Subscribers

import scala.concurrent.Future

trait SyntaxErrorSpec extends RdbcSpec {

  "Error should be returned when query is invalid when" - {
    stmtTest("Select", _.statement(sql"select * should_be_from tbl"))
    stmtTest("Insert", _.statement(sql"insert should_be_into tbl values (1)"))
    stmtTest("Returning insert", _.statement(sql"insert should_be_into tbl values (1)", StatementOptions.ReturnGenKeys))
    stmtTest("Delete", _.statement(sql"delete should_be_from tbl"))
    stmtTest("Update", _.statement(sql"update tbl should_be_set col = null"))
    stmtTest("DDL", _.statement(sql"alter should_be_table tbl drop column col"))
  }

  private def stmtTest(stmtType: String, stmt: Connection => Future[ExecutableStatement]): Unit = {
    s"executing a $stmtType for" - {
      executedFor("nothing", _.execute())
      executedFor("set", _.executeForSet())
      executedFor("value", _.executeForValue(_.int(1)))
      executedFor("first row", _.executeForFirstRow())
      executedFor("optional value", _.executeForValueOpt(_.intOpt(1)))
      executedFor("generated key", _.executeForKey[String])
      executedFor("stream", _.executeForStream().flatMap { rs =>
        val subscriber = Subscribers.eager()
        rs.rows.subscribe(subscriber)
        subscriber.rows
      })

      def executedFor[A](executorName: String, executor: ExecutableStatement => Future[A]): Unit = {
        executorName in { c =>
          withTable(c) {
            assertInvalidQueryThrown {
              stmt(c).flatMap(executor)
            }
          }
        }
      }
    }
  }


  private def assertInvalidQueryThrown(body: => Future[Any]): Unit = {
    assertThrows[InvalidQueryException](body.get)
  }


  private def withTable[A](c: Connection)(body: => A): A = {
    try {
      c.statement(s"create table tbl (col $arbitraryDataType)").get
        .noParams.execute().get
      body
    } finally {
      c.statement("drop table tbl").get
        .noParams.execute().get
    }
  }

  protected def arbitraryDataType: String
}
