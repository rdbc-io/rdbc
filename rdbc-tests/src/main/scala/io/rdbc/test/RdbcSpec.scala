package io.rdbc.test

import io.rdbc.sapi.{Connection, Timeout}
import org.scalatest.{Matchers, Outcome, fixture}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait RdbcSpec extends fixture.FreeSpec with Matchers {
  type FixtureParam = Connection

  protected implicit def ec: ExecutionContext = ExecutionContext.global

  protected implicit def waitAtMost: Duration = 10.seconds
  protected implicit def timeout = Timeout(waitAtMost)
  protected def connection(): Connection

  protected def withFixture(test: OneArgTest): Outcome = {
    val conn = connection()
    try {
      withFixture(test.toNoArgTest(conn))
    } finally {
      conn.forceRelease().get
    }
  }
}
