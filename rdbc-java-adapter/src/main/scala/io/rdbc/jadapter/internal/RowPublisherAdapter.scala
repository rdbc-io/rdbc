/*
 * Copyright 2016 rdbc contributors
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

package io.rdbc.jadapter.internal

import java.util
import java.util.concurrent.CompletionStage

import io.rdbc.jadapter.internal.Conversions._
import io.rdbc.jadapter.internal.ExceptionConverter._
import io.rdbc.{japi, sapi}

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext

private[jadapter] class RowPublisherAdapter(private[jadapter] val underlying: sapi.RowPublisher)
                                           (implicit ec: ExecutionContext)
  extends japi.RowPublisher
    with MappingPublisher[sapi.Row, japi.Row] {

  def getRowsAffected: CompletionStage[java.lang.Long] = convertExceptionsFut {
    underlying.rowsAffected.map(java.lang.Long.valueOf).toJava
  }

  def getWarnings: CompletionStage[_ <: util.List[japi.Warning]] = convertExceptionsFut {
    underlying.warnings.map { warnSeq =>
      warnSeq.map(_.asJava).asJava
    }.toJava
  }

  def getMetadata: CompletionStage[japi.RowMetadata] = convertExceptionsFut {
    underlying.metadata.map(_.asJava).toJava
  }

  private[jadapter] def mapElem(row: sapi.Row): japi.Row = row.asJava

  private[jadapter] def mapError(t: Throwable): Throwable = {
    t match {
      case rdbcEx: sapi.exceptions.RdbcException => convertException(rdbcEx)
      case _ => t
    }
  }

  override def toString: String = underlying.toString

}
