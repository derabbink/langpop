import sbt._
import Keys._

object LangpopBuild extends Build {
	lazy val root = Project(id = "langpop",
							base = file(".")) aggregate(langpopWeb, langpopQuery, langpopQueryGithub, langpopQueryStackoverflow)
	
	lazy val langpopQuery = Project(id = "langpop-query",
							base = file("langpop-query"))
	
	lazy val langpopQueryGithub = Project(id = "langpop-query-github",
							base = file("langpop-query-github")) dependsOn(langpopQuery)
	
	lazy val langpopQueryStackoverflow = Project(id = "langpop-query-stackoverflow",
							base = file("langpop-query-stackoverflow")) dependsOn(langpopQuery)
	
	lazy val langpopWeb = Project(id = "langpop-web",
							base = file("langpop-web")) dependsOn(langpopQuery, langpopQueryGithub, langpopQueryStackoverflow)
}