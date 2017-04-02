package io.rdbc.test

import io.rdbc.api.exceptions.InvalidQueryException
import io.rdbc.sapi._

import scala.concurrent.Future

trait NonExistingTableSpec extends RdbcSpec {

  "Select should" - {
    "return an error when executed on a non-existent table when" - {
      executedFor("stream", _.executeForStream())
      executedFor("set", _.executeForSet())
      executedFor("value", _.executeForValue(_.int(1)))
      executedFor("first row", _.executeForFirstRow())
      executedFor("optional value", _.executeForValueOpt(_.intOpt(1)))

      def executedFor[A](executorName: String, executor: ParametrizedSelect => Future[A]): Unit = {
        executedForTempl(
          executorName,
          errPos = 15,
          _.select(sql"select * from nonexistent"),
          executor
        )
      }
    }
  }

  "Insert should" - {
    "return an error when executed on a non-existent table when" - {
      executedFor("nothing", _.execute())
      executedFor("rows affected", _.executeForRowsAffected())

      def executedFor[A](executorName: String, executor: ParametrizedInsert => Future[A]): Unit = {
        executedForTempl(
          executorName,
          errPos = 13,
          _.insert(sql"insert into nonexistent values (1)"),
          executor
        )
      }
    }
  }

  "Returning insert should" - {
    "return an error when executed on a non-existent table when" - {
      executedFor("nothing", _.execute())
      executedFor("rows affected", _.executeForRowsAffected())
      executedFor("int key", _.executeForIntKey())
      executedFor("some key", _.executeForKey[String])
      executedFor("keys set", _.executeForKeysSet())
      executedFor("keys stream", _.executeForKeysStream())
      executedFor("long key", _.executeForLongKey())
      executedFor("UUID key", _.executeForUUIDKey())

      def executedFor[A](executorName: String, executor: ParametrizedReturningInsert => Future[A]): Unit = {
        executedForTempl(
          executorName,
          errPos = 13,
          _.returningInsert(sql"insert into nonexistent values (1)"),
          executor
        )
      }
    }
  }

  "Delete should" - {
    "return an error when executed on a non-existent table when" - {
      executedFor("nothing", _.execute())
      executedFor("rows affected", _.executeForRowsAffected())

      def executedFor[A](executorName: String, executor: ParametrizedDelete => Future[A]): Unit = {
        executedForTempl(
          executorName,
          errPos = 13,
          _.delete(sql"delete from nonexistent"),
          executor
        )
      }
    }
  }

  "Update should" - {
    "return an error when executed on a non-existent table when" - {
      executedFor("nothing", _.execute())
      executedFor("rows affected", _.executeForRowsAffected())

      def executedFor[A](executorName: String, executor: ParametrizedUpdate => Future[A]): Unit = {
        executedForTempl(
          executorName,
          errPos = 8,
          _.update(sql"update nonexistent set x = 1"),
          executor
        )
      }
    }
  }

  "Any statement should" - {
    "return an error when executed on a non-existent table when" - {
      executedFor("nothing", _.executeIgnoringResult())
      executedFor("stream", _.executeForStream())
      executedFor("set", _.executeForSet())
      executedFor("value", _.executeForValue(_.int(1)))
      executedFor("first row", _.executeForFirstRow())
      executedFor("optional value", _.executeForValueOpt(_.intOpt(1)))

      def executedFor[A](executorName: String, executor: AnyParametrizedStatement => Future[A]): Unit = {
        executedForTempl(
          executorName,
          errPos = 15,
          _.statement(sql"select * from nonexistent"),
          executor
        )
      }
    }
  }

  "DDL statement should" - {
    "return an error when" - {
      "executed referencing a non-existent table" in { c =>
        assertInvalidQueryThrown(errPos = 12) {
          c.ddl("drop table nonexistent").flatMap(_.execute())
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

  private def executedForTempl[S, R](name: String,
                                     errPos: Int,
                                     creator: Connection => Future[S],
                                     executor: S => Future[R]): Unit = {
    s"executed for $name" in { c =>
      assertInvalidQueryThrown(errPos) {
        creator(c).flatMap(executor)
      }
    }
  }
}
