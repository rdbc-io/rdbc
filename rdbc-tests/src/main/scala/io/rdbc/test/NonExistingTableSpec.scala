package io.rdbc.test

import io.rdbc.api.exceptions.InvalidQueryException
import io.rdbc.sapi._
import io.rdbc.test.util.Subscribers

import scala.concurrent.Future

trait NonExistingTableSpec extends RdbcSpec {

  "Error should be returned when referencing a non-existent table when" - {
    stmtTest("Select", _.statement(sql"select * from nonexistent"), errPos = 15)
    stmtTest("Insert", _.statement(sql"insert into nonexistent values (1)"), errPos = 13)
    stmtTest("Returning insert", _.statement(sql"insert into nonexistent values (1)", StatementOptions.ReturnGenKeys), errPos = 13)
    stmtTest("Delete", _.statement(sql"delete from nonexistent"), errPos = 13)
    stmtTest("Update", _.statement(sql"update nonexistent set x = 1"), errPos = 8)
    stmtTest("DDL", _.statement(sql"drop table nonexistent"), errPos = 12)
  }

  private def stmtTest(stmtType: String, stmt: Connection => Future[ParametrizedStatement], errPos: Int): Unit = {
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

      def executedFor[A](executorName: String, executor: ParametrizedStatement => Future[A]): Unit = {
        s"executed for $executorName" in { c =>
          assertInvalidQueryThrown(errPos) {
            stmt(c).flatMap(executor)
          }
        }
      }
    }
  }


  private def assertInvalidQueryThrown(errPos: Int)(body: => Future[Any]): Unit = {
    val e = intercept[InvalidQueryException] {
      body.get
    }
    e.errorPosition.fold(alert("non-fatal: no error position reported")) {
      pos =>
        pos shouldBe errPos
    }
  }
}
