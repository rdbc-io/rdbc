package io.rdbc.test

import io.rdbc.api.exceptions.TooManyParamsException
import io.rdbc.sapi.{Bindable, Connection}

import scala.concurrent.Future

trait TooManyParamsSpec extends RdbcSpec {

  private val any: Any = 0

  "Bindable should " - {
    "throw a TooManyParamsException" - {
      "when too many params are provided" - {
        "when binding parameters by index" - {
          of("a select", _.select("select * from tbl where x = :xparam"))
          of("an insert", _.insert("insert into tbl values(:xparam)"))
          of("an update", _.insert("update tbl set x = :xparam"))
          of("a returning insert", _.returningInsert("insert into tbl values(:xparam)"))
          of("a delete", _.delete("delete from tbl where x = :xparam"))
        }
      }
    }
  }

  private def of(stmtType: String, bindable: Connection => Future[Bindable[_]]): Unit = {
    s"of $stmtType" - {
      "synchronously" in { c =>
        assertTooManyParamsThrown(c, _.bindByIdx(any, any))
      }

      "asynchronously" in { c =>
        assertTooManyParamsThrown(c, _.bindByIdxF(any, any).get)
      }
    }

    def assertTooManyParamsThrown(c: Connection, binder: Bindable[_] => Any): Unit = {
      val e = intercept[TooManyParamsException] {
        binder.apply(bindable(c).get)
      }
      e.provided shouldBe 2
      e.expected shouldBe 1
    }
  }
}
