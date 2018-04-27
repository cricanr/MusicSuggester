# Install Scala & SBT

1. Install Java JDK (Oracle 1.8) & SBT https://www.scala-sbt.org/1.0/docs/Installing-sbt-on-Linux.html or https://www.scala-lang.org/download/
    - make sure it works JDK: $ javac
    - make sbt works: $ sbt
2. IntelliJ IDE download and install: https://www.jetbrains.com/idea (community is free, ultimate is nicer but needs license)
    - Install Scala plugin for IntelliJ
3. Import project using SBT (build.sbt) is the solutions file for Scala
    - wait until it downloads the Internet :) patience is needed :)
4. Look around try make sense of it :) 

# Useful commands for sbt to build project
- Run these in root project folder
```
$ sbt clean
$ sbt compile 
$ sbt test
$ sbt run
```
- You can also combine like:
`$ sbt clean compile test`

# Used frameworks so far: 
1. Play framework (MVC framework for Scala): https://www.playframework.com/documentation/2.6.x/Home
2. Spotify Java Client: https://github.com/thelinmichael/spotify-web-api-java

NOTE: All dependencies are added and project structure is configured in build.sbt

# Useful reading:
1. Get familiar with Scala: https://docs.scala-lang.org/tutorials/tour/tour-of-scala.html.html
2. Get accustomed with Play: https://www.playframework.com/documentation/2.6.x/Home
2. Spotify Java Client: https://github.com/thelinmichael/spotify-web-api-java & https://www.playframework.com/documentation/2.6.x/Tutorials