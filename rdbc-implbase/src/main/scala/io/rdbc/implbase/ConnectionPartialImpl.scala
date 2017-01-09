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

package io.rdbc.implbase

import io.rdbc.sapi._

import scala.concurrent.{ExecutionContext, Future}

trait ConnectionPartialImpl extends Connection {

  implicit protected def ec: ExecutionContext

  override def delete(sql: String): Future[Delete] = statement(sql).map(new DeleteImpl(_))

  override def insert(sql: String): Future[Insert] = statement(sql).map(new InsertImpl(_))

  override def select(sql: String): Future[Select] = statement(sql).map(new SelectImpl(_))

  override def update(sql: String): Future[Update] = statement(sql).map(new UpdateImpl(_))

  override def delete(sqlWithParams: SqlWithParams): Future[ParametrizedDelete] = {
    parametrizedStmt(delete)(sqlWithParams)
  }

  override def insert(sqlWithParams: SqlWithParams): Future[ParametrizedInsert] = {
    parametrizedStmt(insert)(sqlWithParams)
  }

  override def select(sqlWithParams: SqlWithParams): Future[ParametrizedSelect] = {
    parametrizedStmt(select)(sqlWithParams)
  }

  override def update(sqlWithParams: SqlWithParams): Future[ParametrizedUpdate] = {
    parametrizedStmt(update)(sqlWithParams)
  }

  override def returningInsert(sqlWithParams: SqlWithParams): Future[ParametrizedReturningInsert] = {
    parametrizedStmt(returningInsert)(sqlWithParams)
  }

  override def returningInsert(sqlWithParams: SqlWithParams,
                      keyColumns: String*): Future[ParametrizedReturningInsert] = {
    parametrizedStmt {
      sql => returningInsert(sql, keyColumns: _*)
    }(sqlWithParams)
  }

  override def statement(sqlWithParams: SqlWithParams): Future[AnyParametrizedStatement] = {
    parametrizedStmt(statement)(sqlWithParams)
  }

  override def ddl(sql: String): Future[DdlStatement] = {
    statement(sql).flatMap(_.noParamsF).map(new DdlImpl(_))
  }

  protected def parametrizedStmt[T](bindable: String => Future[Bindable[T]])
                                   (sqlWithParams: SqlWithParams): Future[T] = {
    bindable(sqlWithParams.sql).map(_.bindByIdx(sqlWithParams.params: _*))
  }
}
