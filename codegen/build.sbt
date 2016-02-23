libraryDependencies ++= Seq(
  Dependencies.Library.swaggerCodegen
)

// Do not append Scala versions to the generated artifacts
crossPaths := false

// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false
