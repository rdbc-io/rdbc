/*
 * Copyright 2016-2017 Krzysztof Pado
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

package io.rdbc.implbase

import io.rdbc.sapi._

import scala.concurrent.{ExecutionContext, Future}

trait ConnectionPartialImpl extends Connection {

  implicit protected def ec: ExecutionContext

  override def statement(sql: String): Future[Statement] = {
    statement(sql, StatementOptions.Default)
  }

  override def statement(sqlWithParams: SqlWithParams): Future[ParametrizedStatement] = {
    statement(sqlWithParams, StatementOptions.Default)
  }

  override def statement(
                          sqlWithParams: SqlWithParams,
                          statementOptions: StatementOptions
                        ): Future[ParametrizedStatement] = {
    statement(sqlWithParams.sql).map(_.bindByIdx(sqlWithParams.params: _*))
  }
}
