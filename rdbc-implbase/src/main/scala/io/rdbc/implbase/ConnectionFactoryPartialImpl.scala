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

import io.rdbc.sapi.{Connection, ConnectionFactory, Timeout}
import io.rdbc.util.Futures._
import io.rdbc.util.Logging
import io.rdbc.util.Preconditions.checkNotNull

import scala.concurrent.{ExecutionContext, Future}

trait ConnectionFactoryPartialImpl
  extends ConnectionFactory
    with Logging {

  implicit protected def ec: ExecutionContext

  override def withConnection[A](body: Connection => Future[A])
                                (implicit timeout: Timeout): Future[A] = {
    checkNotNull(body)
    checkNotNull(timeout)
    connection().flatMap { conn =>
      body(conn).andThenF { case _ =>
        conn.release()
      }
    }
  }

  override def withTransaction[A](body: Connection => Future[A])
                                  (implicit timeout: Timeout): Future[A] = {
    checkNotNull(body)
    checkNotNull(timeout)
    withConnection { conn =>
      conn.withTransaction {
        body(conn)
      }
    }
  }

}
