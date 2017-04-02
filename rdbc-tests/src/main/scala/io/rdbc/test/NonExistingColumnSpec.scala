package io.rdbc.test

import io.rdbc.api.exceptions.InvalidQueryException
import io.rdbc.sapi.{Connection, ParametrizedSelect, _}

import scala.concurrent.Future

trait NonExistingColumnSpec extends RdbcSpec {

  "Select should" - {
    "return an error when referencing a non-existent column when" - {
      executedFor("stream", _.executeForStream())
      executedFor("set", _.executeForSet())
      executedFor("value", _.executeForValue(_.int(1)))
      executedFor("first row", _.executeForFirstRow())
      executedFor("optional value", _.executeForValueOpt(_.intOpt(1)))

      def executedFor[A](executorName: String, executor: ParametrizedSelect => Future[A]): Unit = {
        executedForTempl(
          executorName,
          errPos = 8,
          _.select(sql"select nonexistent from tbl"), //TODO string interpolation as in slick #$table
          executor
        )
      }
    }
  }

  "Insert should" - {
    "return an error when referencing a non-existent column when" - {
      executedFor("nothing", _.execute())
      executedFor("rows affected", _.executeForRowsAffected())

      def executedFor[A](executorName: String, executor: ParametrizedInsert => Future[A]): Unit = {
        executedForTempl(
          executorName,
          errPos = 17,
          _.insert(sql"insert into tbl(nonexistent) values (1)"),
          executor
        )
      }
    }
  }

  "Returning insert should" - {
    "return an error when referencing a non-existent column when" - {
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
          errPos = 17,
          _.returningInsert(sql"insert into tbl(nonexistent) values (1)"),
          executor
        )
      }
    }
  }

  "Delete should" - {
    "return an error when referencing a non-existent column when" - {
      executedFor("nothing", _.execute())
      executedFor("rows affected", _.executeForRowsAffected())

      def executedFor[A](executorName: String, executor: ParametrizedDelete => Future[A]): Unit = {
        executedForTempl(
          executorName,
          errPos = 23,
          _.delete(sql"delete from tbl where nonexistent = 1"),
          executor
        )
      }
    }
  }

  "Update should" - {
    "return an error when referencing a non-existent column when" - {
      executedFor("nothing", _.execute())
      executedFor("rows affected", _.executeForRowsAffected())

      def executedFor[A](executorName: String, executor: ParametrizedUpdate => Future[A]): Unit = {
        executedForTempl(
          executorName,
          errPos = 16,
          _.update(sql"update tbl set nonexistent = 1"),
          executor
        )
      }
    }
  }

  "Any statement should" - {
    "return an error when referencing a non-existent column when" - {
      executedFor("nothing", _.executeIgnoringResult())
      executedFor("stream", _.executeForStream())
      executedFor("set", _.executeForSet())
      executedFor("value", _.executeForValue(_.int(1)))
      executedFor("first row", _.executeForFirstRow())
      executedFor("optional value", _.executeForValueOpt(_.intOpt(1)))

      def executedFor[A](executorName: String, executor: AnyParametrizedStatement => Future[A]): Unit = {
        executedForTempl(
          executorName,
          errPos = 8,
          _.statement(sql"select nonexistent from tbl"),
          executor
        )
      }
    }
  }

  "DDL statement should" - {
    "return an error when" - {
      "executed referencing a non-existent column" in { c =>
        assertInvalidQueryThrown(errPos = 29) {
          c.ddl("alter table tbl drop column nonexistent").flatMap(_.execute())
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
      withTable(c) {
        assertInvalidQueryThrown(errPos) {
          creator(c).flatMap(executor)
        }
      }
    }
  }

  private def withTable[A](c: Connection)(body: => A): A = {
    try {
      c.ddl(s"create table tbl (col $arbitraryDataType)")
        .flatMap(_.execute()).get
      body
    } finally {
      c.ddl("drop table tbl").flatMap(_.execute()).get
    }
  }

  protected def arbitraryDataType: String
}
