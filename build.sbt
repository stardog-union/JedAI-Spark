organization := "com.stardog"
name := "spark_er"
version := "1.0"
scalaVersion := "2.12.4"
val sparkVersion = "3.3.1"
val jacksonVersion = "2.14.0"


unmanagedBase := baseDirectory.value / "custom_lib"


libraryDependencies += "org.apache.spark" % "spark-core_2.12" % sparkVersion

libraryDependencies += "org.apache.spark" % "spark-sql_2.12" % sparkVersion

libraryDependencies += "org.apache.spark" % "spark-graphx_2.12" % sparkVersion

libraryDependencies += "org.apache.spark" % "spark-mllib_2.12" % sparkVersion

libraryDependencies += "org.apache.spark" % "spark-hive_2.12" % sparkVersion


// https://mvnrepository.com/artifact/org.apache.commons/commons-math3
libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1"

// https://mvnrepository.com/artifact/commons-codec/commons-codec
libraryDependencies += "commons-codec" % "commons-codec" % "1.11"

// https://mvnrepository.com/artifact/org.jgrapht/jgrapht-core
libraryDependencies += "org.jgrapht" % "jgrapht-core" % "0.9.0"//% "1.0.1"

// https://mvnrepository.com/artifact/org.json/json
libraryDependencies += "org.json" % "json" % "20170516"

libraryDependencies += "com.holdenkarau" %% "spark-testing-base" % "3.3.1_1.3.0" % Test

// https://mvnrepository.com/artifact/org.apache.livy/livy-core
// libraryDependencies += "org.apache.livy" %% "livy-core" % "0.7.1-incubating" exclude("com.esotericsoftware.kryo", "kryo")

Test / parallelExecution := false
//mainClass in Compile := Some("Experiments.Main")

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion % "test"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion % "test"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion % "test"


assembly / assemblyMergeStrategy := {
	case PathList("META-INF", xs@_*) => MergeStrategy.discard
	case x => MergeStrategy.first
}
val artifactoryUrl = "https://stardog.jfrog.io/stardog/stardog-testing/nightly-develop-jedai-snapshot/MAVEN"

val artifactoryUser = sys.env("artifactoryUsername")
val artifactoryPassword = sys.env("artifactoryPassword")

publishTo := Some("Artifactory Realm" at artifactoryUrl)
credentials += Credentials("Artifactory Realm", artifactoryUrl, artifactoryUser, artifactoryPassword)
