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

import io.rdbc.japi.exceptions.MissingParamValException;
import io.rdbc.japi.exceptions.NoSuchParamException;
import io.rdbc.japi.exceptions.NoSuitableConverterFoundException;
import io.rdbc.japi.exceptions.TooManyParamsException;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * Represents a SQL statement
 * <p>
 * Methods of this interface allow to bind argument values to parameters
 * either by name or index.
 */
public interface Statement {

    /**
     * Binds each parameter by name.
     */
    ExecutableStatement bind(Map<String, Object> params)
            throws NoSuchParamException,
            MissingParamValException,
            NoSuitableConverterFoundException;

    /**
     * Binds one named parameter to a value.
     */
    default StatementArgBinder arg(String name, Object value) {
        return new StatementArgBinder(this).arg(name, value);
    }

    /**
     * Binds each parameter by index.
     * <p>
     * Parameters are ordered, each value in {@code params} sequence will be bound
     * to the corresponding parameter.
     */
    ExecutableStatement bindByIdx(Object... params)
            throws MissingParamValException,
            NoSuitableConverterFoundException,
            TooManyParamsException;

    /**
     * Returns a parametrized version of the bindable object without
     * providing any parameters.
     */
    ExecutableStatement noArgs() throws MissingParamValException;

    /**
     * Streams statement named arguments to a database.
     * <p>
     * This method can be used to repeatedly execute this statement with
     * published parameters by leveraging Reactive Streams specification's
     * {@code Publisher} with a backpressure. Each published element is a map
     * containing all parameters required by this statement.
     * <p>
     * Resulting {@link CompletionStage} can fail with:
     * <ul>
     * <li>{@link NoSuchParamException}</li>
     * <li>{@link MissingParamValException}</li>
     * <li>{@link NoSuitableConverterFoundException}</li>
     * </ul>
     */
    CompletionStage<Void> streamArgs(Publisher<Map<String, Object>> paramsPublisher);

    /**
     * Streams statement positional arguments to a database.
     * <p>
     * This method can be used to repeatedly execute this statement with
     * published parameters by leveraging Reactive Streams specification's
     * {@code Publisher} with a backpressure. Each published element is a map
     * containing all parameters required by this statement.
     * <p>
     * Resulting {@link CompletionStage} can fail with:
     * <ul>
     * <li>{@link TooManyParamsException}</li>
     * <li>{@link MissingParamValException}</li>
     * <li>{@link NoSuitableConverterFoundException}</li>
     * </ul>
     */
    CompletionStage<Void> streamArgsByIdx(Publisher<List<Object>> paramsPublisher);

}
