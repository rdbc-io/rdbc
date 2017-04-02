package io.rdbc.test

import io.rdbc.api.exceptions.NoSuchParamException
import io.rdbc.sapi.{Bindable, Connection}

import scala.concurrent.Future

trait NoSuchParamSpec extends RdbcSpec {

  private val superfluousParam = "yparam"
  private val param = "xparam"
  private val any: Any = 0

  "Bindable should " - {
    "return a NoSuchParamException" - {
      "when too many params are provided" - {
        "when binding parameters by name" - {
          of("a select", _.select(s"select * from tbl where x = :$param"))
          of("an insert", _.insert(s"insert into tbl values(:$param)"))
          of("an update", _.insert(s"update tbl set x = :$param"))
          of("a returning insert", _.returningInsert(s"insert into tbl values(:$param)"))
          of("a delete", _.delete(s"delete from tbl where x = :$param"))
        }
      }
    }
  }

  private def of(stmtType: String, bindable: Connection => Future[Bindable[_]]): Unit = {
    s"of $stmtType" - {
      "synchronously" in { c =>
        assertNoSuchParamThrown(c, _.bind(param -> any, superfluousParam -> any))
      }

      "asynchronously" in { c =>
        assertNoSuchParamThrown(c, _.bindF(param -> any, superfluousParam -> any).get)
      }
    }

    def assertNoSuchParamThrown(c: Connection, binder: Bindable[_] => Any): Unit = {
      val e = intercept[NoSuchParamException] {
        binder.apply(bindable(c).get)
      }
      e.param shouldBe superfluousParam
    }
  }
}
