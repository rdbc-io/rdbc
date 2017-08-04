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

import io.rdbc.sapi._
import io.rdbc.implbase.Compat._
import org.scalamock.scalatest.MockFactory

import scala.concurrent.{ExecutionContext, Future}

class ConnectionPartialImplSpec
  extends RdbcImplbaseSpec
    with MockFactory {

  private implicit val timeout = Timeout.Inf

  "ConnectionPartialImpl" when {

    "executing a code block in new transaction" should {

      "begin tx and rollback it if the block fails" in {
        val conn = new TestConn
        val failure = new RuntimeException()
        val bodyMock = mockFunction[Future[Unit]]("body")

        inSequence {
          conn.beginMock.expects(timeout).once().returning(Future.unit)
          bodyMock.expects().once().returning(Future.failed(failure))
          conn.rollbackMock.expects(timeout).once().returning(Future.unit)
        }
        conn.commitMock.expects(*).never()

        val res = conn.withTransaction {
          bodyMock()
        }

        the[RuntimeException] thrownBy res.get shouldBe theSameInstanceAs(failure)
      }

      "begin tx, commit it if the block succeeds and return block result" in {
        val conn = new TestConn()
        val blockRes = new AnyRef
        val bodyMock = mockFunction[Future[AnyRef]]("body")

        inSequence {
          conn.beginMock.expects(timeout).once().returning(Future.unit)
          bodyMock.expects().once().returning(Future.successful(blockRes))
          conn.commitMock.expects(timeout).once().returning(Future.unit)
        }
        conn.rollbackMock.expects(*).never()

        val res = conn.withTransaction {
          bodyMock()
        }

        res.get shouldBe theSameInstanceAs(blockRes)
      }

      "neither commit nor rollback if beginning tx fails" in {
        val beginTxFailure = new RuntimeException()
        val conn = new TestConn
        val bodyMock = mockFunction[Future[Unit]]("body")

        conn.beginMock.expects(timeout).once().returning(Future.failed(beginTxFailure))

        conn.commitMock.expects(*).never()
        bodyMock.expects().never()
        conn.rollbackMock.expects(*).never()

        val res = conn.withTransaction {
          bodyMock()
        }

        the[RuntimeException] thrownBy res.get shouldBe theSameInstanceAs(beginTxFailure)
      }

      "ignore rollback errors" in {
        val conn = new TestConn
        val failure = new RuntimeException()
        val bodyMock = mockFunction[Future[Unit]]("body")

        inSequence {
          conn.beginMock.expects(timeout).once().returning(Future.unit)
          bodyMock.expects().once().returning(Future.failed(failure))
          conn.rollbackMock.expects(timeout).once().returning(Future.failed(new RuntimeException))
        }
        conn.commitMock.expects(*).never()

        val res = conn.withTransaction {
          bodyMock()
        }

        the[RuntimeException] thrownBy res.get shouldBe theSameInstanceAs(failure)
      }
    }
  }

  "ConnectionPartialImpl" should {
    "provide default statement options" when {
      "called with plain SQL" in {
        val conn = new TestConn
        val sql = "selecet 1"
        val res = mock[Statement]

        conn.statementMock.expects(sql, StatementOptions.Default).once().returning(res)

        conn.statement(sql) shouldBe theSameInstanceAs(res)
      }

      "called with SQL with params" in {
        val conn = new TestConn
        val param1 = 1
        val param2 = 2
        val sqlWithParams = sql"select $param1, $param2"
        val resStmt = mock[Statement]
        val resExecStmt = mock[ExecutableStatement]

        inSequence {
          conn.statementMock.expects(sqlWithParams.sql, StatementOptions.Default).once().returning(resStmt)
          (resStmt.bindByIdx _).expects(sqlWithParams.params).once().returning(resExecStmt)
        }

        conn.statement(sqlWithParams) shouldBe theSameInstanceAs(resExecStmt)
      }
    }

    "pass params from interpolated SQL as positional params" in {
      val conn = new TestConn
      val param1 = 1
      val param2 = 2
      val sqlWithParams = sql"select $param1, $param2"
      val stmtOptions = StatementOptions(KeyColumns.named("c1, c2"))
      val resStmt = mock[Statement]
      val resExecStmt = mock[ExecutableStatement]

      inSequence {
        conn.statementMock.expects(sqlWithParams.sql, stmtOptions).once().returning(resStmt)
        (resStmt.bindByIdx _).expects(sqlWithParams.params).once().returning(resExecStmt)
      }

      conn.statement(sqlWithParams, stmtOptions) shouldBe theSameInstanceAs(resExecStmt)
    }
  }

  class TestConn
    extends ConnectionPartialImpl {

    val beginMock = mockFunction[Timeout, Future[Unit]]("beginTx")
    val commitMock = mockFunction[Timeout, Future[Unit]]("commitTx")
    val rollbackMock = mockFunction[Timeout, Future[Unit]]("rollbackTx")
    val statementMock = mockFunction[String, StatementOptions, Statement]("statement")

    implicit protected def ec: ExecutionContext = ExecutionContext.global

    def beginTx()(implicit timeout: Timeout): Future[Unit] = {
      beginMock(timeout)
    }

    def commitTx()(implicit timeout: Timeout): Future[Unit] = {
      commitMock(timeout)
    }

    def rollbackTx()(implicit timeout: Timeout): Future[Unit] = {
      rollbackMock(timeout)
    }

    def statement(sql: String, statementOptions: StatementOptions): Statement = {
      statementMock(sql, statementOptions)
    }

    def release(): Future[Unit] = ???
    def forceRelease(): Future[Unit] = ???
    def validate()(implicit timeout: Timeout): Future[Unit] = ???
    def watchForIdle: Future[Unit] = ???
  }

}
