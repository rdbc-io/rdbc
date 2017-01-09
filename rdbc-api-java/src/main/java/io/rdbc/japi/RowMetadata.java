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

package io.rdbc.japi;

import java.util.ArrayList;
import java.util.List;

public final class RowMetadata {

    private final List<ColumnMetadata> columns;

    private RowMetadata(List<ColumnMetadata> columns) {
        this.columns = new ArrayList<>(columns);
    }

    public static RowMetadata of(List<ColumnMetadata> columns) {
        return new RowMetadata(columns);
    }

    public List<ColumnMetadata> getColumns() {
        return new ArrayList<>(columns);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RowMetadata that = (RowMetadata) o;

        return columns.equals(that.columns);
    }

    @Override
    public int hashCode() {
        return columns.hashCode();
    }

    @Override
    public String toString() {
        return "RowMetadata(" +
                "columns=" + columns +
                ')';
    }
}
