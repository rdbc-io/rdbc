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

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class ParametrizedSelectImpl(stmt: AnyParametrizedStatement) extends ParametrizedSelect {

  def executeForStream()(implicit timeout: Timeout): Future[ResultStream] = stmt.executeForStream()

  def executeForSet()(implicit timeout: Timeout): Future[ResultSet] = stmt.executeForSet()

  def executeForFirstRow()(implicit timeout: Timeout): Future[Option[Row]] = stmt.executeForFirstRow()

  def executeForValue[A](f: Row => A)(implicit timeout: Timeout): Future[Option[A]] = stmt.executeForValue[A](f)

  def executeForValueOpt[A](f: Row => Option[A])(implicit timeout: Timeout): Future[Option[Option[A]]] = stmt.executeForValueOpt[A](f)
}
