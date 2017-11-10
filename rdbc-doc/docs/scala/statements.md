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

## Statement types

Statements are a core feature of rdbc API. They represent SQL pieces that are sent
to a database for execution along with arguments, if any. There are two types
of statements in rdbc: template statements and executable statements. Template
statements can't be executed right away. To execute them you first need to bind argument
for every parameter they declare. When filled with arguments, template statements
create executable statements. Statements in rdbc are a representation database engine's
[prepared statements](https://en.wikipedia.org/wiki/Prepared_statement).

## Syntax

There isn't much of a syntax to describe. The only thing in a statement syntax
that rdbc adds to SQL is how parameters are defined. There are two kinds of
parameters: named and positional.

### Named parameters

Named parameters:

*    are alphanumeric identifiers,
*    can be defined inside a query by prepending parameter identifier with a colon,
*    can be placed only where database engine expects parameters as defined
     by a database engine's prepared statement syntax,
*    are ignored inside varchar literals, column aliases, comments, etc.,
*    can be referenced by their name or by index (position).

In the following statement just one parameter is declared: `login`:
```sql
select * from users where username = :login
```

A parameter can be used in multiple places. Statement below defines a single
parameter: `name`, but this parameter is used in two places:
```sql
select * from users where first_name = :name or middle_name = :name 
```

The statement below doesn't declare any parameters. "fname" is inside a varchar
literal and "mname" is part of a comment.
```sql
select * from users where first_name = ':fname' -- or middle_name = :mname
```

### Positional parameters

Positional parameters:

*    can be defined inside a query by placing a question mark: `?`,
*    can be placed only where database engine expects parameters as defined
     by a database engine's prepared statement syntax,
*    are ignored inside varchar literals, column aliases, comments, etc.,
*    can be referenced only by their index (position).

The following example statement uses two positional parameters:
```sql
select * from users where first_name = ? or middle_name = ? 
```

The statement below doesn't declare any parameters. The first question mark is 
inside a varchar literal and the second is part of a comment.
```sql
select * from users where first_name = '?' -- or middle_name = ?
```

## Creating statements
### Bare strings

Statement can be created using a bare string and then in a separate step be
filled with arguments for execution. There is a 
[`statement`]({{scaladocRoot}}/io/rdbc/sapi/Connection.html#statement(sql:String):io.rdbc.sapi.Statement)
method defined in `Connection` trait that accepts a string, and returns a
[`Statement`]({{scaladocRoot}}/io/rdbc/sapi/Statement.html)
instance bound to the connection:

```scala
val stmt: Statement = conn.statement(
 "select * from users " +
 "where (first_name = :name or last_name = :name) and age = :age"
)
```

Once you have a `Statement` object, you can bind values to its parameters:

*   **by name**

    To bind values to parameters by name, use `Statement`'s `bind` method that
    accepts a sequence of `(String, Any)` tuples, one for each parameter. Above
    statement's parameters could be bound to values like this:
    `:::scala stmt.bind("name" -> "Casey", "age" -> 30)`.
    
    If you don't provide all parameter values when binding, a
    [`MissingParamValException`]({{scaladocRoot}}/io/rdbc/api/exceptions/MissingParamValException.html)
    will be thrown. If you provide value for a parameter that wasn't declared by
    the query,
    [`NoSuchParamException`]({{scaladocRoot}}/io/rdbc/api/exceptions/NoSuchParamException.html)
    will be thrown.
    
    Binding by name is available only if name parameters were used.

*   **by index**
    
    There is a possibility to bind values to parameters by index - i.e. just
    provide a list of values and these values will be matched to every parameter
    occurrence. Arguments can be bound to the above query like this:
    `:::scala stmt.bind("Casey", "Casey", 30)`.
    
    If you provide too many parameters
    [`TooManyParamsException`]({{scaladocRoot}}/io/rdbc/api/exceptions/TooManyParamsException.html)
    will be thrown.
    If you provide too few parameters
    [`MissingParamValException`]({{scaladocRoot}}/io/rdbc/api/exceptions/MissingParamValException.html)
    will be thrown.
    
    This method of binding is the only one available if you used positional parameters.

If your statement doesn't declare any parameters, use `bind` method without
passing any arguments to it.

### String interpolator

Creating statements using simple strings and binding values to parameters in
a separate step is flexible but this flexibility is not really needed in most
cases. A preferred way of creating statements is by using
[`sql`]({{scaladocRoot}}/io/rdbc/sapi/SqlInterpolatorTrait$Sql.html#sql(args:Any*):io.rdbc.sapi.SqlWithParams)
[string interpolator](http://docs.scala-lang.org/overviews/core/string-interpolation.html).

You can get `sql` interpolator into scope either by importing everything from
`io.rdbc.sapi` package by `:::scala import io.rdbc.sapi._` statement or selectively by
`:::scala import io.rdbc.sapi.SqlInterpolator._` statement.

Once you have it in scope, you can use it to declare parameters and bind values
to them in one step, like this:

```scala
val conn: Connection = ???

def findUsersStmt(name: String): ExecutableStatement = {
   conn.statement(sql"select * from users where name = $name")
}
```

As you can see, when `sql` interpolator is used `Connection`'s `statement` method
produces `ExecutableStatement`, so the statement already has values
bound to its parameters. The above example is equivalent to the following, somewhat
less concise snippet:

```scala
def findUsersStmt(name: String): ExecutableStatement = {   
    conn.statement("select * from users where name = ?")
        .bindByIdx(name)
}
```

!!! tip "SQL injection safety"
    Important thing to understand is that when using `sql` interpolator you're still
    safe from creating SQL injection vulnerability. Even though it may look like
    that, parameter values are **not** passed in to the database as literals
    concatenated with the rest of the SQL.

SQL parts created by `sql` interpolator can be concatenated in the same way you
would concatenate plain strings:

```scala
def findUsersStmt(name: String, age: Int): ExecutableStatement = {
  conn.statement(
   sql"select * from users " +
   sql"where (first_name = $name or last_name = $name) and age = $age"
  )
}
```

#### Dynamic SQL

Sometimes, most notably in tests or some one-time use scripts, it may be useful
to create SQL dynamically, like this:
```scala
def stmt(table: String, name: String): FutureExecutableStatement = {
  conn.statement(s"select * from $table where name = :name")
      .bind("name" -> name)
}
```

If you want to create SQL dynamically and still benefit from `sql` interpolator
features, use `#$` prefix for dynamic parts, like this:

```scala
def sql(table: String, name: String): ExecutableStatement = {
  conn.statement(sql"select * from #$table where name = $name")
}
```

In the above example, only `$name` is a statement parameter, `#$table` will be
simply replaced by `table` method parameter value.

### Options

When creating a statement you can provide options that can tweak statement's
behavior. To pass options, use `Connection`'s `statement` methods that accept
second argument of
[`StatementOptions`]({{scaladocRoot}}/io/rdbc/sapi/StatementOptions$.html) type.

The list below contains currently supported options:

---

*    **Option:** `generatedKeyCols`

     Controls statement behavior regarding returning keys generated by the
     database when issuing update or insert statements.
     
     **Possible values**:
     
     * `KeyColumns.All` &mdash; all columns with generated keys will be returned
     * `KeyColumns.None` &mdash; no columns with generated keys will be returned
     * `KeyColumns.named(cols: String*)` &mdash; only columns listed by name will be returned
     
     **Default value**: `KeyColumns.None`

---

`StatementOptions` is a case class and in its companion object there is `Default`
instance of it with the default option values. You can use this instance to
tweak only some of the options using built-in `copy` method:

```scala
conn.statement(
  sql"insert into users(name) values ($name)",
  Default.copy(option1 = value1, option2 = value2)
)
```

## Executing statements

Once you have an `ExecutableStatement` instance, you can execute it in a couple
of different ways. The method of execution controls in what shape you get the
results from the database. Paragraphs below describe methods of executing statements.

### Executing for a result set

Arguably the simplest method of execution that returns results is to execute
statement for a result set. To do this, use `ExecutableStatement`'s
[`executeForSet`]({{scaladocRoot}}/io/rdbc/sapi/ExecutableStatement.html#executeForSet()(implicittimeout:io.rdbc.sapi.Timeout):scala.concurrent.Future[io.rdbc.sapi.ResultSet])
method that returns `Future` of
[`ResultSet`]({{scaladocRoot}}/io/rdbc/sapi/ResultSet.html).

`ResultSet` gives you access to the rows as well as to the metadata like warnings
issued by the DB engine, columns metadata and count of rows affected by the statement.
It also implements `Traversable` trait providing a convenience method of traversing
through the rows.

Executing for set is simple, but be aware that for bigger sets you may encounter
`OutOfMemoryError`s. All results are stored in memory, there is no paging of any
kind. If you want to avoid this sort of problems, consider
[streaming](statements.md#streaming-results) the results.

For the documentation on how to work with the resulting rows see
[Result Rows](rows.md) chapter.

```scala
def selectUserAge(name: String): Future[ResultSet] = {
  conn.statement(sql"select age from users where name = $name").executeForSet()
}
```
    
### Streaming results

To stream results from the database, use `ExecutableStatement`'s
[`stream`]({{scaladocRoot}}/io/rdbc/sapi/ExecutableStatement.html#stream()(implicittimeout:io.rdbc.sapi.Timeout):io.rdbc.sapi.RowPublisher)
method. This method returns
[`RowPublisher`]({{scaladocRoot}}/io/rdbc/sapi/RowPublisher.html)
instance which implements reactive stream's
[`Publisher`](http://www.reactive-streams.org/reactive-streams-1.0.1-javadoc/)
interface. `stream` method never throws exceptions &mdash; failures are reported by
the returned publisher.

Items published are rows represented by `Row` trait. For the documentation on how
to work with the resulting rows see [Result Rows](rows.md) chapter.

Here are a couple of things to know when working with streams:

*    statement execution is deferred until publisher is subscribed to,
*    a publisher can be subscribed to only once,
*    after `stream` method is invoked, connection is considered busy and can be used
     for other queries only after the stream completes or is cancelled,
*    cancel is an asynchronous operation and Reactive Streams specification doesn't
     provide a way of notifying a client that cancel operation completed. If clients
     want to use the connection after stream cancellation, they must watch for
     publisher's `done` `Future` completion before requesting any
     subsequent operations.

`RowPublisher` instance, through its members, gives access to number
of affected rows, warnings returned by the database and row metadata.

Processing streams is out of scope of this manual, for details please refer to
documentation of Reactive Streams compatible libraries that are built to
facilitate this, like
[Akka stream](http://doc.akka.io/docs/akka/current/scala/stream/index.html) or
[Monix](https://monix.io/).

Examples (which release the connection on stream termination):

```scala
import akka.stream.scaladsl.{Source, Sink}
import akka.NotUsed

val source: Source[Row, NotUsed] = {
  Source.fromPublisher(
    conn.statement(sql"select name from users").stream()
  ).alsoTo(Sink.onComplete(_ => conn.release()))
}
```

```scala
import monix.reactive.Observable
import monix.eval.Task

val obs: Observable[Row] = {
  Observable.fromReactivePublisher(
    conn.statement(sql"select name from users").stream()
  ).doOnTerminateEval(_ => Task.fromFuture(conn.release()))
}
```

### Executing ignoring results

In many cases clients are not interested in any result of statement execution
other than simple "success" or "failure" information. This is often the case
for `insert`, `update` and `delete` commands. This use case is covered by
`ExecutableStatement`'s `execute` method which returns `Future` of `Unit`.

Example:

```scala
def insertUser(name: String): Future[Unit] = {
  conn.statement(sql"insert into users(name) values ($name)").execute()
}
```

### Executing for a single row and for a value

It is a common use case to expect just a single row to be returned by a query.
For instance, when querying by a primary key. This can be easily achieved by
using `ExecutableStatement`'s
[`executeForFirstRow`]({{scaladocRoot}}/io/rdbc/sapi/ExecutableStatement.html#executeForFirstRow()(implicittimeout:io.rdbc.sapi.Timeout):scala.concurrent.Future[Option[io.rdbc.sapi.Row]])
method which returns a `Future` of `Option[Row]`. Returned `Option` is `None`
in case when query doesn't return any results, otherwise, the first row is
returned as a `Some`.

Example:

```scala
def findUser(login: String): Future[Option[Row]] = {
  conn.statement(sql"select * from users where login = $login")
      .executeForFirstRow()
}
```

Sometimes, not even a single row is needed by a client, only a single column
value, like user's name when searching by login.
[`executeForValue`]({{scaladocRoot}}/io/rdbc/sapi/ExecutableStatement.html#executeForValue[A](valExtractor:io.rdbc.sapi.Row=>A)(implicittimeout:io.rdbc.sapi.Timeout):scala.concurrent.Future[Option[A]])
method comes in handy in these kind of situations. The method accepts  a function
that is supposed to extract this single value from a returned row, if any.

See the example below:

```scala
def findUsersName(login: String): Future[Option[String]] = {
  conn.statement(sql"select name from users where login = $login")
      .executeForValue(r => r.str("name"))
}
```

For the documentation on how to work with the resulting rows see
[Result Rows](rows) chapter.

### Executing for rows affected

When executing insert, update or delete statements it may be good to know
how many rows were affected by the execution. A number of affected rows can be
obtained when executing for set or by streaming but if it's the only information
that is needed use `ExecutableStatement`'s 
[`executeForRowsAffected`]({{scaladocRoot}}/io/rdbc/sapi/ExecutableStatement.html#executeForRowsAffected()(implicittimeout:io.rdbc.sapi.Timeout):scala.concurrent.Future[Long])
method which returns a `Future` of `Long`.

See the example below:

```scala
def updateNames(name: String, age: Int): Future[Long] = {
  conn.statement(sql"update users set name = $name where age = $age")
      .executeForRowsAffected()
}
```

### Executing for generated key

If you rely on primary keys being generated by the database when inserting
new records you'll need just this one key as a result of the execution. If you
need to get multiple generated values then use `executeForStream`, `executeForSet`
or `executeForFirstRow` described above but for a single one, there is
[`executeForKey`]({{scaladocRoot}}/io/rdbc/sapi/ExecutableStatement.html#executeForKey[K]()(implicitevidence$1:scala.reflect.ClassTag[K],implicittimeout:io.rdbc.sapi.Timeout):scala.concurrent.Future[K])
method. This method executes a statement and returns the first column of the first
returned row (in most cases the result is going to be a single row with a single
column anyway). The method is parametrized by a type of the key.

A method in the example below inserts a new user and returns a generated UUID key.

```scala
def insertUser(name: String, age: Int): Future[UUID] = {
  conn.statement(
        sql"insert into users(name, age) values($name, $age)",
        StatementOptions.ReturnGenKeys
  ).executeForKey[UUID]()
}
```

### Streaming statement arguments

rdbc provides an efficient way to execute statement repeatedly with many
sets of arguments. `Statement` can subscribe to a stream of argument sets by
invoking `streamArgs` or `streamArgsByIdx` methods which accept Reactive Streams'
`Publisher`. rdbc driver will back pressure the stream as needed.

Streaming arguments can be used for statements that don't return any values.
Technically it is possible to stream arguments of SQL `select`s but there are no
means for the client to get the data back from the database.

#### Positional and named parameters

Each stream element contains entire set of arguments that a given statement
expects. Clients have an alternative of using named and positional parameters:
`streamArgs` accepts a stream producing `:::scala Map[String, Any]` elements
(named parameters) and `streamArgsByIdx` indexed sequences of arguments
(positional parameters).

#### Handling failures

If any error occurs in the middle of stream processing, the process will be aborted
and `Future` returned by the streaming method will fail too.

#### Examples

Creating `Publisher` instances is out of scope of this manual, for details please
refer to documentation of Reactive Streams compatible libraries that are built to
facilitate this, like
[Akka stream](http://doc.akka.io/docs/akka/current/scala/stream/index.html) or
[Monix](https://monix.io/). Examples below use simple streams backed by in
memory collections.


Streaming named arguments with Akka:

```scala
import io.rdbc.sapi._
import akka.stream.scaladsl.Source

val res: Future[Unit] = conn.withTransaction {
  val data: Vector[Map[String, Any]] = Vector(
    Map("name" -> "Robin", "age" -> 10),
    Map("name" -> "Alex", "age" -> 32),
    Map("name" -> "Casey", "age" -> 12)
  )
  val publisher = Source(data).runWith(Sink.asPublisher(fanout = false))
  val stmt = conn.statement("insert into users(name, age) values (:name, :age)")
  stmt.streamArgs(publisher)
}
```

Streaming positional arguments with Akka:

```scala
import io.rdbc.sapi._
import akka.stream.scaladsl.Source

val res: Future[Unit] = conn.withTransaction {
  val data: Vector[Vector[Any]] = Vector(
    Vector("Robin", 10),
    Vector("Alex", 32),
    Vector("Casey", 12)
  )
  val publisher = Source(data).runWith(Sink.asPublisher(fanout = false))
  val stmt = conn.statement("insert into users(name, age) values (?, ?)")
  stmt.streamArgsByIdx(publisher)
}
```
