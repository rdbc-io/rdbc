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
import io.rdbc.sapi

import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext

private[jadapter] class ConnectionAdapter(val underlying: sapi.Connection)
                                         (implicit ec: ExecutionContext,
                                          exConversion: ExceptionConversion)
  extends Connection {

  import exConversion._

  def beginTx(timeout: Duration): CompletionStage[Void] = convertExceptionsFut {
    underlying.beginTx()(timeout.asScala).map[Void](_ => null).toJava
  }

  def beginTx(): CompletionStage[Void] = convertExceptionsFut {
    beginTx(InfiniteDuration)
  }

  def commitTx(timeout: Duration): CompletionStage[Void] = convertExceptionsFut {
    underlying.commitTx()(timeout.asScala).map[Void](_ => null).toJava
  }

  def commitTx(): CompletionStage[Void] = convertExceptionsFut {
    commitTx(InfiniteDuration)
  }

  def rollbackTx(timeout: Duration): CompletionStage[Void] = convertExceptionsFut {
    underlying.rollbackTx()(timeout.asScala).map[Void](_ => null).toJava
  }

  def rollbackTx(): CompletionStage[Void] = convertExceptionsFut {
    rollbackTx(InfiniteDuration)
  }

  def release(): CompletionStage[Void] = convertExceptionsFut {
    underlying.release().map[Void](_ => null).toJava
  }

  def forceRelease(): CompletionStage[Void] = convertExceptionsFut {
    underlying.forceRelease().map[Void](_ => null).toJava
  }

  def validate(timeout: Duration): CompletionStage[Void] = convertExceptionsFut {
    underlying.validate()(timeout.asScala)
      .map(_ => null: Void).toJava
  }

  def statement(sql: String): Statement = convertExceptions {
    underlying.statement(sql).asJava
  }

  def statement(sql: String, options: StatementOptions): Statement = convertExceptions {
    underlying.statement(sql, options.asScala).asJava
  }

  def watchForIdle: CompletionStage[Connection] = convertExceptionsFut {
    underlying.watchForIdle.map[Connection](_ => this).toJava
  }

  override def toString: String = underlying.toString
}
