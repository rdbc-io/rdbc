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

import io.rdbc.sapi.{ParametrizedStatement, ResultSet, ResultStream, Row}

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

trait ParametrizedStatementPartialImpl extends ParametrizedStatement {
  implicit def ec: ExecutionContext

  def executeForStream()(implicit timeout: FiniteDuration): Future[ResultStream]

  def executeForSet()(implicit timeout: FiniteDuration): Future[ResultSet] = {
    executeForStream().flatMap { resultStream =>
      val subscriber = new HeadSubscriber(None)
      resultStream.rows.subscribe(subscriber)
      for {
        rowsAffected <- resultStream.rowsAffected
        warnings <- resultStream.warnings
        rows <- subscriber.rows
      } yield {
        new ResultSet(
          rowsAffected = rowsAffected,
          warnings = warnings,
          metadata = resultStream.metadata,
          rows = rows
        )
      }
    }
  }

  def executeIgnoringResult()(implicit timeout: FiniteDuration): Future[Unit] = {
    executeForRowsAffected().map(_ => ())
  }

  def executeForRowsAffected()(implicit timeout: FiniteDuration): Future[Long] = {
    executeForStream().flatMap { source =>
      source.rows.subscribe(new CancellingSubscriber[Row])
      source.rowsAffected
    }
  }

  def executeForFirstRow()(implicit timeout: FiniteDuration): Future[Option[Row]] = {
    executeForStream().flatMap { resultStream =>
      val subscriber = new HeadSubscriber(Some(1L))
      resultStream.rows.subscribe(subscriber)
      subscriber.rows.map(_.headOption)
    }
  }

  def executeForValue[A](valExtractor: Row => A)(implicit timeout: FiniteDuration): Future[Option[A]] = {
    executeForValueOpt(row => Some(valExtractor(row)))
  }

  def executeForValueOpt[A](valExtractor: Row => Option[A])(implicit timeout: FiniteDuration): Future[Option[A]] = {
    executeForFirstRow().map(maybeRow => maybeRow.flatMap(valExtractor))
  }
}
