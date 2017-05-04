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

package io.rdbc.test.util

import io.rdbc.ImmutSeq
import io.rdbc.sapi.{Row, Timeout}
import io.rdbc.test._
import org.reactivestreams.{Subscriber, Subscription}

import scala.concurrent.{Future, Promise}

private[test] object Subscribers {
  def eager(): HeadSubscriber = new HeadSubscriber(None)
  def head(n: Long): HeadSubscriber = new HeadSubscriber(Some(n))
  def ignoring(): IgnoringSubscriber = new IgnoringSubscriber
  def chunk(implicit subscribeTimeout: Timeout): ChunkSubscriber = new ChunkSubscriber()(subscribeTimeout)
}

private[test] class IgnoringSubscriber extends Subscriber[Any] {
  override def onError(t: Throwable): Unit = ()

  override def onSubscribe(s: Subscription): Unit = s.request(Long.MaxValue)

  override def onComplete(): Unit = ()

  override def onNext(ignored: Any): Unit = ()
}

private[test] class HeadSubscriber(n: Option[Long]) extends Subscriber[Row] {
  private val promise = Promise[ImmutSeq[Row]]
  private var buf = Vector.empty[Row]

  private var subscription: Option[Subscription] = None

  val rows: Future[ImmutSeq[Row]] = promise.future

  override def onError(t: Throwable): Unit = promise.failure(t)

  override def onSubscribe(s: Subscription): Unit = {
    subscription = Some(s)
    val demand = n.getOrElse(Long.MaxValue)
    if (demand != 0) {
      s.request(demand)
    } else {
      s.cancel()
    }
  }

  override def onComplete(): Unit = {
    if (!promise.isCompleted) {
      promise.success(buf)
    }
  }

  override def onNext(t: Row): Unit = {
    buf = buf :+ t
    if (n.contains(buf.size)) {
      promise.success(buf)
      subscription.foreach(_.cancel())
    }
  }
}

private[test] class ChunkSubscriber()(implicit subscribeTimeout: Timeout) extends Subscriber[Row] {
  private var _rowsPromise = Promise[ImmutSeq[Row]]
  private val completionPromise = Promise[Unit]
  private var buf = Vector.empty[Row]

  private val subscriptionPromise = Promise[Subscription]
  private var _demand: Long = 0
  val completion: Future[Unit] = completionPromise.future

  def demand: Long = _demand

  def request(n: Long, rowsPromise: Promise[ImmutSeq[Row]]): Unit = {
    _demand = n
    _rowsPromise = rowsPromise
    subscriptionPromise.future.get.request(n)
  }

  def cancel(): Unit = {
    subscriptionPromise.future.get.cancel()
  }

  override def onError(t: Throwable): Unit = _rowsPromise.failure(t)

  override def onSubscribe(s: Subscription): Unit = {
    subscriptionPromise.success(s)
  }

  override def onComplete(): Unit = {
    if (!_rowsPromise.isCompleted) {
      _rowsPromise.success(buf)
    }
    completionPromise.success(())
  }

  override def onNext(t: Row): Unit = {
    buf = buf :+ t
    _demand -= 1
    if (_demand == 0) {
      _rowsPromise.success(buf)
      buf = Vector.empty
    }
  }
}
