package io.rdbc

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Awaitable}

package object test {

  implicit class AwaitableOps[T](a: Awaitable[T]) {
    def get(implicit atMost: Duration): T = Await.result(a, atMost)
  }

}
