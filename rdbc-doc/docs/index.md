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
!!! warning
    rdbc project and this documentation is still a work in progress.
    It's not ready yet for production use.
    
## What is rdbc?

rdbc is a SQL-level relational database connectivity API targeting Scala and 
Java programming languages. The API is fully asynchronous and provides
a possibility to leverage [Reactive Streams'](http://www.reactive-streams.org/)
stream processing capabilities.

## Goals

Following list outlines the goals of the API:

1. **Provide vendor neutral access to most commonly used database features.**

    The API is meant to be vendor neutral in a sense that if clients stick
    to using only standard SQL features no vendor-specific code should be needed
    and database backends can be switched with no client code changes.

2. **Be asynchronous and reactive.**

    All methods that can potentially perform I/O actions don't block the executing
    thread so the API fits well into a non-blocking application design. rdbc
    allows building applications according to the [Reactive Manifesto](http://www.reactivemanifesto.org/)
    by using [Reactive Streams](http://www.reactive-streams.org/) for asynchronous
    results streaming with a back-pressure.
   
3. **Provide a foundation for higher-level APIs.**

    rdbc is a rather low-level API enabling clients to use plain SQL queries
    and get results back. While it can be used directly it's also meant to 
    provide a foundation for higher-level APIs like functional or object
    relational mapping libraries.
   
## Non-goals

Following list outlines the areas that the API is not meant to cover.

1. **Provide a full type-safety.**

    rdbc works on a SQL level, meaning that requests made to the database
    are strings. There is no additional layer that would ensure type-safety
    when working with SQL. The API is also meant to be dynamic and allow type
    converters to be registered at runtime. This approach sacrifices some
    type-safety but at the same time makes it possible to implement wider range
    of higher-level APIs on top of rdbc.

## Getting help

Join [rdbc-io/rdbc](https://gitter.im/rdbc-io/rdbc) gitter channel for 
questions and any kind of discussion about rdbc. You are also free to
ask your question by creating a Github [issue](https://github.com/rdbc-io/rdbc/issues/new).

See also [rdbc](https://stackoverflow.com/questions/tagged/rdbc)
tag on StackOverflow.

## License

rdbc is an open source software licensed under
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
