package io.rdbc.test

import io.rdbc.api.exceptions.InvalidQueryException
import io.rdbc.sapi._

import scala.concurrent.Future

trait SyntaxErrorSpec extends RdbcSpec {

  "Select should" - {
    "return an error when sql is syntactically incorrect when" - {
      executedFor("stream", _.executeForStream())
      executedFor("set", _.executeForSet())
      executedFor("value", _.executeForValue(_.int(1)))
      executedFor("first row", _.executeForFirstRow())
      executedFor("optional value", _.executeForValueOpt(_.intOpt(1)))

      def executedFor[A](executorName: String, executor: ParametrizedSelect => Future[A]): Unit = {
        executedForTempl(
          executorName,
          _.select(sql"select * should_be_from tbl"),
          executor
        )
      }
    }
  }

  "Insert should" - {
    "return an error when sql is syntactically incorrect when" - {
      executedFor("nothing", _.execute())
      executedFor("rows affected", _.executeForRowsAffected())

      def executedFor[A](executorName: String, executor: ParametrizedInsert => Future[A]): Unit = {
        executedForTempl(
          executorName,
          _.insert(sql"insert should_be_into tbl values (1)"),
          executor
        )
      }
    }
  }

  "Returning insert should" - {
    "return an error when sql is syntactically incorrect when" - {
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
          _.returningInsert(sql"insert should_be_into tbl values (1)"),
          executor
        )
      }
    }
  }

  "Delete should" - {
    "return an error when sql is syntactically incorrect when" - {
      executedFor("nothing", _.execute())
      executedFor("rows affected", _.executeForRowsAffected())

      def executedFor[A](executorName: String, executor: ParametrizedDelete => Future[A]): Unit = {
        executedForTempl(
          executorName,
          _.delete(sql"delete should_be_from tbl"),
          executor
        )
      }
    }
  }

  "Update should" - {
    "return an error when sql is syntactically incorrect when" - {
      executedFor("nothing", _.execute())
      executedFor("rows affected", _.executeForRowsAffected())

      def executedFor[A](executorName: String, executor: ParametrizedUpdate => Future[A]): Unit = {
        executedForTempl(
          executorName,
          _.update(sql"update tbl should_be_set col = null"),
          executor
        )
      }
    }
  }

  "Any statement should" - {
    "return an error when sql is syntactically incorrect when" - {
      executedFor("nothing", _.executeIgnoringResult())
      executedFor("stream", _.executeForStream())
      executedFor("set", _.executeForSet())
      executedFor("value", _.executeForValue(_.int(1)))
      executedFor("first row", _.executeForFirstRow())
      executedFor("optional value", _.executeForValueOpt(_.intOpt(1)))

      def executedFor[A](executorName: String, executor: AnyParametrizedStatement => Future[A]): Unit = {
        executedForTempl(
          executorName,
          _.statement(sql"select * should_be_from tbl"),
          executor
        )
      }
    }
  }

  "DDL statement should" - {
    "return an error when" - {
      "executed sql is syntactically incorrect" in { c =>
        assertInvalidQueryThrown {
          c.ddl("alter should_be_table tbl drop column col").flatMap(_.execute())
        }
      }
    }
  }

  private def assertInvalidQueryThrown(body: => Future[Any]): Unit = {
    assertThrows[InvalidQueryException](body.get)
  }

  private def executedForTempl[S, R](name: String,
                                     creator: Connection => Future[S],
                                     executor: S => Future[R]): Unit = {
    s"executed for $name" in { c =>
      withTable(c) {
        assertInvalidQueryThrown {
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
