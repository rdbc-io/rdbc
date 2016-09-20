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

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

trait ParametrizedStatement {
  def executeForStream()(implicit timeout: FiniteDuration): Future[ResultStream]

  def executeForSet()(implicit timeout: FiniteDuration): Future[ResultSet]

  def executeIgnoringResult()(implicit timeout: FiniteDuration): Future[Unit]

  def executeForRowsAffected()(implicit timeout: FiniteDuration): Future[Long]

  def executeForFirstRow()(implicit timeout: FiniteDuration): Future[Option[Row]]

  def executeForValue[A](valExtractor: Row => A)(implicit timeout: FiniteDuration): Future[Option[A]]

  def executeForValueOpt[A](valExtractor: Row => Option[A])(implicit timeout: FiniteDuration): Future[Option[A]]
}
