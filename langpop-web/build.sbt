name := "langpop-web"

scalaVersion := "2.9.2"

seq(webSettings :_*)

classpathTypes ~= (_ + "orbit")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
	"org.scalatra" % "scalatra" % "2.1.1",
	"org.scalatra" % "scalatra-scalate" % "2.1.1",
	"com.typesafe" % "config" % "1.0.0",
	"org.apache.httpcomponents" % "httpclient" % "4.2.2",
	"org.scalatra" % "scalatra-specs2" % "2.1.1" % "test",
	"org.scalatra" % "scalatra-scalatest" % "2.1.1" % "test",
	"ch.qos.logback" % "logback-classic" % "1.0.6" % "runtime",
	"org.eclipse.jetty" % "jetty-webapp" % "8.1.7.v20120910" % "container",
	"org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar"))
)