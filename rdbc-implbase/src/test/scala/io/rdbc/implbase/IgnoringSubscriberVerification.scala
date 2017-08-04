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

import org.reactivestreams.Subscription
import org.reactivestreams.tck.SubscriberWhiteboxVerification.{SubscriberPuppet, WhiteboxSubscriberProbe}
import org.reactivestreams.tck.{SubscriberWhiteboxVerification, TestEnvironment}
import org.scalatest.testng.TestNGSuiteLike

class IgnoringSubscriberVerification
  extends SubscriberWhiteboxVerification[Any](new TestEnvironment())
    with TestNGSuiteLike {

  def createSubscriber(probe: WhiteboxSubscriberProbe[Any]): IgnoringSubscriber = {
    new IgnoringSubscriber {
      override def onSubscribe(s: Subscription): Unit = {
        super.onSubscribe(s)

        probe.registerOnSubscribe(new SubscriberPuppet() {
          def signalCancel(): Unit = s.cancel()
          def triggerRequest(elements: Long): Unit = s.request(elements)
        })
      }

      override def onNext(elem: Any): Unit = {
        super.onNext(elem)
        probe.registerOnNext(elem)
      }

      override def onError(t: Throwable): Unit = {
        super.onError(t)
        probe.registerOnError(t)
      }

      override def onComplete(): Unit = {
        super.onComplete()
        probe.registerOnComplete()
      }
    }
  }

  def createElement(element: Int): Any = element
}
