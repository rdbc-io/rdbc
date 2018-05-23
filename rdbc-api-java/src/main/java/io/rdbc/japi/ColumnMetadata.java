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


import java.util.Objects;
import java.util.Optional;

/**
 * Represents column's metadata.
 */
public final class ColumnMetadata {

    private final String name;
    private final String dbTypeId;

    private ColumnMetadata(String name, String dbTypeId) {
        this.name = name;
        this.dbTypeId = dbTypeId;
    }

    /**
     * Creates new ColumnMetadata instance.
     *
     * @param name     column name
     * @param dbTypeId database vendor identifier of a datatype declared for the column
     */
    public static ColumnMetadata of(String name, String dbTypeId) {
        return new ColumnMetadata(name, dbTypeId);
    }

    public String getName() {
        return name;
    }

    public String getDbTypeId() {
        return dbTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnMetadata that = (ColumnMetadata) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(dbTypeId, that.dbTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dbTypeId);
    }

    @Override
    public String toString() {
        return "ColumnMetadata{" +
                "name='" + name + '\'' +
                ", dbTypeId='" + dbTypeId + '\'' +
                '}';
    }
}
