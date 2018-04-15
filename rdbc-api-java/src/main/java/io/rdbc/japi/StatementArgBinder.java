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
