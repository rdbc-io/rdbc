<!---
 ! Copyright 2016-2017 rdbc contributors
 !
 ! Licensed under the Apache License, Version 2.0 (the "License");
 ! you may not use this file except in compliance with the License.
 ! You may obtain a copy of the License at
 !
 !     http://www.apache.org/licenses/LICENSE-2.0
 !
 ! Unless required by applicable law or agreed to in writing, software
 ! distributed under the License is distributed on an "AS IS" BASIS,
 ! WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ! See the License for the specific language governing permissions and
 ! limitations under the License. 
 -->

## Connecting to a database

To connect to a database you first need to get a hold on [`ConnectionFactory`]({{scaladocRoot}}/io/rdbc/sapi/ConnectionFactory.html)
implementation provided by a rdbc driver. Consult the driver's documentation on how to
instantiate and configure `ConnectionFactory` it provides. 

To connect to a database means to obtain [`Connection`]({{scaladocRoot}}/io/rdbc/sapi/Connection.html) instance. Once you have
the factory, you have the following options on how to get the connection:

### Manually opening and releasing a connection

Connection factory provides 
[`connection`]({{scaladocRoot}}/io/rdbc/sapi/ConnectionFactory.html#connection()(implicittimeout:io.rdbc.sapi.Timeout):scala.concurrent.Future[io.rdbc.sapi.Connection])
method that simply returns a `Future` holding a `Connection`. A connection
obtained this way must be then released manually using 
[`release`]({{scaladocRoot}}/io/rdbc/sapi/Connection.html#release():scala.concurrent.Future[Unit])
method. Here's the example usage:

```scala
import io.rdbc.sapi._
import io.rdbc.util.Futures._

val cf: ConnectionFactory = ???

val result = cf.connect().flatMap { conn =>
   /* use the connection */
   ???
}.andThenF { case _ =>
   conn.release()
}
```
[`andThenF`]({{scaladocRoot}}io/rdbc/util/Futures$$FutureOps.html#andThenF[U](pf:PartialFunction[scala.util.Try[T],scala.concurrent.Future[U]])(implicitec:scala.concurrent.ExecutionContext):scala.concurrent.Future[T])
future combinator provided by
[rdbc utilities](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.rdbc%22%20util)
package is like a standard `andThen` but partial function passed to it returns
a `Future` and the chain can proceed only when this future completes. Relation between
`andThenF` and `andThen` is analogous to the relation between `flatMap` and `map`.

### Using the loan pattern

Even if you haven't heard about the loan pattern you probably know what it is.
Connection factory provides
[`withConnection`]({{scaladocRoot}}/io/rdbc/sapi/ConnectionFactory.html#withConnection[A](body:io.rdbc.sapi.Connection=>scala.concurrent.Future[A])(implicittimeout:io.rdbc.sapi.Timeout):scala.concurrent.Future[A])
method that executes a block of code in the context of a database connection and
releases the connection afterwards. It looks like this:

```scala
import io.rdbc.sapi._

val cf: ConnectionFactory = ???

val result = cf.withConnection { conn =>
   /* use the connection */
   ???
}
```

The above example is equivalent to the one from the
[previous paragraph](connection.md#manually-opening-and-releasing-a-connection).

## Transaction management

In rdbc, `Connection` provides facilities to manage database transactions. Using SQL
to manage transaction state by issuing commands like `BEGIN` or `ROLLBACK` is
not allowed. If you don't manage transaction at all, every SQL statement will
be executed in its own transaction. Following sections describe ways of managing
transactions.

### Manual

Managing transaction manually means using the three methods provided by `Connection`
trait:
[`beginTx`]({{scaladocRoot}}/io/rdbc/sapi/Connection.html#beginTx()(implicittimeout:io.rdbc.sapi.Timeout):scala.concurrent.Future[Unit]),
[`commitTx`]({{scaladocRoot}}/io/rdbc/sapi/Connection.html#commitTx()(implicittimeout:io.rdbc.sapi.Timeout):scala.concurrent.Future[Unit])
and [`rollbackTx`]({{scaladocRoot}}/io/rdbc/sapi/Connection.html#rollbackTx()(implicittimeout:io.rdbc.sapi.Timeout):scala.concurrent.Future[Unit]). 
Each of these methods return a `Future` of `Unit` &mdash; there is no dedicated 
trait or class representing a transaction. Similar to manual handling of connecting
and disconnecting from the database, manual transaction management is kind of
cumbersome. When you don't need the flexibility provided by the three methods,
use the transactional loan pattern described below.

### Using the transactional loan pattern

`Connection` trait provides
[`withTransaction`]({{scaladocRoot}}/io/rdbc/sapi/Connection.html#withTransaction[A](body:=>scala.concurrent.Future[A])(implicittimeout:io.rdbc.sapi.Timeout):scala.concurrent.Future[A])
method which allows to execute a block of code in a context of a newly started
transaction. If a `Future` returned by the block of code is successful, the
transaction will be committed &mdash; if it fails, the transaction will be rolled
back:

```scala
import io.rdbc.sapi._

val cf: ConnectionFactory = ???

val result = cf.withConnection { conn =>
   conn.withTransaction {
    /* use the connection in a context of the transaction */
    ???
   }
}
```

If any error occurs when rolling back the transaction, this error will be reported
to the execution context and the original error causing the rollback will be returned.

It's a common use case to connect to the database and execute just a single
transaction. This use case is covered by 
[`withTransaction`]({{scaladocRoot}}/io/rdbc/sapi/ConnectionFactory.html#withTransaction[A](body:io.rdbc.sapi.Connection=>scala.concurrent.Future[A])(implicittimeout:io.rdbc.sapi.Timeout):scala.concurrent.Future[A])
method provided by a `ConnectionFactory`. Above snippet could be simplified as follows:

```scala
import io.rdbc.sapi._

val cf: ConnectionFactory = ???

val result = cf.withTransaction { conn =>
    /* use the connection in a context of the transaction */
    ???
}
```

## Validation

Sometimes, it may be useful to verify whether already open connection is "valid",
i.e. whether it can still execute queries. A connection may get broken because of
a number of reasons including network-related problems. To check whether the connection
is usable, use
[`validate`]({{scaladocRoot}}/io/rdbc/sapi/Connection.html#validate()(implicittimeout:io.rdbc.sapi.Timeout):scala.concurrent.Future[Unit])
method.

Connection can also be validated by executing any query but using `validate` is
a preferred way since it can leverage a vendor-specific way of validating the
connection with a minimal overhead.

## Concurrent operations

As every other rdbc class, `Connection` is thread-safe. However, it doesn't mean
that multiple threads can execute queries using the same connection at the same
time. Only one operation can be executed at any given time. When some thread tries
to use a non-idle connection, resulting `Future` fails with
[`IllegalSessionStateException`]({{scaladocRoot}}/io/rdbc/api/exceptions/IllegalSessionStateException.html).
Exception to this rule is 
[`forceRelease`]({{scaladocRoot}}/io/rdbc/sapi/Connection.html#forceRelease():scala.concurrent.Future[Unit])
method which can be used regardless of whether connection is idle or not.

Example below demonstrates an invalid code which can fail with `IllegalSessionStateException`:
```
#!scala hl_lines="7"
import io.rdbc.sapi._

val cf: ConnectionFactory = ???

cf.withConnection { conn =>
    conn.validate().foreach(valid => println(s"1. valid = $valid"))
    conn.validate().foreach(valid => println(s"2. valid = $valid"))
}
```

`Future` returned by statement at line `7` may fail because connection may still
be busy executing a request from line `6`. You can be sure though that the connection
will start executing the first request (line `6`) before the second (line `7`)
&mdash; there is a happens-before relation there.

To make the above code safe, it could be rewritten like this:

```scala
import io.rdbc.sapi._

val cf: ConnectionFactory = ???

cf.withConnection { conn =>
    conn.validate().foreach(valid => println(s"1. valid = $valid"))
    .andThen { _ =>
       conn.validate().foreach(valid => println(s"2. valid = $valid"))
    }
}
```

`Connection` also provides
[`watchForIdle`]({{scaladocRoot}}/io/rdbc/sapi/Connection.html#watchForIdle:scala.concurrent.Future[Unit])
method returning future which completes when connection becomes idle. Snippet below
is safe from failing with `IllegalSessionStateException`.

```scala
import io.rdbc.sapi._

val cf: ConnectionFactory = ???

cf.withConnection { conn =>
    conn.validate().foreach(valid => println(s"1. valid = $valid"))
    conn.watchForIdle.andThen { _ =>
        conn.validate().foreach(valid => println(s"2. valid = $valid"))
    }
}
```

## Connection pooling

Estabilishing a new connection every time an interaction with the database is needed
is expensive performance-wise. There usually is an overhead of initializing
new session. In order to avoid this problem you can use connection pooling.

Connection pooling mechanism isn't really part of the API &mdash; to use pooling
you just need to use a special `ConnectionFactory` implementation.

Have a look at [rdbc-pool](https://github.com/rdbc-io/rdbc-pool) project that
provides a `ConnectionFactory` implementation capable of connection pooling.
