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

import org.reactivestreams.{Publisher, Subscriber, Subscription}

private[jadapter]
trait MappingPublisher[A, B] extends Publisher[B] {

  private[jadapter] def underlying: Publisher[A]

  private[jadapter] def mapElem(elem: A): B

  private[jadapter] def mapError(t: Throwable): Throwable

  override def subscribe(mappedSubscriber: Subscriber[_ >: B]): Unit = {
    underlying.subscribe(new Subscriber[A] {
      def onError(t: Throwable): Unit = mappedSubscriber.onError(mapError(t))

      def onComplete(): Unit = mappedSubscriber.onComplete()

      def onNext(elem: A): Unit = mappedSubscriber.onNext(mapElem(elem))

      def onSubscribe(s: Subscription): Unit = mappedSubscriber.onSubscribe(s)
    })
  }
}
