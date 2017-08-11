/*
 * Copyright 2016 rdbc contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rdbc.sapi

trait SqlInterpolatorTrait {
  implicit class Sql(val sc: StringContext) {

    /** A "sql" string interpolator that provides functionality to
      * create [[SqlWithParams]] instances like:
      *
      * {{{
      *  val id = 10
      *  val stmt = conn.statement(sql"select * from test where id = \$id")
      * }}}
      *
      * The code above produces `select * from test where id = ?` statement
      * with the sole parameter bound to value `10`.
      *
      * The interpolator supports also literal values that allow to dynamically
      * construct statements. To use this feature, instead of \$, use #\$,
      * for example:
      *
      * {{{
      *  val id = 10
      *  val table = "test"
      *  val stmt = conn.statement(sql"select * from #\$table where id = \$id")
      * }}}
      *
      * The code above produces `select * from test where id = ?` statement
      * with the sole parameter bound to value `10`.
      */
    def sql(args: Any*): SqlWithParams = {
      sc.checkLengths(args)

      var sqlArgs = Vector.empty[Any]
      val argsIter = args.iterator
      val sql = new StringBuilder

      sc.parts.foreach { part =>
        if (part.endsWith("#") && argsIter.hasNext) {
          sql.append(part.take(part.length - 1))
          sql.append(argsIter.next)
        } else if (argsIter.hasNext) {
          sql.append(part).append("?")
          sqlArgs = sqlArgs :+ argsIter.next
        } else {
          sql.append(part)
        }
      }
      SqlWithParams(sql.toString, sqlArgs)
    }
  }
}

object SqlInterpolator extends SqlInterpolatorTrait
