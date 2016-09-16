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

package io.rdbc.core.api

import org.reactivestreams.Publisher

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

/**
  * Represents a database connection (session).
  *
  */
trait Connection {

  implicit def ec: ExecutionContext

  /**
    * Starts a database transaction.
    *
    * @param timeout dupa
    */
  def beginTx()(implicit timeout: FiniteDuration): Future[Unit]

  def commitTx()(implicit timeout: FiniteDuration): Future[Unit]

  def rollbackTx()(implicit timeout: FiniteDuration): Future[Unit]

  def release(): Future[Unit]

  def validate(): Future[Boolean]

  def select(sql: String): Future[Select] = statement(sql).map(new Select(_))

  def update(sql: String): Future[Update] = statement(sql).map(new Update(_))

  def insert(sql: String): Future[Insert] = statement(sql).map(new Insert(_))

  def returningInsert(sql: String): Future[ReturningInsert]

  def returningInsert(sql: String, keyColumns: String*): Future[ReturningInsert]

  def delete(sql: String): Future[Delete] = statement(sql).map(new Delete(_))

  def statement(sql: String): Future[Statement]

  def streamIntoTable(sql: String, paramsPublisher: Publisher[Map[String, Any]]): Future[Unit]
}
