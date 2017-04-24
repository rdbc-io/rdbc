/*
 * Copyright 2016 Krzysztof Pado
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

private[sapi] trait InterpolatorsTrait {

  /** A "sql" string interpolator that provides functionality to
    * create [[SqlWithParams]] instances like:
    *
    * {{{
    *  val id = 10
    *  val select = conn.select(sql"select * from test where id = \$id")
    * }}}
    */
  implicit class SqlInterpolator(val sc: StringContext) {
    def sql(args: Any*): SqlWithParams = {
      val sqlWithPlaceholders = sc.parts.zipWithIndex.reduce { (t1, t2) =>
        val (part1, idx) = t1
        val (part2, nextIdx) = t2
        (s"$part1:p$idx$part2", nextIdx)
      }._1

      SqlWithParams(sqlWithPlaceholders, args.toVector)
    }
  }
}

/** String interpolators */
object Interpolators extends InterpolatorsTrait
