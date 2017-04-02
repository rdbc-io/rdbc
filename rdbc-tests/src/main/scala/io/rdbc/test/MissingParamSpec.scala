package io.rdbc.test

import io.rdbc.api.exceptions.MissingParamValException
import io.rdbc.sapi.{Bindable, Connection}

import scala.concurrent.Future

trait MissingParamSpec extends RdbcSpec {

  private val missingParam = "yparam"
  private val otherParam = "xparam"
  private val any: Any = 0

  "Bindable should " - {
    "return a MissingParamValException" - {
      "when not all params are provided" - {
        "when binding parameters" - {
          of("a select", _.select(s"select * from tbl where x = :$otherParam, y = :$missingParam"))
          of("an insert", _.insert(s"insert into tbl values(:$otherParam, :$missingParam)"))
          of("an update", _.insert(s"update tbl set x = :$otherParam, y = :$missingParam"))
          of("a returning insert", _.returningInsert(s"insert into tbl values(:$otherParam, :$missingParam)"))
          of("a delete", _.delete(s"delete from tbl where x = :$otherParam and y = :$missingParam)"))
        }
      }
    }
  }

  private def of(stmtType: String, bindable: Connection => Future[Bindable[_]]): Unit = {
    s"of $stmtType" - {
      "synchronously" - {
        "by name" in { c =>
          assertMissingParamThrown(c, _.bind(otherParam -> any))
        }

        "by index" in { c =>
          assertMissingParamThrown(c, _.bindByIdx(any))
        }

        "providing no params" in { c =>
          assertAnyMissingParamThrown(c, _.noParams)
        }
      }

      "asynchronously" - {
        "by name" in { c =>
          assertMissingParamThrown(c, _.bindF(otherParam -> any).get)
        }

        "by index" in { c =>
          assertMissingParamThrown(c, _.bindByIdxF(any).get)
        }

        "providing no params" in { c =>
          assertAnyMissingParamThrown(c, _.noParamsF.get)
        }
      }
    }

    def assertMissingParamThrown(c: Connection, binder: Bindable[_] => Any): Unit = {
      val e = intercept[MissingParamValException] {
        binder.apply(bindable(c).get)
      }
      e.missingParam shouldBe missingParam
    }

    def assertAnyMissingParamThrown(c: Connection, binder: Bindable[_] => Any): Unit = {
      val e = intercept[MissingParamValException] {
        binder.apply(bindable(c).get)
      }
      e.missingParam should (equal(missingParam) or equal(otherParam))
    }
  }
}
