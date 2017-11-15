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

import io.rdbc.japi.util.ThrowingSupplier;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

/**
 * Represents a database connection (session).
 * <p>
 * Instances of implementations of this interface can be obtained using a
 * {@link ConnectionFactory}. When clients are done with the connection, they are
 * required to call a `release` method co clean up resources such as open sockets.
 * <p>
 * Invoking any method of this interface when any previous operation has not
 * completed yet is not allowed. Operation is considered complete when a resulting
 * {@link CompletionStage} completes.
 * <p>
 * Transaction management has to be done using {@code beginTx}, {@code commitTx} and
 * {@code rollbackTx} methods. Using SQL statements to manage transaction state is
 * not allowed.
 */
public interface Connection {

    /**
     * Begins a database transaction.
     * <p>
     * Using this method is a preferred way of starting a transaction, using SQL
     * statements to manage transaction state may lead to undefined behavior.
     * <p>
     * After the operation takes longer time than {@code timeout}, operation will be
     * aborted. Note however, that it may not be feasible to abort the operation
     * immediately.
     * <p>
     * Returned {@code CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.BeginTxException} when general error occurs</li>
     * <li>{@link io.rdbc.japi.exceptions.TimeoutException} when maximum operation time has been exceeded</li>
     * </ul>
     */
    CompletionStage<Void> beginTx(Duration timeout);

    /**
     * Begins a database transaction.
     * <p>
     * Using this method is a preferred way of starting a transaction, using SQL
     * statements to manage transaction state may lead to undefined behavior.
     * <p>
     * Returned {@code CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.BeginTxException} when general error occurs</li>
     * </ul>
     */
    CompletionStage<Void> beginTx();

    /**
     * Commits a database transaction.
     * <p>
     * Using this method is a preferred way of committing a transaction, using
     * SQL statements to manage transaction state may lead to undefined behavior.
     * <p>
     * After the operation takes longer time than {@code timeout}, operation will be
     * aborted. Note however, that it may not be feasible to abort the operation
     * immediately.
     * <p>
     * Returned {@code CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.CommitTxException} when general error occurs</li>
     * <li>{@link io.rdbc.japi.exceptions.TimeoutException} when maximum operation time has been exceeded</li>
     * </ul>
     */
    CompletionStage<Void> commitTx(Duration timeout);

    /**
     * Commits a database transaction.
     * <p>
     * Using this method is a preferred way of committing a transaction, using
     * SQL statements to manage transaction state may lead to undefined behavior.
     * <p>
     * Returned {@code CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.CommitTxException} when general error occurs</li>
     * </ul>
     */
    CompletionStage<Void> commitTx();

    /**
     * Rolls back a database transaction.
     * <p>
     * Using this method is a preferred way of rolling back a transaction, using
     * SQL statements to manage transaction state may lead to undefined behavior.
     * <p>
     * After the operation takes longer time than {@code timeout}, operation will be
     * aborted. Note however, that it may not be feasible to abort the operation
     * immediately.
     * <p>
     * Returned {@code CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.RollbackTxException} when general error occurs</li>
     * <li>{@link io.rdbc.japi.exceptions.TimeoutException} when maximum operation time has been exceeded</li>
     * </ul>
     */
    CompletionStage<Void> rollbackTx(Duration timeout);

    /**
     * Rolls back a database transaction.
     * <p>
     * Using this method is a preferred way of rolling back a transaction, using
     * SQL statements to manage transaction state may lead to undefined behavior.
     * <p>
     * Returned {@code CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.RollbackTxException} when general error occurs</li>
     * </ul>
     */
    CompletionStage<Void> rollbackTx();

    /**
     * Executes a function in a context of a transaction.
     * <p>
     * Executes a function in a context of a freshly started transaction.
     * After the function finishes, transaction is committed in case of a success
     * and rolled back in case of a failure.
     */
    <T> CompletionStage<T> withTransaction(ThrowingSupplier<CompletionStage<T>> body);

    /**
     * Executes a function in a context of a transaction.
     * <p>
     * Executes a function in a context of a freshly started transaction.
     * After the function finishes, transaction is committed in case of a success
     * and rolled back in case of a failure.
     *
     * @param txManageTimeout Timeout for operations managing transaction state.
     */
    <T> CompletionStage<T> withTransaction(Duration txManageTimeout, ThrowingSupplier<CompletionStage<T>> body);

    /**
     * Releases the connection and underlying resources.
     * <p>
     * Only idle connections can be released using this method. To forcibly
     * release the connection use {@code forceRelease} method.
     * <p>
     * After calling this method no future operations on the instance are allowed.
     * <p>
     * Returned {@code CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.ConnectionReleaseException} when general error occurs</li>
     * </ul>
     */
    CompletionStage<Void> release();

    /**
     * Releases the connection and underlying resources regardless of whether
     * the connection is currently in use or not.
     * <p>
     * After calling this method no future operations on the instance are allowed.
     * <p>
     * Returned {@code CompletionStage} can fail with:
     * <ul>
     * <li>{@link io.rdbc.japi.exceptions.ConnectionReleaseException} when general error occurs</li>
     * </ul>
     */
    CompletionStage<Void> forceRelease();

    /**
     * Checks whether the connection is still usable.
     * <p>
     * If checking takes longer than {@code timeout}, connection is considered unusable.
     *
     * @return Successful CompletionStage of {@code Void} iff connection is usable, CompletionStage failed
     * with ConnectionValidationException otherwise.
     */
    CompletionStage<Void> validate(Duration timeout);

    /**
     * Returns an {@link Statement} instance bound to this connection that
     * represents a SQL statement.
     */
    Statement statement(String sql);

    /**
     * Returns a {@link Statement} instance bound to this connection that
     * represents any SQL statement.
     */
    Statement statement(String sql, StatementOptions statementOptions);

    /**
     * Returns a {@code CompletionStage} that is complete when this connection is idle and ready
     * for accepting queries.
     */
    CompletionStage<Connection> watchForIdle();
}
