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

import java.util.*;

public final class KeyColumns {

    public enum Type {
        ALL, NONE, COLUMNS
    }

    public static final KeyColumns ALL = new KeyColumns(Optional.empty(), Type.ALL);
    public static final KeyColumns NONE = new KeyColumns(Optional.empty(), Type.NONE);

    private final Optional<List<String>> columns;
    private final Type type;

    private KeyColumns(Optional<List<String>> columns, Type type) {
        this.columns = columns;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public List<String> getColumns() {
        return columns.map(ArrayList::new).orElseThrow(() ->
                new NoSuchElementException("KeyColumns value is " + type.name())
        );
    }

    public static KeyColumns columns(String... columns) {
        return new KeyColumns(Optional.of(Arrays.asList(columns)), Type.COLUMNS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyColumns that = (KeyColumns) o;

        return columns.equals(that.columns) && type == that.type;
    }

    @Override
    public int hashCode() {
        int result = columns.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        if (type == Type.COLUMNS) {
            return "KeyColumns(columns=" + getColumns() + ")";
        } else {
            return "KeyColumns(" + type + ")";
        }
    }
}
