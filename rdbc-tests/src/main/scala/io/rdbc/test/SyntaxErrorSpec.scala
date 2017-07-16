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

package io.rdbc.test

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import io.rdbc.api.exceptions.InvalidQueryException
import io.rdbc.sapi._
import io.rdbc.test.util.Subscribers

import scala.concurrent.Future

trait SyntaxErrorSpec
  extends RdbcSpec
    with TableSpec {

  protected def arbitraryDataType: String

  protected implicit def system: ActorSystem
  protected implicit def materializer: Materializer

  "Error should be returned when query is invalid when" - {
    stmtTest("Select", (c, t) => c.statement(sql"select * should_be_from #$t"))
    stmtTest("Insert", (c, t) => c.statement(sql"insert should_be_into #$t values (1)"))
    stmtTest("Returning insert", (c, t) =>
      c.statement(sql"insert should_be_into #$t values (1)", StatementOptions.ReturnGenKeys)
    )
    stmtTest("Delete", (c, t) => c.statement(sql"delete should_be_from #$t"))
    stmtTest("Update", (c, t) => c.statement(sql"update #$t should_be_set col = null"))
    stmtTest("DDL", (c, t) => c.statement(sql"alter should_be_table #$t drop column col"))
  }

  "Streaming arguments should" - {
    "fail with an InvalidQueryException" - {
      "when statement is incorrect syntactically" in { c =>
        withTable(c, s"col $arbitraryDataType") { tbl =>
          val stmt = c.statement(s"insert should_be_into #$tbl values (:x)")
          val src = Source(Vector(Vector(1), Vector(2))).runWith(Sink.asPublisher(fanout = false))

          assertInvalidQueryThrown {
            stmt.streamArgsByIdx(src).get
          }
        }
      }
    }
  }

  private def stmtTest(stmtType: String, stmt: (Connection, String) => ExecutableStatement): Unit = {
    s"executing a $stmtType for" - {
      executedFor("nothing", _.execute())
      executedFor("set", _.executeForSet())
      executedFor("value", _.executeForValue(_.int(1)))
      executedFor("first row", _.executeForFirstRow())
      executedFor("generated key", _.executeForKey[String])
      executedFor("stream", stmt => {
        val rs = stmt.stream()
        val subscriber = Subscribers.eager()
        rs.subscribe(subscriber)
        subscriber.rows
      })

      def executedFor[A](executorName: String, executor: ExecutableStatement => Future[A]): Unit = {
        executorName in { c =>
          withTable(c, s"col $arbitraryDataType") { tbl =>
            assertInvalidQueryThrown {
              executor(stmt(c, tbl)).get
            }
          }
        }
      }
    }
  }


  private def assertInvalidQueryThrown(body: => Any): Unit = {
    assertThrows[InvalidQueryException](body)
  }
}
