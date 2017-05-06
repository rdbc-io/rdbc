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

import io.rdbc.sapi._

trait TxSpec {
  this: RdbcSpec with TableSpec =>

  protected def withAndWithoutTx(block: (Connection, String) => Unit): Unit = {
    "inside a transaction" in { c =>
      withTable(c) { t =>
        c.beginTx().get
        try {
          block(c, t)
          c.commitTx().get
        } finally {
          c.rollbackTx().get
        }
      }
    }

    "without explicit transaction" in { c =>
      withTable(c) { tableName =>
        block(c, tableName)
      }
    }
  }
}
