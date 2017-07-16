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
import io.rdbc.api.exceptions.NoSuchParamException
import io.rdbc.sapi.{Connection, Statement, StatementOptions}

import scala.collection.immutable.ListMap

trait NoSuchParamSpec
  extends RdbcSpec
    with TableSpec {

  private val superfluousParam = "yparam"
  private val param = "xparam"
  private val any: Any = 0

  protected def arbitraryDataType: String

  protected implicit def system: ActorSystem
  protected implicit def materializer: Materializer

  "Statement should " - {
    "return a NoSuchParamException" - {
      "when too many params are provided" - {
        "when binding parameters by name" - {
          stmtTest("a select", _.statement(s"select * from tbl where x = :$param"))
          stmtTest("an insert", _.statement(s"insert into tbl values(:$param)"))
          stmtTest("an update", _.statement(s"update tbl set x = :$param"))
          stmtTest("a returning insert",
            _.statement(s"insert into tbl values(:$param)", StatementOptions.ReturnGenKeys)
          )
          stmtTest("a delete", _.statement(s"delete from tbl where x = :$param"))
        }
      }
    }
  }

  "Streaming arguments should" - {
    "fail with a NoSuchParamException" - {
      val validMap = ListMap(param -> any)
      val invalidMap = ListMap(param -> any, superfluousParam -> any)

      "when stream element contains extra param" - {
        streaming("when first stream element is wrong", invalidMap, validMap)
        streaming("when middle stream element is wrong", validMap, invalidMap, validMap)
        streaming("when last stream element is wrong", validMap, invalidMap)
      }
    }
  }

  private def streaming(desc: String, args: ListMap[String, Any]*): Unit = {
    desc - {
      "when passed by name" in { c =>
        withTable(c, s"$param $arbitraryDataType") { tbl =>
          val stmt = c.statement(s"insert into $tbl values (:$param)")
          val src = Source(args.toVector).runWith(Sink.asPublisher(fanout = false))

          assertNoSuchParamThrown {
            stmt.streamArgs(src).get
          }
        }
      }
    }
  }


  private def stmtTest(stmtType: String, statement: Connection => Statement): Unit = {
    s"of $stmtType" in { c =>
      assertNoSuchParamThrown {
        statement(c).bind(param -> any, superfluousParam -> any)
      }
    }
  }

  def assertNoSuchParamThrown(body: => Unit): Unit = {
    val e = intercept[NoSuchParamException] {
      body
    }
    e.param shouldBe superfluousParam
  }
}
