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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StatementArgBinder {
    private final Statement statement;
    private final Map<String, Object> args;

    StatementArgBinder(Statement statement) {
        this.statement = statement;
        this.args = new HashMap<>();
    }

    /**
     * Binds one named parameter to a value.
     */
    public StatementArgBinder arg(String name, Object value) {
        Objects.requireNonNull(name, "name cannot be null");
        args.put(name, value);
        return this;
    }

    /**
     * Finishes binding named parameters.
     */
    public ExecutableStatement bind() {
        return statement.bind(args);
    }
}
