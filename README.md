langpop
======
A tool for measuring programming language popularity on [StackOverflow][1] and [GitHub][2] in real time.

This tool uses StackOverflow's API and GitHub's API to collect information about the usage of a certain programming language. All collected data is exposed by a query-able JSON web service.<br/>
N.B.: Querying GitHub is not yet implemented.

Getting Started
------
**The current version is does not yet work.**
There are some quick-and-dirty code solutions to problems (probably) caused by Scalatra, causing the system to only start if you already have a StackOverflow access token.
Sadly, after the first batch of events is processed, everything crashes, because in the context of a servlet objects are destroyed and not recreated with the proper initialization. I blame Scalatra, and not the lack of time for this project ;)

The tool is written in [Scala][3], and you'll need [SBT][4] to build and run it.<br/>
The SBT project is composed of multiple sub-projects, with `langpop-web` being the main entry point of any code invocations. However, you should **run `sbt` *only* on the main project**, to avoid the creation of bogus `project/` directories in the sub-projects' folders.

Now follow these steps

1.  To run your own instance of this software, **[register an app][5] on [stackapps][6]**.
2.  Do the same for GitHub. *(This is not implemented yet, so you don't need to.)*
3.  **Copy `application.conf.sample` to `application.conf`** in `langpop-web/src/main/resources/` **and edit. ** You need to fill in your stackapps app details in the `langpop.web.auth.stackoverflow` fields.
4.  **Check file permissions:** Make sure SBT can write to the following files:</br>
(This should all work by default.)
    * `stackoverflow.auth.properties` in the `<execution path>/`. (Depends on the file name you set in the `langpop.web.auth.stackoverflow.credentialsFile` configuration option. This file contains the StackOverflow access token, it and should be kept private)
    * `<execution path>/log/` must be writable. If it does not exist, SBT should be able to create it.
    * `tagList.txt` in `langpop-web/src/main/resources/` should be readable by SBT. (Depends on the file name you set in the `langpop.aggregate.tagsfile` configuration option. This file contains all the languages/tags the system will monitor.)
5.  **To start the app/web server** run `sbt container:start`. (Stop it with `sbt container:stop`)
6.  Visit [`localhost:8080/auth`][7] to execute the explicit OAuth2.0 process for StackOverflow (and  GitHub in the future).

After completing all steps and everything works, **you'll only need step 5** to start the server again.

Querying Data
------
Send a HTTP GET request to [`localhost:8080/langpop/1352645381/scala/java`](http://localhost:8080/langpop/1352645381/scala/java) to get a JSON response.

Here `1352645381` is the unix timestamp for which your data is returned.<br/>
`scala/java` states that you want information for two tags/languages, namely `scala` and `java`. You can add more tags as you please, separated by `/`.

The response looks up all information it can find for all the tags/languages you requested. Your response will look something like this:

    {
        timestamp:1352645381,
        github:{
            "scala":10,
            "java":100
        },
        stackoverflow:{
            "scala":12,
            "java":25
        }
    }

Such a response may not include information for all the tags/languages requested. Tags/languages for which no information is found are not included in responses. If for the specified timestamp no information for a requested tag/language is found, the system will return the information for the closest *smaller* timestamp for which information about said tag/language *is* found.

Example: For timestamp `1352645381` the system only knows about the popularity of `java` on StackOverflow.
For `scala`, it looks back in time and finds information from timestamp `1352642854`, which is then returned. For GitHub (once implemented) this works analogously.

Design Notes
------
This project was an assignment of the course [Functional Programming][8] at my MSc program. When I started, I chose Scala as the functional language to learn, and I did not have any experience with it whatsoever. This may explain some implementational details/code you might find odd.

The web framework used is [Scalatra][9].<br/>
Actors are implemented with [Akka][10].

The project was developed with [Scala IDE for Eclipse][11], and Eclipse `.project` files (though purposely included in this repo) were generated using the [sbteclipse][12] plugin.

  [1]: http://stackoverflow.com
  [2]: http://github.com
  [3]: http://www.scala-lang.org
  [4]: http://www.scala-sbt.org
  [5]: http://stackapps.com/apps/oauth/register
  [6]: http://stackapps.com
  [7]: http://localhost:8080/auth
  [8]: http://swerl.tudelft.nl/bin/view/Main/FunctionalProgrammingCourse
  [9]: http://www.scalatra.org
  [10]: http://akka.io
  [11]: http://scala-ide.org
  [12]: https://github.com/typesafehub/sbteclipse
