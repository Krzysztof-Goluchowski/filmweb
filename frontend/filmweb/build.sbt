import org.scalajs.linker.interface.ModuleSplitStyle

lazy val filmweb = project.in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaVersion := "3.3.3",

    scalaJSUseMainModuleInitializer := true,

    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("filmweb")))
    },

    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.4.0",
    libraryDependencies += "com.raquo" %%% "laminar" % "15.0.1",
    libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "2.1.1",
    libraryDependencies += "com.github.japgolly.scalajs-react" %%% "extra" % "2.1.1",
    libraryDependencies += "io.frontroute" %%% "frontroute" % "0.18.2",
    libraryDependencies += "io.laminext" %%% "core" % "0.16.2",
    libraryDependencies += "io.laminext" %%% "fetch" % "0.16.2",
    libraryDependencies += "org.scala-js" %%% "scala-js-macrotask-executor" % "1.1.1",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "3.3.0",
    libraryDependencies += "org.scala-lang" %% "toolkit" % "0.1.7",
    libraryDependencies += "io.laminext" %%% "fetch-upickle" % "0.16.2"
  )
  