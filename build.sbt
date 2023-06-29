val akkaVersion = "2.8.0"
val akkaHttpVersion = "10.5.1"

lazy val root = (project in file("."))
  .settings(
    name := "scaliingo",
    organization := "io.github.crotodev",
    organizationName := "crotodev",
    version := "0.1.0",
    scalaVersion := "2.12.17",
    maxErrors := 3,
    startYear := Some(2023),
    idePackagePrefix := Some("io.github.crotodev.tiingo"),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.9.0",
      "com.typesafe" % "config" % "1.4.2",
      "ch.qos.logback" % "logback-classic" % "1.4.7",
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
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
    excludeLintKeys += idePackagePrefix,
    sbtPlugin := true,
    publishMavenStyle := true
  )
  .dependsOn(
    RootProject(
      uri("https://github.com/crotodev/utils-sc.git#45c1522fd2ec9ec86dec807604710641677138ce")
    )
  )
