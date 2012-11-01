name := "langpop-aggregate"

scalaVersion := "2.9.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
	"com.typesafe.akka" % "akka-actor" % "2.0.3",
	"org.scalatest" %% "scalatest" % "1.8" % "test",
	"com.typesafe.akka" % "akka-testkit" % "2.0.3" % "test",
	"ch.qos.logback" % "logback-classic" % "1.0.7"
)
