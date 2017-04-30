package io.rdbc.test

import io.rdbc.api.exceptions.TooManyParamsException
import io.rdbc.sapi.{Connection, Statement, StatementOptions}

import scala.concurrent.Future

trait TooManyParamsSpec extends RdbcSpec {

  private val any: Any = 0

  "Bindable should " - {
    "throw a TooManyParamsException" - {
      "when too many params are provided" - {
        "when binding parameters by index" - {
          of("a select", _.statement("select * from tbl where x = :xparam"))
          of("an insert", _.statement("insert into tbl values(:xparam)"))
          of("an update", _.statement("update tbl set x = :xparam"))
          of("a returning insert", _.statement("insert into tbl values(:xparam)", StatementOptions.ReturnGenKeys))
          of("a delete", _.statement("delete from tbl where x = :xparam"))
        }
      }
    }
  }

  private def of(stmtType: String, bindable: Connection => Future[Statement]): Unit = {
    s"of $stmtType" - {
      "synchronously" in { c =>
        assertTooManyParamsThrown(c, _.bindByIdx(any, any))
      }

      "asynchronously" in { c =>
        assertTooManyParamsThrown(c, _.bindByIdxF(any, any).get)
      }
    }

    def assertTooManyParamsThrown(c: Connection, binder: Statement => Any): Unit = {
      val e = intercept[TooManyParamsException] {
        binder.apply(bindable(c).get)
      }
      e.provided shouldBe 2
      e.expected shouldBe 1
    }
  }
}
