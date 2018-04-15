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

import java.util.concurrent.atomic.AtomicBoolean

import io.rdbc.util.Preconditions.checkNotNull
import org.reactivestreams.{Subscriber, Subscription}

class IgnoringSubscriber extends Subscriber[Any] {

  private val subscribed = new AtomicBoolean(false)

  override def onError(t: Throwable): Unit = {
    checkNotNull(t)
  }

  override def onSubscribe(s: Subscription): Unit = {
    checkNotNull(s)
    if (subscribed.compareAndSet(false, true)) {
      s.request(Long.MaxValue)
    } else s.cancel()
  }

  override def onComplete(): Unit = ()

  override def onNext(elem: Any): Unit = {
    checkNotNull(elem)
  }
}
