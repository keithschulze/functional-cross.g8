// Typelevel based project

inThisBuild(List(
  organization := "$organization$",
  scalaVersion := "2.13.1",
  crossScalaVersions := Seq("2.11.12", "2.12.10", "2.13.1")
))

// Common deps
lazy val catsVersion        = "2.1.1"
lazy val catsEffectVersion  = "2.1.2"
lazy val scalacheckVersion  = "1.14.3"


lazy val buildSettings = Seq(
  scalacOptions ++= orgScalacOptions(scalaOrganization.value),
  libraryDependencies ++= scalaVersionDeps(scalaVersion.value)
  )

lazy val commonSettings = Seq(
  licenses ++= Seq(
    ("MIT", url("http://opensource.org/licenses/MIT"))
  ),
  scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "utf-8", // Specify character encoding used by source files.
    "-explaintypes", // Explain type errors in more detail.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros", // Allow macro definition (besides implementation and application)
    "-language:higherKinds", // Allow higher-kinded types
    "-language:implicitConversions", // Allow definition of implicit functions called views
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
    "-Xfatal-warnings", // Fail the compilation if there are any warnings.
    "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
    "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
    "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
    "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
    "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
    "-Xlint:option-implicit", // Option.apply used implicit view.
    "-Xlint:package-object-classes", // Class or object defined in package object.
    "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
    "-Ywarn-dead-code", // Warn when dead code is identified.
    "-Ywarn-numeric-widen", // Warn when numerics are widened.
    "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
  ) ++ scalaVersionFlags(scalaVersion.value),
  addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full),
  wartremoverWarnings in (Compile, compile) ++= Warts.unsafe
  )

lazy val commonJSSettings = Seq(
  scalaJSStage in Global := FastOptStage,
  scalaJSUseMainModuleInitializer := true
)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

lazy val $name;format="camel"$Settings = buildSettings ++ commonSettings ++ noPublishSettings

// Sub-projects
lazy val $name;format="camel"$ = project
  .in(file("."))
  .settings(moduleName := "root")
  .settings($name;format="camel"$Settings)
  .aggregate($name;format="camel"$JVM, $name;format="camel"$JS)
  .dependsOn($name;format="camel"$JVM, $name;format="camel"$JS, testsJVM % "test-internal -> test")

lazy val $name;format="camel"$JVM = project
  .in(file(".$name$JVM"))
  .settings(moduleName := "$name$")
  .settings($name;format="camel"$Settings)
  .aggregate(coreJVM, testsJVM, jvm)
  .dependsOn(coreJVM, testsJVM % "test-internal -> test", jvm)

lazy val $name;format="camel"$JS = project
  .in(file(".$name$JS"))
  .settings(moduleName := "$name$")
  .settings($name;format="camel"$Settings)
  .settings(commonJSSettings)
  .aggregate(coreJS, testsJS, js)
  .dependsOn(coreJS, testsJS % "test-internal -> test", js)
  .enablePlugins(ScalaJSPlugin)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(moduleName := "$name$-core", name := "$name$ core")
  .settings($name;format="camel"$Settings)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-core"   % catsVersion,
      "org.typelevel" %%% "cats-effect" % catsEffectVersion
    )
  )
  .jsSettings(commonJSSettings)

lazy val coreJVM = core.jvm
lazy val coreJS = core.js

lazy val tests = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(core)
  .settings(moduleName := "$name$-tests")
  .settings($name;format="camel"$Settings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalacheck"  %%% "scalacheck"  % scalacheckVersion
      )
    )

lazy val testsJVM = tests.jvm
lazy val testsJS = tests.js

// $name$-only is JVM-only
lazy val jvm = project
  .dependsOn(coreJVM, testsJVM % "test-internal -> test")
  .settings(moduleName := "$name$-jvm")
  .settings($name;format="camel"$Settings)

lazy val js = project
  .dependsOn(coreJS, testsJS % "test-internal -> test")
  .settings(moduleName := "$name$-js")
  .settings($name;format="camel"$Settings)
  .enablePlugins(ScalaJSPlugin)

// Helper functions

def scalaVersionFlags(version: String): List[String] =
  CrossVersion.partialVersion(version) match {
    case Some((2, 11)) => List(
      "-Xfuture", // Turn on future language features.
      "-Ywarn-unused-import",
      "-Xlint:unsound-match", // Pattern match may not be typesafe.
      "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
      "-Ypartial-unification", // Enable partial unification in type constructor inference
      "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
      "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
      "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
      "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
      "-Ywarn-nullary-unit" // Warn when nullary methods return Unit.
    )
    case Some((2, 12)) =>
      List(
        "-Xfuture", // Turn on future language features.
        "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
        // "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
        "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
        "-Ywarn-unused:locals", // Warn if a local definition is unused.
        // "-Ywarn-unused:params",              // Warn if a value parameter is unused.
        "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
        "-Ywarn-unused:privates", // Warn if a private member is unused.
        "-Xlint:constant",
        "-Xlint:unsound-match", // Pattern match may not be typesafe.
        "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
        "-Ypartial-unification", // Enable partial unification in type constructor inference
        "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
        "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
        "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
        "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
        "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
        "-Ywarn-numeric-widen", // Warn when numerics are widened.
        "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
    )
    case _ => List(
      "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
      "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
      "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
      "-Ywarn-unused:locals", // Warn if a local definition is unused.
      "-Ywarn-unused:params",              // Warn if a value parameter is unused.
      "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
      "-Ywarn-unused:privates", // Warn if a private member is unused.
      "-Ymacro-annotations",
      "-Xlint:constant",
      "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
      "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
      "-Ywarn-unused:locals", // Warn if a local definition is unused.
      "-Ywarn-unused:params", // Warn if a value parameter is unused.
      "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
      "-Ywarn-unused:privates", // Warn if a private member is unused.
      "-Ywarn-value-discard", // Warn when non-Unit expression results are unused.
      "-Ybackend-parallelism",
      "8", // Enable paralellisation — change to desired number!
      "-Ycache-plugin-class-loader:last-modified", // Enables caching of classloaders for compiler plugins
      "-Ycache-macro-class-loader:last-modified" // and macro definitions. This can lead to performance improvements.
    )
  }

def orgScalacOptions(version: String): List[String] =
  version match {
    case "org.typelevel" => List(
      "-Yinduction-heuristics",       // speeds up the compilation of inductive implicit resolution
      "-Ykind-polymorphism",          // type and method definitions with type parameters of arbitrary kinds
      "-Yliteral-types",              // literals can appear in type position
      "-Xstrict-patmat-analysis",     // more accurate reporting of failures of match exhaustivity
      "-Xlint:strict-unsealed-patmat" // warn on inexhaustive matches against unsealed traits
      )
    case _             => List.empty
  }

def scalaVersionDeps(version: String): List[ModuleID] =
  CrossVersion.partialVersion(version) match {
    case Some((2, 13)) => List.empty
    case _             => List(
      compilerPlugin(
        "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
      )
    )
  }
