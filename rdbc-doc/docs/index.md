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
    thread so the API fits well into non-blocking application design. rdbc
    allows building applications according to the [Reactive Manifesto](http://www.reactivemanifesto.org/)
    by using [Reactive Streams](http://www.reactive-streams.org/) for asynchronous
    streaming results with a back-pressure.
   
3. **Provide a foundation for higher-level APIs.**

    rdbc is a rather low-level API enabling clients to use plain SQL queries
    and get results back. While it can be used directly it's also meant to 
    provide a foundation for higher-level APIs like functional or object
    relational mapping libraries.
   
## Non-goals

Following list outlines the areas that the API is not meant to cover.

1. **Provide full type-safety.**

    rdbc works on a SQL level, meaning that requests made to the database
    are strings. There is no additional layer that would ensure type-safety
    when working with SQL. The API is also meant to be dynamic and allow type
    converters to be registered at runtime. This approach sacrifices some
    type-safety but at the same time makes it possible to implement wider range
    of higher-level APIs on top of rdbc.

2. **Replace FRM and ORM libraries.**

    TODO description.

## Getting help

    TODO description.

## License

rdbc is an open source software licensed under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
