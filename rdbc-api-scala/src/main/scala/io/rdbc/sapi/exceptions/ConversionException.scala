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

package io.rdbc.sapi.exceptions

class ConversionException(msg: String,
                          val value: Any,
                          val targetType: Class[_],
                          maybeCause: Option[Throwable])
  extends RdbcException(msg, maybeCause) {

  def this(value: Any,
           targetType: Class[_],
           maybeCause: Option[Throwable] = None) = {
    this(
      msg = s"Value '$value' of type '${value.getClass.getCanonicalName}' " +
        s"could not be converted to '${targetType.getCanonicalName}'",
      value = value,
      targetType = targetType,
      maybeCause = maybeCause)
  }

}
