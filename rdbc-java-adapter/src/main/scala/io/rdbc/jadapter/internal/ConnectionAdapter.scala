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

import java.time.Duration
import java.util.concurrent.CompletionStage

import io.rdbc.jadapter.internal.Conversions._
import io.rdbc.japi._
import io.rdbc.japi.util.ThrowingSupplier
import io.rdbc.sapi
import io.rdbc.util.Preconditions.checkNotNull

import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext

private[jadapter] class ConnectionAdapter(val underlying: sapi.Connection)
                                         (implicit ec: ExecutionContext,
                                          exConversion: ExceptionConversion)
  extends Connection {

  import exConversion._

  def beginTx(timeout: Duration): CompletionStage[Void] = {
    checkNotNull(timeout)
    convertExceptionsFut {
      underlying.beginTx()(timeout.asScala).map[Void](_ => null).toJava
    }
  }

  def beginTx(): CompletionStage[Void] = {
    beginTx(InfiniteTimeout)
  }

  def commitTx(timeout: Duration): CompletionStage[Void] = {
    checkNotNull(timeout)
    convertExceptionsFut {
      underlying.commitTx()(timeout.asScala).map[Void](_ => null).toJava
    }
  }

  def commitTx(): CompletionStage[Void] = {
    commitTx(InfiniteTimeout)
  }

  def rollbackTx(timeout: Duration): CompletionStage[Void] = {
    checkNotNull(timeout)
    convertExceptionsFut {
      underlying.rollbackTx()(timeout.asScala).map[Void](_ => null).toJava
    }
  }

  def rollbackTx(): CompletionStage[Void] = {
    rollbackTx(InfiniteTimeout)
  }

  def withTransaction[T](body: ThrowingSupplier[CompletionStage[T]]): CompletionStage[T] = {
    checkNotNull(body)
    withTransaction(InfiniteTimeout, body)
  }

  def withTransaction[T](timeout: Duration,
                         body: ThrowingSupplier[CompletionStage[T]]): CompletionStage[T]  = {
    checkNotNull(timeout)
    checkNotNull(body)
    convertExceptionsFut {
      underlying.withTransaction {
        body.supply().toScala
      }(timeout.asScala).toJava
    }
  }

  def release(): CompletionStage[Void] = convertExceptionsFut {
    underlying.release().map[Void](_ => null).toJava
  }

  def forceRelease(): CompletionStage[Void] = convertExceptionsFut {
    underlying.forceRelease().map[Void](_ => null).toJava
  }

  def validate(timeout: Duration): CompletionStage[Void] = {
    checkNotNull(timeout)
    convertExceptionsFut {
      underlying.validate()(timeout.asScala)
        .map(_ => null: Void).toJava
    }
  }

  def statement(sql: String): Statement = {
    checkNotNull(sql)
    convertExceptions {
      underlying.statement(sql).asJava
    }
  }

  def statement(sql: String, options: StatementOptions): Statement = {
    checkNotNull(sql)
    checkNotNull(options)
    convertExceptions {
      underlying.statement(sql, options.asScala).asJava
    }
  }

  def watchForIdle: CompletionStage[Connection] = convertExceptionsFut {
    underlying.watchForIdle.map[Connection](_ => this).toJava
  }

  override def toString: String = underlying.toString
}
