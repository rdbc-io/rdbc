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

import io.rdbc.sapi.{Connection, Timeout}
import org.scalatest.{Matchers, Outcome, fixture}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait RdbcSpec extends fixture.FreeSpec with Matchers {
  type FixtureParam = Connection

  protected implicit def ec: ExecutionContext = ExecutionContext.global

  protected implicit def waitAtMost: Duration = 10.seconds
  protected implicit def timeout = Timeout(waitAtMost)
  protected def connection(): Connection

  protected def withFixture(test: OneArgTest): Outcome = {
    val conn = connection()
    try {
      withFixture(test.toNoArgTest(conn))
    } finally {
      conn.forceRelease().get
    }
  }
}
