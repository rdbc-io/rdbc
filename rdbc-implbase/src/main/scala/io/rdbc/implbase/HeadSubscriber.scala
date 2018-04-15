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

import io.rdbc._
import io.rdbc.sapi.Row
import io.rdbc.util.Preconditions.checkNotNull
import org.reactivestreams.{Subscriber, Subscription}

import scala.concurrent.{Future, Promise}

class HeadSubscriber(n: Option[Long]) extends Subscriber[Row] {
  private val promise = Promise[ImmutSeq[Row]]
  private var buf = Vector.empty[Row]

  private var subscription: Option[Subscription] = None
  private val subscribed = new AtomicBoolean(false)

  val rows: Future[ImmutSeq[Row]] = promise.future

  override def onError(t: Throwable): Unit = {
    checkNotNull(t)
    promise.failure(t)
  }

  override def onSubscribe(s: Subscription): Unit = {
    checkNotNull(s)
    if (subscribed.compareAndSet(false, true)) {
      subscription = Some(s)
      s.request(n.getOrElse(Long.MaxValue))
    } else {
      s.cancel()
    }
  }

  override def onComplete(): Unit = {
    if (!promise.isCompleted) {
      promise.success(buf)
    }
  }

  override def onNext(row: Row): Unit = {
    checkNotNull(row)
    buf = buf :+ row
    if (n.contains(buf.size)) {
      promise.success(buf)
      subscription.foreach(_.cancel())
    }
  }
}
