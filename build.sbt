organization := "com.github.jbrechtel"

name := "robospecs"

version := "0.2-SNAPSHOT"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.pivotallabs" % "robolectric" % "1.0-RC4",
  "org.specs2" %% "specs2" % "1.6.1",
  "org.mockito" % "mockito-core" % "1.9.0-rc1"
)

javaSource in Test <<= (sourceDirectory in Test)(_ / "emptyAndroidProject" / "src" / "java")
