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

package io.rdbc.util

object Preconditions {

  def notNull(args: sourcecode.Text[_]*): Unit = {
    args.foreach { param =>  //TODO this has major performance hit
      if (param == null) throw new NullPointerException(s"Parameter '${param.source}' cannot be null")
    }
  }

  def argsNotNull()(implicit args: sourcecode.Args): Unit = {
    notNull(args.value.flatten: _*) //TODO this has major performance hit
  }

  def check[A](arg: sourcecode.Text[A], requirement: Boolean, msg: String): Unit = {
    require(requirement, s"Requirement failed for parameter '${arg.source}': $msg")
  }

  def checkNonEmpty(arg: sourcecode.Text[TraversableOnce[_]]): Unit = {
    check(arg, arg.value.nonEmpty, "cannot be empty")
  }

  def checkNonEmptyString(arg: sourcecode.Text[String]): Unit = {
    check(arg, !arg.value.isEmpty, "cannot be empty")
  }

}
