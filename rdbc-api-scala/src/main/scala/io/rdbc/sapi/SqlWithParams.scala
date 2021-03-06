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

import io.rdbc.ImmutIndexedSeq

/** Represent a sql statement along with parameters.
  * Instances of this class are supposed to be constructed using `sql`
  * string interpolator like this:
  * {{{
  *   import io.rdbc.sapi._
  *   val id = 0
  *   val s: SqlWithParams = sql"select * from test where id = \$id"
  * }}}
  *
  * Instances created by the interpolator can be composed:
  * {{{
  *   import io.rdbc.sapi._
  *   val x = 0
  *   val y = 0
  *   val s: SqlWithParams =
  *     sql"select * from test where x = \$x" +
  *     sql"and y = \$y"
  * }}}
  */
case class SqlWithParams(sql: String, params: ImmutIndexedSeq[Any]) {
  def +(other: SqlWithParams): SqlWithParams = {
    copy(
      sql = sql + other.sql,
      params = params.toVector ++ other.params
    )
  }
}
