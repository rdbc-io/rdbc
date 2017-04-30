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

import io.rdbc._

/** Represents a set of rows returned by a database engine.
  *
  * @param rowsAffected a number of rows that were affected by the statement
  *                     that this result set is for
  * @param warnings     a sequence of warnings that were emitted by the database
  *                     during processing the statement that this result set is for
  * @param metadata     a meta data of columns of this result set
  * @param rows         a sequence of rows returned by a database
  */
class ResultSet(val rowsAffected: Long,
                val warnings: ImmutSeq[Warning],
                val metadata: RowMetadata,
                val rows: ImmutSeq[Row]) extends Iterable[Row] {
  def iterator: Iterator[Row] = rows.iterator
}
