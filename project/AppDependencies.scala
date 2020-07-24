import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-26"  % "2.24.0",
    "uk.gov.hmrc"       %% "stub-data-generator"        % "0.5.3",
    "com.eclipsesource" %% "play-json-schema-validator" % "0.9.5"
  )

  val test = Seq(
    "org.scalatest" %% "scalatest" % "3.0.8" % "test",
    "org.pegdown"    % "pegdown"   % "1.6.0" % "test"
  )

}
