/*
 * Copyright 2016 Krzysztof Pado
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

import io.rdbc.sapi.{AnyParametrizedStatement, AnyStatement}

class StmtWrapper[A](underlying: AnyStatement)(parametrizedConv: AnyParametrizedStatement => A)
  extends BindablePartialImpl[A] {

  override def bind(params: (String, Any)*): A = {
    parametrizedConv(underlying.bind(params: _*))
  }

  override def bindByIdx(params: Any*): A = {
    parametrizedConv(underlying.bindByIdx(params: _*))
  }

  override def noParams: A = {
    parametrizedConv(underlying.noParams)
  }
}
