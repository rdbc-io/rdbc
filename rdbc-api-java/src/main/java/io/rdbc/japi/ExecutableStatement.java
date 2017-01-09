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

import io.rdbc.japi.util.ThrowingFunction;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Represents an executable SQL statement.
 * <p>
 * Executable statement is a statement that has all parameters
 * provided and is ready to be executed.
 */
public interface ExecutableStatement {

    /**
     * Executes this statement and returns a {@link RowPublisher} instance
     * that can be used to stream rows from the database leveraging
     * Reactive Streams specification's {@code Publisher} with backpressure.
     * <p>
     * After the operation takes longer time than {@code timeout}, operation
     * will be aborted. Note however, that it may not be feasible
     * to abort the operation immediately.
     * <p>
     * Returned publisher can signal following error types:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.UnauthorizedException} when client is not authorized to perform the action</li>
     * <li>{@link io.rdbc.japi.exceptions.InvalidQueryException} when query is rejected by a database engine as invalid</li>
     * <li>{@link io.rdbc.japi.exceptions.InactiveTxException} when transaction is in progress but is in inactive state</li>
     * <li>{@link io.rdbc.japi.exceptions.ConstraintViolationException} when operation results in an integrity constraint violation</li>
     * </ul>
     */
    RowPublisher stream(Duration timeout);

    /**
     * Executes this statement and returns a {@link RowPublisher} instance
     * that can be used to stream rows from the database leveraging
     * Reactive Streams specification's {@code Publisher} with backpressure.
     * <p>
     * Returned publisher can signal following error types:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.UnauthorizedException} when client is not authorized to perform the action</li>
     * <li>{@link io.rdbc.japi.exceptions.InvalidQueryException} when query is rejected by a database engine as invalid</li>
     * <li>{@link io.rdbc.japi.exceptions.InactiveTxException} when transaction is in progress but is in inactive state</li>
     * <li>{@link io.rdbc.japi.exceptions.ConstraintViolationException} when operation results in an integrity constraint violation</li>
     * </ul>
     */
    RowPublisher stream();

    /**
     * Executes this statement ignoring any resulting information.
     * <p>
     * After the operation takes longer time than {@code timeout}, operation
     * will be aborted. Note however, that it may not be feasible
     * to abort the operation immediately.
     * <p>
     * Returned {@link CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.UnauthorizedException} when client is not authorized to perform the action</li>
     * <li>{@link io.rdbc.japi.exceptions.InvalidQueryException} when query is rejected by a database engine as invalid</li>
     * <li>{@link io.rdbc.japi.exceptions.InactiveTxException} when transaction is in progress but is in inactive state</li>
     * <li>{@link io.rdbc.japi.exceptions.ConstraintViolationException} when operation results in an integrity constraint violation</li>
     * </ul>
     */
    CompletionStage<Void> execute(Duration timeout);

    /**
     * Executes this statement ignoring any resulting information.
     * <p>
     * Returned {@link CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.UnauthorizedException} when client is not authorized to perform the action</li>
     * <li>{@link io.rdbc.japi.exceptions.InvalidQueryException} when query is rejected by a database engine as invalid</li>
     * <li>{@link io.rdbc.japi.exceptions.InactiveTxException} when transaction is in progress but is in inactive state</li>
     * <li>{@link io.rdbc.japi.exceptions.ConstraintViolationException} when operation results in an integrity constraint violation</li>
     * </ul>
     */
    CompletionStage<Void> execute();

    /**
     * Executes this statement and returns a {@link ResultSet} instance.
     * <p>
     * After execution all resulting rows will be pulled from a database
     * and buffered in the resulting object.  If expected result set is
     * very big this may cause out of memory errors.
     * <p>
     * After the operation takes longer time than {@code timeout}, operation
     * will be aborted. Note however, that it may not be feasible
     * to abort the operation immediately.
     * <p>
     * Returned {@link CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.UnauthorizedException} when client is not authorized to perform the action</li>
     * <li>{@link io.rdbc.japi.exceptions.InvalidQueryException} when query is rejected by a database engine as invalid</li>
     * <li>{@link io.rdbc.japi.exceptions.InactiveTxException} when transaction is in progress but is in inactive state</li>
     * <li>{@link io.rdbc.japi.exceptions.ConstraintViolationException} when operation results in an integrity constraint violation</li>
     * </ul>
     */
    CompletionStage<ResultSet> executeForSet(Duration timeout);

    /**
     * Executes this statement and returns a {@link ResultSet} instance.
     * <p>
     * Returned {@link CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.UnauthorizedException} when client is not authorized to perform the action</li>
     * <li>{@link io.rdbc.japi.exceptions.InvalidQueryException} when query is rejected by a database engine as invalid</li>
     * <li>{@link io.rdbc.japi.exceptions.InactiveTxException} when transaction is in progress but is in inactive state</li>
     * <li>{@link io.rdbc.japi.exceptions.ConstraintViolationException} when operation results in an integrity constraint violation</li>
     * </ul>
     */
    CompletionStage<ResultSet> executeForSet();

