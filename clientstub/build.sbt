libraryDependencies ++= Seq(
  Dependencies.Library.Play.ws,
  Dependencies.Library.Play.json,
  Dependencies.Library.javaxInject,
  Dependencies.Library.playJsonExtension,
  Dependencies.Library.jodaTime,
  Dependencies.Library.Play.test % "test",
  Dependencies.Library.Specs2.core % "test",
  Dependencies.Library.Specs2.matcherExtra % "test",
  Dependencies.Library.Specs2.mock % "test",
  Dependencies.Library.playWSMock % "test"
)
