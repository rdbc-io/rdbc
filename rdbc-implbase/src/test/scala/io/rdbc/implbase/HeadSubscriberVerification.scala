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

import java.time.{Instant, LocalDate, LocalDateTime, LocalTime}
import java.util.UUID

import io.rdbc.sapi.{Row, SqlNumeric}
import org.reactivestreams.Subscription
import org.reactivestreams.tck.SubscriberWhiteboxVerification.{SubscriberPuppet, WhiteboxSubscriberProbe}
import org.reactivestreams.tck.{SubscriberWhiteboxVerification, TestEnvironment}
import org.scalatest.testng.TestNGSuiteLike

import scala.reflect.ClassTag

class HeadSubscriberVerification
  extends SubscriberWhiteboxVerification[Row](new TestEnvironment())
    with TestNGSuiteLike {

  def createSubscriber(probe: WhiteboxSubscriberProbe[Row]): HeadSubscriber = {
    new HeadSubscriber(n = Some(100)) {
      override def onSubscribe(s: Subscription): Unit = {
        super.onSubscribe(s)

        probe.registerOnSubscribe(new SubscriberPuppet() {
          def signalCancel(): Unit = s.cancel()
          def triggerRequest(elements: Long): Unit = s.request(elements)
        })
      }

      override def onNext(elem: Row): Unit = {
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

  def createElement(element: Int): Row = dummyRow

  private def dummyRow: Row = new Row {
    def localDateTime(name: String): LocalDateTime = ???
    def localDateTime(idx: Int): LocalDateTime = ???
    def col[A: ClassTag](idx: Int): A = ???
    def col[A: ClassTag](name: String): A = ???
    def shortOpt(name: String): Option[Short] = ???
    def shortOpt(idx: Int): Option[Short] = ???
    def bool(name: String): Boolean = ???
    def bool(idx: Int): Boolean = ???
    def numeric(name: String): SqlNumeric = ???
    def numeric(idx: Int): SqlNumeric = ???
    def float(name: String): Float = ???
    def float(idx: Int): Float = ???
    def uuid(name: String): UUID = ???
    def uuid(idx: Int): UUID = ???
    def long(name: String): Long = ???
    def long(idx: Int): Long = ???
    def instant(name: String): Instant = ???
    def instant(idx: Int): Instant = ???
    def doubleOpt(name: String): Option[Double] = ???
    def doubleOpt(idx: Int): Option[Double] = ???
    def localDateOpt(name: String): Option[LocalDate] = ???
    def localDateOpt(idx: Int): Option[LocalDate] = ???
    def bigDecimalOpt(name: String): Option[BigDecimal] = ???
    def bigDecimalOpt(idx: Int): Option[BigDecimal] = ???
    def strOpt(name: String): Option[String] = ???
    def strOpt(idx: Int): Option[String] = ???
    def instantOpt(name: String): Option[Instant] = ???
    def instantOpt(idx: Int): Option[Instant] = ???
    def localDate(name: String): LocalDate = ???
    def localDate(idx: Int): LocalDate = ???
    def bigDecimal(name: String): BigDecimal = ???
    def bigDecimal(idx: Int): BigDecimal = ???
    def longOpt(name: String): Option[Long] = ???
    def longOpt(idx: Int): Option[Long] = ???
    def double(name: String): Double = ???
    def double(idx: Int): Double = ???
    def colOpt[A: ClassTag](idx: Int): Option[A] = ???
    def colOpt[A: ClassTag](name: String): Option[A] = ???
    def localTimeOpt(name: String): Option[LocalTime] = ???
    def localTimeOpt(idx: Int): Option[LocalTime] = ???
    def uuidOpt(name: String): Option[UUID] = ???
    def uuidOpt(idx: Int): Option[UUID] = ???
    def int(name: String): Int = ???
    def int(idx: Int): Int = ???
    def numericOpt(name: String): Option[SqlNumeric] = ???
    def numericOpt(idx: Int): Option[SqlNumeric] = ???
    def str(name: String): String = ???
    def str(idx: Int): String = ???
    def localTime(name: String): LocalTime = ???
    def localTime(idx: Int): LocalTime = ???
    def localDateTimeOpt(name: String): Option[LocalDateTime] = ???
    def localDateTimeOpt(idx: Int): Option[LocalDateTime] = ???
    def charOpt(name: String): Option[Char] = ???
    def charOpt(idx: Int): Option[Char] = ???
    def boolOpt(name: String): Option[Boolean] = ???
    def boolOpt(idx: Int): Option[Boolean] = ???
    def intOpt(name: String): Option[Int] = ???
    def intOpt(idx: Int): Option[Int] = ???
    def bytesOpt(name: String): Option[Array[Byte]] = ???
    def bytesOpt(idx: Int): Option[Array[Byte]] = ???
    def bytes(name: String): Array[Byte] = ???
    def bytes(idx: Int): Array[Byte] = ???
    def char(name: String): Char = ???
    def char(idx: Int): Char = ???
    def floatOpt(name: String): Option[Float] = ???
    def floatOpt(idx: Int): Option[Float] = ???
    def short(name: String): Short = ???
    def short(idx: Int): Short = ???
  }
}
