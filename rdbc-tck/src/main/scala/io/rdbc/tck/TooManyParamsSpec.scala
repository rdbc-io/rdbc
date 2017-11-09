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
import io.rdbc.sapi.exceptions.TooManyParamsException
import io.rdbc.sapi.{Connection, Statement, StatementOptions}

trait TooManyParamsSpec
  extends RdbcSpec
    with TableSpec {

  private val any: Any = 0

  protected def arbitraryDataType: String

  protected implicit def system: ActorSystem

  protected implicit def materializer: Materializer

  "Statement should " - {
    "throw a TooManyParamsException" - {
      "when too many params are provided" - {
        "when binding parameters by index" - {
          stmtTest("a select", _.statement("select * from tbl where x = :xparam"))
          stmtTest("an insert", _.statement("insert into tbl values(:xparam)"))
          stmtTest("an update", _.statement("update tbl set x = :xparam"))
          stmtTest("a returning insert", _.statement("insert into tbl values(:xparam)", StatementOptions.ReturnGenKeys))
          stmtTest("a delete", _.statement("delete from tbl where x = :xparam"))
        }
      }
    }
  }

  "Streaming arguments should" - {
    "fail with a TooManyParamsException" - {
      val validArgs = Vector(any)
      val invalidArgs = Vector(any, any)

      "when stream element contains extra param" - {
        streaming("when first stream element is wrong", invalidArgs, validArgs)
        streaming("when middle stream element is wrong", validArgs, invalidArgs, validArgs)
        streaming("when last stream element is wrong", validArgs, invalidArgs)
      }
    }
  }

  private def streaming(desc: String, args: Vector[Any]*): Unit = {
    desc - {
      "when passed by index" in { c =>
        withTable(c, s"xparam $arbitraryDataType") { tbl =>
          val stmt = c.statement(s"insert into $tbl values (:xparam)")
          val src = Source(args.toVector).runWith(Sink.asPublisher(fanout = false))

          assertTooManyParamsThrown {
            stmt.streamArgsByIdx(src).get
          }
        }
      }
    }
  }

  private def stmtTest(stmtType: String, statement: Connection => Statement): Unit = {
    s"of $stmtType" in { c =>
      assertTooManyParamsThrown {
        statement(c).bindByIdx(any, any)
      }
    }
  }

  private def assertTooManyParamsThrown(body: => Unit): Unit = {
    val e = intercept[TooManyParamsException] {
      body
    }
    e.provided shouldBe 2
    e.expected shouldBe 1
  }
}
