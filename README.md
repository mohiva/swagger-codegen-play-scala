# Swagger Codegen - Play Scala

A code generator for [Swagger] which generates API clients with the help of the PlayWS library. This code generator is based on the `akka-scala` generator included in the [Swagger Codegen] distribution.

## Create client

Download swagger codegen executable into the root directory of this project:

```
wget http://repo1.maven.org/maven2/io/swagger/swagger-codegen-cli/2.1.5/swagger-codegen-cli-2.1.5.jar -O swagger-codegen-cli.jar
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
----------------------------------------------------------------------------------
modelPackage        | The package were the generated `model` classes should be located
apiPackage          | The package were the generated `api` classes should be located
invokerPackage      | The package were the generated `invoker` classes should be located
configKeyPath       | Path under which the config must be defined 
projectOrganization | Project organization in generated build.sbt
projectName         | Project name in generated build.sbt
projectVersion      | Project version in generated build.sbt
scalaVersion        | The Scala version to use in generated build.sbt
playVersion         | The Play version to use in generated build.sbt



[Swagger]: http://swagger.io/
[Swagger Codegen]: https://github.com/swagger-api/swagger-codegen