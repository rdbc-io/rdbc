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

package io.rdbc.test

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import io.rdbc.sapi._
import org.reactivestreams.Publisher
import org.reactivestreams.tck.{PublisherVerification, TestEnvironment}
import org.scalatest.testng.TestNGSuiteLike

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

abstract class RowPublisherVerification(env: TestEnvironment, publisherShutdownTimeout: Long)
  extends PublisherVerification[Row](env, publisherShutdownTimeout)
    with TestNGSuiteLike {

  private implicit val system = ActorSystem("RowPublisherVerification") //TODO shutdown the system
  private implicit val materializer = ActorMaterializer()

  protected implicit def ec: ExecutionContext = ExecutionContext.global

  protected implicit def waitAtMost: Duration = 10.seconds

  protected implicit def timeout = Timeout(waitAtMost)

  protected def connection(): Connection

  protected def intDataType: String
  protected def varcharDataType: String
  protected def castVarchar2Int(colName: String): String

  private val idx = new AtomicInteger(0)

  override def maxElementsFromPublisher(): Long = 100L

  override def createPublisher(elements: Long): Publisher[Row] = {
    val conn = connection()
    val currIdx = idx.incrementAndGet()
    conn.statement(s"create table tbl$currIdx (col $intDataType)").get.noArgs.execute().get
    conn.beginTx().get
    for { i <- 1L to elements } yield {
      conn.statement(sql"insert into tbl#$currIdx(col) values($i)").get.execute().get
    }
    conn.commitTx().get
    val pub = conn.statement(s"select col from tbl$currIdx").get.noArgs.executeForStream().get.rows
    Source.fromPublisher(pub)
      .watchTermination() { (_, doneFut) =>
        doneFut.onComplete { _ =>
          conn.forceRelease().get
        }
      }
      .runWith(Sink.asPublisher(false))
  }

  override def createFailedPublisher(): Publisher[Row] = {
    val conn = connection()
    val currIdx = idx.incrementAndGet()
    conn.statement(s"create table tbl$currIdx (col $varcharDataType)").get.noArgs.execute().get
    conn.statement(s"insert into tbl$currIdx(col) values('text')").get.noArgs.execute().get
    val pub = conn.statement(s"select ${castVarchar2Int("col")} from tbl$currIdx").get
      .noArgs
      .executeForStream().get
      .rows
    Source.fromPublisher(pub)
      .watchTermination() { (_, doneFut) =>
        doneFut.onComplete { _ =>
          conn.forceRelease().get
        }
      }
      .runWith(Sink.asPublisher(false))
  }

}