    /**
     * Executes this statement returning a number of rows that were affected.
     * <p>
     * After the operation takes longer time than {@code timeout}, operation
     * will be aborted. Note however, that it may not be feasible
     * to abort the operation immediately.
     * <p>
     * Returned {@link CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.UnauthorizedException} when client is not authorized to perform the action</li>
     * <li>{@link io.rdbc.japi.exceptions.InvalidQueryException} when query is rejected by a database engine as invalid</li>
     * <li>{@link io.rdbc.japi.exceptions.InactiveTxException} when transaction is in progress but is in inactive state</li>
     * <li>{@link io.rdbc.japi.exceptions.ConstraintViolationException} when operation results in an integrity constraint violation</li>
     * </ul>
     */
    CompletionStage<Long> executeForRowsAffected(Duration timeout);

    /**
     * Executes this statement returning a number of rows that were affected.
     * <p>
     * Returned {@link CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.UnauthorizedException} when client is not authorized to perform the action</li>
     * <li>{@link io.rdbc.japi.exceptions.InvalidQueryException} when query is rejected by a database engine as invalid</li>
     * <li>{@link io.rdbc.japi.exceptions.InactiveTxException} when transaction is in progress but is in inactive state</li>
     * <li>{@link io.rdbc.japi.exceptions.ConstraintViolationException} when operation results in an integrity constraint violation</li>
     * </ul>
     */
    CompletionStage<Long> executeForRowsAffected();

    /**
     * Executes this statement and returns the first row returned by a database engine.
     * <p>
     * If no rows are found, an empty {@link Optional} will be returned.
     * <p>
     * After the operation takes longer time than {@code timeout}, operation
     * will be aborted. Note however, that it may not be feasible
     * to abort the operation immediately.
     * <p>
     * Returned {@link CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.UnauthorizedException} when client is not authorized to perform the action</li>
     * <li>{@link io.rdbc.japi.exceptions.InvalidQueryException} when query is rejected by a database engine as invalid</li>
     * <li>{@link io.rdbc.japi.exceptions.InactiveTxException} when transaction is in progress but is in inactive state</li>
     * <li>{@link io.rdbc.japi.exceptions.ConstraintViolationException} when operation results in an integrity constraint violation</li>
     * </ul>
     */
    CompletionStage<Optional<Row>> executeForFirstRow(Duration timeout);

    /**
     * Executes this statement and returns the first row returned by a database engine.
     * <p>
     * If no rows are found, an empty {@link Optional} will be returned.
     * <p>
     * Returned {@link CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.UnauthorizedException} when client is not authorized to perform the action</li>
     * <li>{@link io.rdbc.japi.exceptions.InvalidQueryException} when query is rejected by a database engine as invalid</li>
     * <li>{@link io.rdbc.japi.exceptions.InactiveTxException} when transaction is in progress but is in inactive state</li>
     * <li>{@link io.rdbc.japi.exceptions.ConstraintViolationException} when operation results in an integrity constraint violation</li>
     * </ul>
     */
    CompletionStage<Optional<Row>> executeForFirstRow();

    /**
     * Executes this statement and returns a single column value from the
     * first row returned by a database engine.
     * <p>
     * If no rows are found, an empty {@link Optional} will be returned.
     * <p>
     * After the operation takes longer time than {@code timeout}, operation
     * will be aborted. Note however, that it may not be feasible
     * to abort the operation immediately.
     * <p>
     * Returned {@link CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.UnauthorizedException} when client is not authorized to perform the action</li>
     * <li>{@link io.rdbc.japi.exceptions.InvalidQueryException} when query is rejected by a database engine as invalid</li>
     * <li>{@link io.rdbc.japi.exceptions.InactiveTxException} when transaction is in progress but is in inactive state</li>
     * <li>{@link io.rdbc.japi.exceptions.ConstraintViolationException} when operation results in an integrity constraint violation</li>
     * </ul>
     *
     * @param valExtractor function used to extract value from the returned row
     */
    <T> CompletionStage<Optional<T>> executeForValue(ThrowingFunction<Row, T> valExtractor,
                                                     Duration timeout);

    /**
     * Executes this statement and returns a single column value from the
     * first row returned by a database engine.
     * <p>
     * If no rows are found, an empty {@link Optional} will be returned.
     * <p>
     * Returned {@link CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.UnauthorizedException} when client is not authorized to perform the action</li>
     * <li>{@link io.rdbc.japi.exceptions.InvalidQueryException} when query is rejected by a database engine as invalid</li>
     * <li>{@link io.rdbc.japi.exceptions.InactiveTxException} when transaction is in progress but is in inactive state</li>
     * <li>{@link io.rdbc.japi.exceptions.ConstraintViolationException} when operation results in an integrity constraint violation</li>
     * </ul>
     *
     * @param valExtractor function used to extract value from the returned row
     */
    <T> CompletionStage<Optional<T>> executeForValue(ThrowingFunction<Row, T> valExtractor);
}
