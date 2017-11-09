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

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import io.rdbc.sapi.exceptions.MissingParamValException
import io.rdbc.sapi.{Connection, Statement, StatementOptions}

import scala.collection.immutable.ListMap

trait MissingParamSpec
  extends RdbcSpec
    with TableSpec {

  private val missingParam = "yparam"
  private val otherParam = "xparam"
  private val any: Any = 0

  protected implicit def system: ActorSystem
  protected implicit def materializer: Materializer

  protected def arbitraryDataType: String

  "Statement should " - {
    "return a MissingParamValException" - {
      "when not all args are provided" - {
        "when binding arguments" - {
          stmtType("of a select", _.statement(s"select * from tbl where x = :$otherParam, y = :$missingParam"))
          stmtType("of an insert", _.statement(s"insert into tbl values(:$otherParam, :$missingParam)"))
          stmtType("of an update", _.statement(s"update tbl set x = :$otherParam, y = :$missingParam"))
          stmtType("of a returning insert",
            _.statement(s"insert into tbl values(:$otherParam, :$missingParam)",
              StatementOptions.ReturnGenKeys)
          )
          stmtType("of a delete", _.statement(s"delete from tbl where x = :$otherParam and y = :$missingParam)"))
        }
      }
    }
  }

  "Streaming arguments should" - {
    "fail with a MissingParamValException" - {
      val validMap = ListMap(otherParam -> any, missingParam -> any)
      val missingMap = ListMap(otherParam -> any)

      streaming("when first stream element is wrong", missingMap, validMap)
      streaming("when middle stream element is wrong", validMap, missingMap, validMap)
      streaming("when last stream element is wrong", validMap, missingMap)
    }
  }

  private def stmtType(stmtType: String, statement: Connection => Statement): Unit = {
    s"of $stmtType" - {
      "by name" in { c =>
        assertMissingParamThrown {
          statement(c).bind(otherParam -> any)
        }
      }

      "by index" in { c =>
        assertMissingParamThrown {
          statement(c).bindByIdx(any)
        }
      }

      "providing no params" in { c =>
        val e = intercept[MissingParamValException] {
          statement(c).noArgs
        }
        e.missingParam should (equal(missingParam) or equal(otherParam))
      }
    }
  }

  private def streaming(desc: String, args: ListMap[String, Any]*): Unit = {
    desc - {
      "when passed by name" in { c =>
        withTable(c, s"x $arbitraryDataType, y $arbitraryDataType") { tbl =>
          val stmt = c.statement(s"insert into $tbl values (:$otherParam, :$missingParam)")
          val src = Source(args.toVector).runWith(Sink.asPublisher(fanout = false))

          assertMissingParamThrown {
            stmt.streamArgs(src).get
          }
        }
      }

      "when passed by index" in { c =>
        val argsVectors = args.map { argMap =>
          argMap.values.toVector
        }.toVector

        withTable(c, s"x $arbitraryDataType, y $arbitraryDataType") { tbl =>
          val stmt = c.statement(s"insert into $tbl values (:$otherParam, :$missingParam)")
          val src = Source(argsVectors).runWith(Sink.asPublisher(fanout = false))

          assertMissingParamThrown {
            stmt.streamArgsByIdx(src).get
          }
        }
      }
    }
  }

  def assertMissingParamThrown(body: => Unit): Unit = {
    val e = intercept[MissingParamValException] {
      body
    }
    e.missingParam shouldBe missingParam
  }
}
