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
import java.util.concurrent.CompletionStage;

/**
 * Provides access to a database {@link Connection}.
 * <p>
 * Implementors are required to make the implementation of this interface thread-safe.
 */
public interface ConnectionFactory {

    /**
     * Returns a {@code CompletionStage} of a {@link Connection}.
     */
    CompletionStage<Connection> getConnection();

    /**
     * Returns a {@code CompletionStage} of a {@link Connection}.
     */
    CompletionStage<Connection> getConnection(Duration timeout);

    /**
     * Executes a function in a context of a connection.
     * <p>
     * Executes a function in a context of
     * a connection obtained via {@code connection} method, and releases
     * the connection afterwards.
     */
    <T> CompletionStage<T> withConnection(ThrowingFunction<Connection, CompletionStage<T>> body);

    /**
     * Executes a function in a context of a connection.
     * <p>
     * Executes a function in a context of
     * a connection obtained via {@code connection} method, and releases
     * the connection afterwards.
     *
     * @param connGetTimeout Timeout for obtaining new connection.
     */
    <T> CompletionStage<T> withConnection(Duration connGetTimeout, ThrowingFunction<Connection, CompletionStage<T>> body);

    /**
     * Executes a function in a context of a transaction.
     * <p>
     * Executes a function in a context
     * of a connection obtained via {@code connection} method. Before the function is
     * executed, transaction is started. After the function finishes, transaction
     * is committed in case of a success and rolled back in case of a
     * failure - after that, the connection is released.
     */
    <T> CompletionStage<T> withTransaction(ThrowingFunction<Connection, CompletionStage<T>> body);

    /**
     * Executes a function in a context of a transaction.
     * <p>
     * Executes a function in a context
     * of a connection obtained via {@code connection} method. Before the function is
     * executed, transaction is started. After the function finishes, transaction
     * is committed in case of a success and rolled back in case of a
     * failure - after that, the connection is released.
     *
     * @param txManageTimeout Timeout for operations managing transaction state.
     */
    <T> CompletionStage<T> withTransaction(Duration txManageTimeout, ThrowingFunction<Connection, CompletionStage<T>> body);

    /**
     * Shuts down this connection factory.
     * <p>
     * Returned {@code CompletionStage} never fails - it completes on finished shutdown attempt.
     * After the factory is shut down it is illegal to request new connections from it.
     */
    CompletionStage<Void> shutdown();
}
