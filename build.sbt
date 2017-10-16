val swaggerCodegen = Project(
  id = "codegen",
  base = file("codegen")
)

val swaggerClientStub = Project(
  id = "clientstub",
  base = file("clientstub")
)

val root = Project(
  id = "swagger-codegen-play-scala",
  base = file("."),
  aggregate = Seq(
    swaggerCodegen,
    swaggerClientStub
  ),
  settings = Defaults.coreDefaultSettings ++
    Seq(
      publishLocal := {},
      publishM2 := {},
      publishArtifact := false
    )
)
