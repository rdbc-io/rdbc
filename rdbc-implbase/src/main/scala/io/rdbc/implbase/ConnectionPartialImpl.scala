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
import io.rdbc.util.Logging
import io.rdbc.implbase.Compat._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait ConnectionPartialImpl
  extends Connection
    with Logging {

  implicit protected def ec: ExecutionContext

  override def withTransaction[A](body: => Future[A])
                                 (implicit timeout: Timeout): Future[A] = {
    beginTx()
      .flatMap(_ => body)
      .transformWith {
        case Success(res) => commitTx().map(_ => res)
        case Failure(ex) =>
          rollbackTx().recover { case rollbackEx =>
            logger.warn(
              "Error occurred when rolling back transaction",
              rollbackEx
            )
          }.flatMap(_ => Future.failed(ex))
      }
  }

  override def statement(sql: String): Statement = {
    statement(sql, StatementOptions.Default)
  }

  override def statement(sqlWithParams: SqlWithParams): ExecutableStatement = {
    statement(sqlWithParams, StatementOptions.Default)
  }

  override def statement(
                          sqlWithParams: SqlWithParams,
                          statementOptions: StatementOptions
                        ): ExecutableStatement = {
    statement(sqlWithParams.sql).bindByIdx(sqlWithParams.params: _*)
  }
}
