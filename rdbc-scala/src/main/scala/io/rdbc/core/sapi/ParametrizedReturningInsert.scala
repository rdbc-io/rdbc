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

package io.rdbc.core.sapi

import java.util.UUID

import io.rdbc.core.api.exceptions.NoKeysReturnedException

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class ParametrizedReturningInsert(stmt: ParametrizedStatement) {

  def execute()(implicit timeout: FiniteDuration): Future[Unit] = stmt.executeIgnoringResult()

  def executeForRowsAffected()(implicit timeout: FiniteDuration): Future[Long] = stmt.executeForRowsAffected()

  def executeForKeysStream()(implicit timeout: FiniteDuration): Future[ResultStream] = stmt.executeForStream()

  def executeForKeysSet()(implicit timeout: FiniteDuration): Future[ResultSet] = stmt.executeForSet()

  def executeForKey[K](cls: Class[K])(implicit timeout: FiniteDuration): Future[K] = {
    stmt.executeForValue[K](_.obj(0, cls)).flatMap {
      case Some(key) => Future.successful(key)
      case None => Future.failed(NoKeysReturnedException)
    }
  }

  def executeForIntKey()(implicit timeout: FiniteDuration): Future[Int] = executeForKey(classOf[Int])

  def executeForLongKey()(implicit timeout: FiniteDuration): Future[Long] = executeForKey(classOf[Long])

  def executeForUUIDKey()(implicit timeout: FiniteDuration): Future[UUID] = executeForKey(classOf[UUID])
}
