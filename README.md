# Metaschema Java Tools and Libraries

This open-source, Metaschema Java framework offers a set of Java libraries providing a programmatic means to work with models defined by the [Metaschema modeling language](https://github.com/metaschema-framework/metaschema). This framework also supports programmatically creating, modifying, parsing, and writing XML, JSON, and YAML instance data that conforms to a given Metaschema model. This work is intended to make it easier for Java software developers to incorporate Metaschema-based capabilities into their applications.

The Metaschema Java framework provides:

- Java objects for loading and working with XML-based Metaschema constructs. This functionality is provided by the [Metaschema XML model](core/).
- Java bean code generation based on one or more Metaschema using Maven. This functionality is provided by the [Metaschema Maven plugin](metaschema-maven-plugin/).
- A Java parser for reading and writing XML, JSON, or YAML into Java beans generated by this framework. This functionality is provided by the [Metaschema Java Binding Parser](databind/).
- XML and JSON schema generation based on a Metaschema provided by [Netaschema Schema Generator](schemagen/).

## Public domain

See the [project license](LICENSE.md) in this repository.

This project is in the worldwide [public domain](LICENSE.md) and as stated in [CONTRIBUTING.md](CONTRIBUTING.md).

## Contributing to this code base

Thank you for interest in contributing to the Metaschema Java framework. For complete instructions on how to contribute code, please read through our [CONTRIBUTING.md](CONTRIBUTING.md) documentation.

## Using as a Maven dependency

This project's modules are published to [Maven Central](https://search.maven.org/search?q=g:dev.metaschema).

You can include these artifacts in your Maven POM as a dependency.

## Building

This project can be built with [Apache Maven](https://maven.apache.org/) version 3.9.0 or greater.

The following instructions can be used to clone and build this project.

1. Clone the GitHub repository.

```bash
git clone --recurse-submodules https://github.com/metaschema-framework/metaschema-java.git 
```

2. Build the project with Maven

```bash
mvn install
```

## Relationship to prior work

The contents of this repository is based on work from the [Metaschema Java repository](https://github.com/usnistgov/metaschema-java/) maintained by the National Institute of Standards and Technology (NIST), the [contents of which have been dedicated in the worldwide public domain](https://github.com/usnistgov/metaschema-java/blob/1a496e4bcf905add6b00a77a762ed3cc31bf77e6/LICENSE.md) using the [CC0 1.0 Universal](https://creativecommons.org/publicdomain/zero/1.0/) public domain dedication. This repository builds on this prior work, maintaining the [CCO license](https://github.com/metaschema-framework/metaschema-java/blob/main/LICENSE.md) on any new works in this repository.
