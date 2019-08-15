import scalariform.formatter.preferences._
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.SbtArtifactory
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "cgt-property-disposals-stubs"

lazy val scalariformSettings =
  scalariformPreferences := scalariformPreferences
    .value
    .setPreference(AlignArguments, true)
    .setPreference(AlignParameters, false)
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(AllowParamGroupsOnNewlines, false)
    .setPreference(CompactControlReadability, false)
    .setPreference(CompactStringConcatenation, false)
    .setPreference(DanglingCloseParenthesis, Force)
    .setPreference(DoubleIndentConstructorArguments, true)
    .setPreference(DoubleIndentMethodDeclaration, true)
    .setPreference(FirstArgumentOnNewline, Force)
    .setPreference(FirstParameterOnNewline, Force)
    .setPreference(IndentLocalDefs, true)
    .setPreference(IndentPackageBlocks, true)
    .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
    .setPreference(NewlineAtEndOfFile, true)
    .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, false)
    .setPreference(PreserveSpaceBeforeArguments, false)
    .setPreference(RewriteArrowSymbols, false)
    .setPreference(SingleCasePatternOnNewline, false)
    .setPreference(SpaceBeforeColon, false)
    .setPreference(SpaceBeforeColon, false)
    .setPreference(SpaceInsideBrackets, false)
    .setPreference(SpaceInsideParentheses, false)
    .setPreference(SpacesAroundMultiImports, false)
    .setPreference(SpacesWithinPatternBinders, true)


lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory)
  .settings(
    majorVersion                     := 0,
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test
  )
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(PlayKeys.playDefaultPort := 7022)
  .settings(scalariformSettings)