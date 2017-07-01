package io.rdbc.util

class PreconditionsSpec extends RdbcUtilSpec {

  "Preconditions not null checking feature" when {
    "executed for single argument" should {

      def test(arg: String): Unit = {
        Preconditions.notNull(arg)
      }

      "throw NPE for null argument" in {
        assertThrows[NullPointerException](test(null))
      }

      "not throw NPE for not-null argument" in {
        test("notnull")
      }
    }

    "executed for multiple arguments" should {

      def test(arg1: String, arg2: String): Unit = {
        Preconditions.notNull(arg1, arg2)
      }

      "throw NPE for all null arguments" in {
        assertThrows[NullPointerException](test(null, null))
      }

      "throw NPE for first null argument" in {
        assertThrows[NullPointerException](test(null, "notnull"))
      }

      "throw NPE for second null argument" in {
        assertThrows[NullPointerException](test("notnull", null))
      }

      "not throw NPE for not-null arguments" in {
        test("notnull1", "notnull2")
      }
    }
  }
}
