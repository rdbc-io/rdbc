package io.rdbc.test

import io.rdbc.api.exceptions.TimeoutException
import io.rdbc.sapi._
import io.rdbc.test.util.Subscribers

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.reflect.ClassTag

trait TimeoutSpec extends RdbcSpec {

  private val testTimeout = 1.second.timeout

  "Any statement should" - {
    "return an error when max time is exceeded" - {
      executedFor("nothing", _.execute()(testTimeout))
      executedFor("set", _.executeForSet()(testTimeout))
      executedFor("value", _.executeForValue(_.int(1))(testTimeout))
      executedFor("first row", _.executeForFirstRow()(testTimeout))
      executedFor("optional value", _.executeForValueOpt(_.intOpt(1))(testTimeout))
      executedFor("generated key", _.executeForKey[String]()(ClassTag(classOf[String]), testTimeout))


      executedFor("stream", _.executeForStream()(testTimeout).flatMap { rs =>
        val subscriber = Subscribers.eager()
        rs.rows.subscribe(subscriber)
        subscriber.rows
      })

      def executedFor[A](executorName: String, executor: ParametrizedStatement => Future[A]): Unit = {
        executedForTempl(
          executorName,
          _.statement(slowStatement).map(_.noParams),
          executor
        )
      }
    }
  }

  private def assertTimeoutThrown(body: => Future[Any]): Unit = {
    assertThrows[TimeoutException] {
      body.get
    }
  }

  private def executedForTempl[S, R](name: String,
                                     creator: Connection => Future[S],
                                     executor: S => Future[R]): Unit = {
    s"executed for $name" in { c =>
      assertTimeoutThrown {
        creator(c).flatMap(executor)
      }
    }
  }

  private val slowStatement: String = slowStatement(5.seconds) //TODO hardcoded
  protected def slowStatement(time: FiniteDuration): String
}
