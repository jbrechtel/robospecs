organization := "com.github.jbrechtel"

name := "robospecs"

version := "0.2"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
  "com.pivotallabs" % "robolectric" % "1.1",
  "org.specs2" %% "specs2" % "1.9",
  "org.mockito" % "mockito-core" % "1.9.0"
)

javaSource in Test <<= (sourceDirectory in Test)(_ / "emptyAndroidProject" / "src" / "java")

publishTo := Some(Resolver.file("file",  new File( "../jbrechtel.github.com/repo/releases" )) )
