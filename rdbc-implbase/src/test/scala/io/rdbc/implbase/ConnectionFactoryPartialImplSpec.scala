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

package io.rdbc.implbase

import io.rdbc.sapi.{Connection, Timeout}
import org.scalamock.scalatest.MockFactory
import io.rdbc.implbase.Compat._

import scala.concurrent.{ExecutionContext, Future}

class ConnectionFactoryPartialImplSpec
  extends RdbcImplbaseSpec
    with MockFactory {

  implicit private val timeout = Timeout.Inf

  "ConnectionFactoryPartialImpl" when {

    "excuting a code block in a context of connection" should {
      "open new connection and release it" when {
        "code block fails" in {
          val fact = new TestConnFact
          val bodyMock = mockFunction[Connection, Future[Unit]]("body")
          val failure = new RuntimeException

          val connectionMock = mock[Connection]

          inSequence {
            fact.connectionMock.expects(timeout).once().returning(Future.successful(connectionMock))
            bodyMock.expects(connectionMock).once().returning(Future.failed(failure))
            (connectionMock.release _).expects().once().returning(Future.unit)
          }

          val res = fact.withConnection { conn =>
            bodyMock(conn)
          }

          the[RuntimeException] thrownBy res.get shouldBe theSameInstanceAs(failure)
        }

        "code block succeeds" in {
          val fact = new TestConnFact
          val bodyMock = mockFunction[Connection, Future[AnyRef]]("body")
          val bodyRes = new AnyRef

          val connectionMock = mock[Connection]

          inSequence {
            fact.connectionMock.expects(timeout).once().returning(Future.successful(connectionMock))
            bodyMock.expects(connectionMock).once().returning(Future.successful(bodyRes))
            (connectionMock.release _).expects().once().returning(Future.unit)
          }

          val res = fact.withConnection { conn =>
            bodyMock(conn)
          }

          res.get shouldBe theSameInstanceAs(bodyRes)
        }

        "ignore connection release failure" in {
          val fact = new TestConnFact
          val bodyMock = mockFunction[Connection, Future[AnyRef]]("body")
          val bodyRes = new AnyRef

          val connectionMock = mock[Connection]

          inSequence {
            fact.connectionMock.expects(timeout).once().returning(Future.successful(connectionMock))
            bodyMock.expects(connectionMock).once().returning(Future.successful(bodyRes))
            (connectionMock.release _).expects().once().returning(Future.failed(new RuntimeException))
          }

          val res = fact.withConnection { conn =>
            bodyMock(conn)
          }

          res.get shouldBe theSameInstanceAs(bodyRes)
        }
      }

      class TestConnFact extends ConnectionFactoryPartialImpl {

        val connectionMock = mockFunction[Timeout, Future[Connection]]("connection")

        implicit protected val ec: ExecutionContext = ExecutionContext.global

        def connection()(implicit timeout: Timeout): Future[Connection] = {
          connectionMock(timeout)
        }

        def shutdown(): Future[Unit] = ???
      }
    }
  }
}
