<!---
 ! Copyright 2016-2017 Krzysztof Pado
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
!!! warning
    rdbc project and this documentation is still a work in progress.
    It's not ready yet for production use.

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
filled with arguments for execution. There is a [`statement`]() method defined in `Connection`
trait that accepts a string, and returns a [`Statement`]() instance bound to the
connection:

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
    
    If you don't provide all parameter values when binding, a `MissingParamValException`
    will be thrown. If you provide value for a parameter that wasn't declared by
    the query, [`NoSuchParamException`]() will be thrown.
    
    Binding by name is available only if name parameters were used.

*   **by index**
    
    There is a possibility to bind values to parameters by index - i.e. just
    provide a list of values and these values will be matched to every parameter
    occurrence. Arguments can be bound to the above query like this:
    `:::scala stmt.bind("Casey", "Casey", 30)`.
    
    If you provide too many parameters [`TooManyParamsException`]() will be thrown.
    If you provide too few parameters [`MissingParamValException`]() will be thrown.
    
    This method of binding is the only one available if you used positional parameters.

**TODO describe what to do with statements without parameters.**

### String interpolator

Creating statements using simple strings and binding values to parameters in
a separate step is flexible but this flexibility is not really needed in most
cases. A preferred way of creating statements is by using [`sql`]()
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

Important thing to understand is that when using `sql` interpolator you're still
safe from creating SQL injection vulnerability. Parameter values are **not** passed
in to the database as literals concatenated with the rest of the SQL.

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
second argument of [`StatementOptions`]() type.

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
[`executeForSet`]() method that returns `Future` of [`ResultSet`]().

`ResultSet` gives you access to the rows as well as to the metadata like warnings
issued by the DB engine, columns metadata and count of rows affected by the statement.
It also implements `Traversable` trait providing a convenience method of traversing
through the rows.

Executing for set is simple, but be aware that for bigger sets you may encounter
`OutOfMemoryError`s. All results are stored in memory, there is no paging of any
kind. If you want to avoid these kind of problems, consider
[streaming](statements/#executing-for-a-stream) the results.

For the documentation on how to work with the resulting rows see
[Result Rows](rows) chapter.
    
### Executing for a stream
### Executing ignoring results
### Executing for a first row
### Executing for a value
### Executing for rows affected
### Executing for generated key

### Batch updates, inserts and deletes
