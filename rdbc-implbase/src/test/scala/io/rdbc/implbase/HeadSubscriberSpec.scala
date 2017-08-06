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

package io.rdbc.implbase

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import io.rdbc.sapi.{Row, Timeout}
import org.scalamock.scalatest.MockFactory

import scala.concurrent.Promise

class HeadSubscriberSpec
  extends RdbcImplbaseSpec
    with MockFactory {

  private implicit val actorSystem: ActorSystem = ActorSystem()
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val timeout: Timeout = Timeout.Inf

  "HeadSubscriber" should {

    "request at most head elements" when {

      "there are more elements than head" in {
        runHeadSubscriberTest(head = Some(10), elemCount = 20)
      }

      "there are fewer elements than head" in {
        runHeadSubscriberTest(head = Some(20), elemCount = 10)
      }
    }

    "request all elements" when {
      "subscriber is not constrained" in {
        runHeadSubscriberTest(head = None, elemCount = 10)
      }
    }
  }

  def runHeadSubscriberTest(head: Option[Long], elemCount: Int): Unit = {
    val elems = (0 until elemCount).map(_ => mock[Row])
    val subscriber = new HeadSubscriber(head)

    val completePromise = Promise[Done]
    Source(elems)
      .watchTermination() { (mat, fut) =>
        completePromise.completeWith(fut)
        mat
      }
      .to(Sink.fromSubscriber(subscriber))
      .run()

    completePromise.future.get

    subscriber.rows.get
    val headElems = head.map(h => elems.take(h.toInt)).getOrElse(elems)
    subscriber.rows.get shouldBe headElems
  }

}
