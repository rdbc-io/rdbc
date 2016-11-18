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

import java.util.UUID

import io.rdbc.api.exceptions.NoKeysReturnedException
import io.rdbc.sapi._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

class ParametrizedReturningInsertImpl(stmt: AnyParametrizedStatement)(implicit ec: ExecutionContext) extends ParametrizedReturningInsert {

  def execute()(implicit timeout: FiniteDuration): Future[Unit] = stmt.executeIgnoringResult()

  def executeForRowsAffected()(implicit timeout: FiniteDuration): Future[Long] = stmt.executeForRowsAffected()

  def executeForKeysStream()(implicit timeout: FiniteDuration): Future[ResultStream] = stmt.executeForStream()

  def executeForKeysSet()(implicit timeout: FiniteDuration): Future[ResultSet] = stmt.executeForSet()

  def executeForKey[K: ClassTag](implicit timeout: FiniteDuration): Future[K] = {
    stmt.executeForValue[K](_.col(0)).flatMap {
      case Some(key) => Future.successful(key)
      case None => Future.failed(NoKeysReturnedException)
    }
  }

  def executeForIntKey()(implicit timeout: FiniteDuration): Future[Int] = executeForKey

  def executeForLongKey()(implicit timeout: FiniteDuration): Future[Long] = executeForKey

  def executeForUUIDKey()(implicit timeout: FiniteDuration): Future[UUID] = executeForKey
}
