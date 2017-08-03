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

import io.rdbc.sapi.{KeyColumns, StatementOptions}

class StatementOptionsSpec extends RdbcSpec {

  "StatementOptions.Default" should {
    "be set not to return any generated keys" in {
      StatementOptions.Default.generatedKeyCols shouldBe KeyColumns.None
    }
  }

  "StatementOptions.ReturnGenKeys" should {
    "be set to return all generated keys" in {
      StatementOptions.ReturnGenKeys.generatedKeyCols shouldBe KeyColumns.All
    }
  }

  "KeyColumns.named" should {
    "create KeyColumns.Named instances" in {
      KeyColumns.named("c1", "c2") shouldBe KeyColumns.Named(Vector("c1", "c2"))
    }
  }

}
