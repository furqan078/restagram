name := "restagram"

version := "1.0-SNAPSHOT"

libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)

play.Project.playJavaSettings
