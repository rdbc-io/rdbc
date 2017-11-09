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

public final class StatementOptions {

    /** Default statement options */
    public static final StatementOptions DEFAULT = StatementOptions.of(KeyColumns.NONE);

    /** Options that make statement return all generated keys */
    public static final StatementOptions RETURN_GEN_KEYS = StatementOptions.of(KeyColumns.ALL);

    private final KeyColumns generatedKeyCols;

    private StatementOptions(KeyColumns generatedKeyCols) {
        this.generatedKeyCols = generatedKeyCols;
    }

    public KeyColumns getGeneratedKeyCols() {
        return generatedKeyCols;
    }

    public static StatementOptions of(KeyColumns generatedKeyCols) {
        return new StatementOptions(generatedKeyCols);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatementOptions that = (StatementOptions) o;

        return generatedKeyCols.equals(that.generatedKeyCols);
    }

    @Override
    public int hashCode() {
        return generatedKeyCols.hashCode();
    }

    @Override
    public String toString() {
        return "StatementOptions(" +
                "generatedKeyCols=" + generatedKeyCols +
                ')';
    }
}
