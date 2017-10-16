libraryDependencies ++= Seq(
  Dependencies.Library.Play.ws,
  Dependencies.Library.Play.json,
  Dependencies.Library.javaxInject,
  Dependencies.Library.playJsonExtension,
  Dependencies.Library.Play.test % "test",
  Dependencies.Library.Play.specs2 % "test",
  Dependencies.Library.Play.Specs2.matcherExtra % "test",
  Dependencies.Library.Play.Specs2.mock % "test",
  Dependencies.Library.playWSMock % "test"
)
