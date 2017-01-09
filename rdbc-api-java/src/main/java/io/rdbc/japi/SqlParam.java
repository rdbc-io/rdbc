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

import java.util.NoSuchElementException;
import java.util.Optional;

public final class SqlParam<T> {

    private final Class<T> type;
    private final Optional<T> value;

    public static <T> SqlParam<T> sqlNull(Class<T> type) {
        return new SqlParam<>(type, Optional.empty());
    }

    public static <T> SqlParam<T> of(Class<T> type, T value) {
        return new SqlParam<>(type, Optional.of(value));
    }

    private SqlParam(Class<T> type, Optional<T> value) {
        this.type = type;
        this.value = value;
    }

    public Class<T> getType() {
        return type;
    }

    public T getValue() {
        return value.orElseThrow(() ->
                new NoSuchElementException("SqlParam value is null")
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SqlParam<?> sqlParam = (SqlParam<?>) o;

        return type.equals(sqlParam.type) && value.equals(sqlParam.value);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SqlParam{" +
                "type=" + type +
                ", value=" + value.map(Object::toString).orElse("NULL") +
                '}';
    }
}
