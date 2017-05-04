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

import io.rdbc.api.exceptions.NoKeysReturnedException
import io.rdbc.sapi._

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

trait ParametrizedStatementPartialImpl extends ParametrizedStatement {
  implicit protected def ec: ExecutionContext

  override def executeForSet()(implicit timeout: Timeout): Future[ResultSet] = {
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

  override def execute()(implicit timeout: Timeout): Future[Unit] = {
    executeForRowsAffected().map(_ => ())
  }

  override def executeForRowsAffected()(implicit timeout: Timeout): Future[Long] = {
    executeForStream().flatMap { source =>
      source.rows.subscribe(new IgnoringSubscriber)
      source.rowsAffected
    }
  }

  override def executeForFirstRow()(implicit timeout: Timeout): Future[Option[Row]] = {
    /* The method is optimized for result sets that are small.
       It's faster to fetch all rows instead of fetching
       just one and cancelling the subscription. */
    executeForSet().map(_.rows.headOption)
  }

  override def executeForValue[A](valExtractor: Row => A)
                                 (implicit timeout: Timeout): Future[Option[A]] = {
    executeForFirstRow().map(maybeRow => maybeRow.map(valExtractor))
  }

  override def executeForValueOpt[A](valExtractor: Row => Option[A])
                                    (implicit timeout: Timeout): Future[Option[Option[A]]] = {
    executeForFirstRow().map(maybeRow => maybeRow.map(valExtractor))
  }

  override def executeForKey[K: ClassTag]()(implicit timeout: Timeout): Future[K] = {
    executeForValue[K](_.col(0)).flatMap {
      case Some(key) => Future.successful(key)
      case None => Future.failed(new NoKeysReturnedException)
    }
  }
}
