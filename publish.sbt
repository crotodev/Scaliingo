ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/crotodev/Scaliingo"),
    "https://github.com/crotodev/Scaliingo.git"
  )
)

ThisBuild / developers := List(
  Developer("crotodev", "Christian Rotondo", "", url("https://github.com/crotodev"))
)

ThisBuild / publishTo := Some(
  if (isSnapshot.value)
    "snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"
  else
    "releases" at "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2"
)

ThisBuild / publishMavenStyle := true
ThisBuild / versionScheme := Some("early-semver")
licenses += (("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.txt")))

