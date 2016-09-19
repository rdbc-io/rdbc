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

  implicit def ec: ExecutionContext

  def delete(sql: String): Future[Delete] = statement(sql).map(new DeleteImpl(_))

  def insert(sql: String): Future[Insert] = statement(sql).map(new InsertImpl(_))

  def select(sql: String): Future[Select] = statement(sql).map(new SelectImpl(_))

  def update(sql: String): Future[Update] = statement(sql).map(new UpdateImpl(_))

}
