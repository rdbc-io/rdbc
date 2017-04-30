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

package io.rdbc.sapi

import scala.reflect.ClassTag

private[sapi] trait SqlParamImplicits {
  implicit class Opt2OptParam[A: ClassTag](opt: Option[A]) {

    /** Converts this option to nullable SQL parameter. */
    def toSqlParam: SqlParam[A] = opt.map(NotNullParam.apply).getOrElse {
      val cls = implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]]
      NullParam(cls)
    }
  }
}

object SqlParam extends SqlParamImplicits

/** A statement parameter that can represent null (empty) values.
  *
  * This trait is analogous to standard [[scala.Option Option]], but keeps type information
  * for empty values. In some cases database engine cannot infer null parameter
  * type - instances of this trait can be used then.
  *
  * An implicit conversion is provided from [[scala.Option Option]] to instances of this
  * trait - see [[Opt2OptParam]].
  *
  * This trait does not provide any operations. It is recommended to use
  * [[scala.Option Option]] instances and at the final stage convert them to instances of
  * this trait when passed as statement parameters.
  */
sealed trait SqlParam[A]

/** Not null parameter value. */
case class NotNullParam[A](v: A) extends SqlParam[A]

/** Null parameter value */
case class NullParam[A](cls: Class[A]) extends SqlParam[A]
