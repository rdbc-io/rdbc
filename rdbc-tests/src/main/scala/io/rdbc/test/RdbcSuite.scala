package io.rdbc.test

trait RdbcSuite
  extends NonExistingTableSpec
    with NonExistingColumnSpec
    with MissingParamSpec
    with TooManyParamsSpec
    with NoSuchParamSpec
    with SyntaxErrorSpec
    with TimeoutSpec
