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

package io.rdbc

import io.rdbc.sapi.{NotNullParam, NullParam}
import io.rdbc.sapi.SqlParam._

class SqlParamSpec extends RdbcSpec {

  "SqlParam implicit conversion" should {

    "convert None to NULL param" in {
      Option.empty[String].toSqlParam shouldBe NullParam(classOf[String])
    }

    "convert Some to not-NULL param" in {
      val value = 10
      Some(value).toSqlParam shouldBe NotNullParam(value)
    }
  }

}
