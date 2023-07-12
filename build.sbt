lazy val root = (project in file("."))
  .settings(
    name := "scaliingo",
    organization := "io.github.crotodev",
    organizationName := "crotodev",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.12.17",
    maxErrors := 3,
    startYear := Some(2023),
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.4.7",
      "org.scalatest" %% "scalatest" % "3.2.15" % Test
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-feature",
      "-unchecked",
      "-Xfatal-warnings",
      "-Xlint:_,-missing-interpolator",
      "-Yno-adapted-args",
      "-Ywarn-unused-import",
      "-Xfuture"
    ),
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case x                   => (assembly / assemblyMergeStrategy).value(x)
    },
    semanticdbEnabled := true,
    onChangedBuildSource := ReloadOnSourceChanges,
    sbtPlugin := true,
    publishMavenStyle := true
  )
