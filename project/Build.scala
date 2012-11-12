import sbt._
import Keys._

object LangpopBuild extends Build {
	lazy val root = Project(id = "langpop",
							base = file(".")) aggregate(langpopWeb, langpopQuery, langpopAggregate)
	
	lazy val langpopQuery = Project(id = "langpop-query",
							base = file("langpop-query"))
	
	lazy val langpopAggregate = Project(id = "langpop-aggregate",
							base = file("langpop-aggregate")) dependsOn(langpopQuery)
	
	lazy val langpopWeb = Project(id = "langpop-web",
							base = file("langpop-web")) dependsOn(langpopAggregate)
}