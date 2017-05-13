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

# Quickstart
# User guide
## Installation
## API documentation
## Working with a connection
### Connecting to a database
### Releasing a connection
### Connection pooling
## Statements
### Statement types (parametrized vs non parametrized)
### Creating statements
#### String interpolator
#### Bare strings
#### Options
### Type conversions
### Executing statements
#### Executing for a stream
#### Executing for a set
#### Executing for a first row
#### Executing for a value
#### Executing for rows affected
#### Executing for generated key
#### Executing ignoring results
#### Batch updates, inserts and deletes
### Deallocating statements
## Results
### Rows
### Metadata
## Transactions
## Exceptions
## Utilities
## Examples
# Developer guide

TODO:

1. general info on thread safety

2. type conversions, both parameters and results

3. should statement provide access to the metadata (see JDBC)?

