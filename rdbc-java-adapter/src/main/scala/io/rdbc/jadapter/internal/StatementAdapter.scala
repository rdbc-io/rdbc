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

import java.util.concurrent.CompletionStage
import java.util.{List => JList, Map => JMap}

import io.rdbc.japi.{ExecutableStatement, Statement}
import io.rdbc.jadapter.internal.Conversions._
import io.rdbc.{ImmutIndexedSeq, sapi}
import org.reactivestreams.Publisher

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext

private[jadapter]
object StatementAdapter {

  class Java2ScalaNamedParamPublisher(val underlying: Publisher[JMap[String, Object]])
    extends MappingPublisher[JMap[String, Object], Map[String, Any]] {

    def mapElem(elem: JMap[String, Object]): Map[String, Object] = elem.asScala.toMap

    def mapError(t: Throwable): Throwable = t
  }

  class Java2ScalaPositionalParamPublisher(val underlying: Publisher[JList[Object]])
    extends MappingPublisher[JList[Object], ImmutIndexedSeq[Any]] {

    def mapElem(elem: JList[Object]): ImmutIndexedSeq[Object] = elem.asScala.toVector

    def mapError(t: Throwable): Throwable = t
  }

}

private[jadapter]
class StatementAdapter(protected val underlying: sapi.Statement)(implicit ec: ExecutionContext,
                                                                 exConversion: ExceptionConversion)
  extends Statement {

  import StatementAdapter._
  import exConversion._

  def bind(params: JMap[String, Object]): ExecutableStatement = convertExceptions {
    underlying.bind(params.asScala.toSeq: _*).asJava
  }

  def bindByIdx(params: Object*): ExecutableStatement = convertExceptions {
    underlying.bindByIdx(params).asJava
  }

  def noArgs(): ExecutableStatement = convertExceptions {
    underlying.noArgs.asJava
  }

  def streamArgs(paramsPublisher: Publisher[JMap[String, Object]]): CompletionStage[Void] = convertExceptionsFut {
    underlying.streamArgs(
      new Java2ScalaNamedParamPublisher(paramsPublisher)
    ).map(_ => null: Void).toJava
  }

  def streamArgsByIdx(paramsPublisher: Publisher[JList[Object]]): CompletionStage[Void] = convertExceptionsFut {
    underlying.streamArgsByIdx(
      new Java2ScalaPositionalParamPublisher(paramsPublisher)
    ).map(_ => null: Void).toJava
  }

  override def toString: String = underlying.toString

}
