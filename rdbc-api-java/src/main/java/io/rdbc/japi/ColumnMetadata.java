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


import java.util.Optional;

/**
 * Represents column's metadata.
 */
public final class ColumnMetadata {

    private final String name;
    private final String dbTypeId;
    private final Optional<Class<?>> valueClass;

    private ColumnMetadata(String name, String dbTypeId, Optional<Class<?>> valueClass) {
        this.name = name;
        this.dbTypeId = dbTypeId;
        this.valueClass = valueClass;
    }

    /**
     * Creates new ColumnMetadata instance.
     *
     * @param name     column name
     * @param dbTypeId database vendor identifier of a datatype declared for the column
     */
    public static ColumnMetadata of(String name, String dbTypeId) {
        return new ColumnMetadata(name, dbTypeId, Optional.empty());
    }

    /**
     * Creates new ColumnMetadata instance.
     *
     * @param name     column name
     * @param dbTypeId database vendor identifier of a datatype declared for the column
     * @param cls      JVM class that a database driver uses to represent values of the column
     */
    public static ColumnMetadata of(String name, String dbTypeId, Class<?> cls) {
        return new ColumnMetadata(name, dbTypeId, Optional.of(cls));
    }

    public String getName() {
        return name;
    }

    public String getDbTypeId() {
        return dbTypeId;
    }

    public Optional<Class<?>> getValueClass() {
        return valueClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnMetadata that = (ColumnMetadata) o;

        if (!name.equals(that.name)) return false;
        if (!dbTypeId.equals(that.dbTypeId)) return false;
        return valueClass.equals(that.valueClass);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + dbTypeId.hashCode();
        result = 31 * result + valueClass.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ColumnMetadata(" +
                "name='" + name + '\'' +
                ", dbTypeId='" + dbTypeId + '\'' +
                ", valueClass=" + valueClass +
                ')';
    }
}
