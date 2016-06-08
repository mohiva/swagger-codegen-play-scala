# Swagger Codegen - Play Scala [![Build Status](https://travis-ci.org/mohiva/swagger-codegen-play-scala.png)](https://travis-ci.org/mohiva/swagger-codegen-play-scala) [![Coverage Status](https://coveralls.io/repos/mohiva/swagger-codegen-play-scala/badge.svg?branch=master&service=github)](https://coveralls.io/github/mohiva/swagger-codegen-play-scala?branch=master) [![Join the chat at https://gitter.im/mohiva/swagger-codegen-play-scala](https://badges.gitter.im/mohiva/swagger-codegen-play-scala.svg)](https://gitter.im/mohiva/swagger-codegen-play-scala?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
=========

A code generator for [Swagger] which generates API clients with the help of the PlayWS library. This code generator is based on the `akka-scala` generator included in the [Swagger Codegen] distribution.

## Versions

Project             | Play
--------------------|------------------
0.1.x               | 2.4
master              | 2.5

## Create client

Download swagger codegen executable into the root directory of this project:

```
wget http://repo1.maven.org/maven2/io/swagger/swagger-codegen-cli/2.1.6/swagger-codegen-cli-2.1.6.jar -O swagger-codegen-cli.jar
```

Execute the script
```
scripts/codegen -i ${SWAGGER_SPEC_FILE} -o ${SWAGGER_CLIENT_DIR} -c ${SWAGGER_CONF_FILE}
```

To run the bundled `petstore` example:
```
scripts/codegen -i conf/petstore.yaml -o build -c conf/config.json
```

Compile the client:
```
cd build
sbt compile
```

## Config options:

The code generator supports the following config options:

Name                | Description
--------------------|-------------------------------------------------------------
modelPackage        | The package for generated `model` classes
apiPackage          | The package for generated `api` classes
invokerPackage      | The package for generated `root` classes
configKeyPath       | Path under which the config must be defined 
projectOrganization | Project organization in generated build.sbt
projectName         | Project name in generated build.sbt
projectVersion      | Project version in generated build.sbt
scalaVersion        | The Scala version to use in generated build.sbt
playVersion         | The Play version to use in generated build.sbt



[Swagger]: http://swagger.io/
[Swagger Codegen]: https://github.com/swagger-api/swagger-codegen
