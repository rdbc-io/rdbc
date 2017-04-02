package io.rdbc.test.util

import io.rdbc.ImmutSeq
import io.rdbc.sapi.Row
import org.reactivestreams.{Subscriber, Subscription}

import scala.concurrent.{Future, Promise}

object Subscribers {
  def eager(): HeadSubscriber = new HeadSubscriber(None)
  def head(n: Long): HeadSubscriber = new HeadSubscriber(Some(n))
  def ignoring(): IgnoringSubscriber = new IgnoringSubscriber
}

class IgnoringSubscriber extends Subscriber[Any] {
  override def onError(t: Throwable): Unit = ()

  override def onSubscribe(s: Subscription): Unit = s.request(Long.MaxValue)

  override def onComplete(): Unit = ()

  override def onNext(ignored: Any): Unit = ()
}

class HeadSubscriber(n: Option[Long]) extends Subscriber[Row] {
  private val promise = Promise[ImmutSeq[Row]]
  private var buf = Vector.empty[Row]

  private var subscription: Option[Subscription] = None

  val rows: Future[ImmutSeq[Row]] = promise.future

  override def onError(t: Throwable): Unit = promise.failure(t)

  override def onSubscribe(s: Subscription): Unit = {
    subscription = Some(s)
    s.request(n.getOrElse(Long.MaxValue))
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