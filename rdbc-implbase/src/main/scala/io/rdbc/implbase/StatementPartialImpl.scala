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

package io.rdbc.implbase

import io.rdbc.sapi.{ExecutableStatement, Statement}

import scala.concurrent.Future
import scala.util.Try

trait StatementPartialImpl extends Statement {

  override def bindF(params: (String, Any)*): Future[ExecutableStatement] = {
    Future.fromTry(Try(bind(params: _*)))
  }

  override def bindByIdxF(params: Any*): Future[ExecutableStatement] = {
    Future.fromTry(Try(bindByIdx(params: _*)))
  }

  override def noArgsF: Future[ExecutableStatement] = {
    Future.fromTry(Try(noArgs))
  }
}
