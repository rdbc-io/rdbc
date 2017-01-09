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

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

public final class SqlNumeric {

    private enum Type {
        NAN, POS_INF, NEG_INF, FINITE
    }

    private final Type type;
    private final Optional<BigDecimal> value;

    public static final SqlNumeric NAN = new SqlNumeric(Type.NAN, Optional.empty());
    public static final SqlNumeric NEG_INFINITY = new SqlNumeric(Type.NEG_INF, Optional.empty());
    public static final SqlNumeric POS_INFINITY = new SqlNumeric(Type.POS_INF, Optional.empty());

    private SqlNumeric(Type type, Optional<BigDecimal> value) {
        this.type = type;
        this.value = value;
    }

    public static SqlNumeric of(BigDecimal value) {
        return new SqlNumeric(Type.FINITE, Optional.of(value));
    }

    public boolean isNaN() {
        return type == Type.NAN;
    }

    public boolean isPosInifinity() {
        return type == Type.POS_INF;
    }

    public boolean isNegInifinity() {
        return type == Type.NEG_INF;
    }

    public boolean isFinite() {
        return type == Type.FINITE;
    }

    public BigDecimal getValue() {
        return value.orElseThrow(() ->
                new NoSuchElementException("SqlNumeric value is " + type.name())
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SqlNumeric that = (SqlNumeric) o;

        return type == that.type && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        if (isFinite()) {
            return "SqlNumeric(" + getValue() + ")";
        } else {
            return "SqlNumeric(" + type + ")";
        }
    }
}
