import groovy.util.logging.Slf4j
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Title

@Slf4j
@Title("Execute gradle properties")
@Stepwise
class PropertiesTest extends Specification {
   @Shared
   File projectDir, buildDir, settingsFile, resourcesDir, buildFile

   @Shared
   List tasks

   @Shared
   BuildResult result

   @Shared
   AntBuilder ant = new AntBuilder()

   @Shared
   String taskName

   @Shared
   String projectName = 'run-properties'

   @Shared
   String options = '-Si'

   @Shared
   String taskGroup = System.getProperty("pipeline") ?: 'pipeline'

   @Shared
   String analyticsVersion = System.getProperty("analyticsVersion")

   def setupSpec() {

      projectDir = new File("${System.getProperty("projectDir")}/test-build")
      buildDir = new File(projectDir, 'build')
      buildFile = new File(projectDir, 'build.gradle')

      resourcesDir = new File('src/test/resources')

      ant.copy(todir: projectDir) {
         fileset(dir: resourcesDir)
      }

      settingsFile = new File(projectDir, 'settings.gradle').write("""rootProject.name = '$projectName'""")

      buildFile = new File(projectDir, 'build.gradle').write("""
               |plugins {
               |  id 'com.redpillanalytics.gradle-plugin'
               |  id "com.redpillanalytics.gradle-analytics" version "$analyticsVersion"
               |}
               |
               |repositories {
               |  jcenter()
               |  mavenLocal()
               |  maven {
               |     name 'test'
               |     url 'gcs://maven.redpillanalytics.io/demo'
               |  }
               |}
               |
               |plugin {
               |  taskGroup = '$taskGroup'
               |}
               |""".stripMargin())
   }

   // helper method
   def executeSingleTask(String taskName, List otherArgs) {

      otherArgs.add(0, taskName)

      log.warn "runner arguments: ${otherArgs.toString()}"

      // execute the Gradle test build
      result = GradleRunner.create()
              .withProjectDir(projectDir)
              .withArguments(otherArgs)
              .withPluginClasspath()
              .forwardOutput()
              .build()
   }

   def "Execute :properties task"() {
      given:
      taskName = 'properties'
      result = executeSingleTask(taskName, [options])

      expect:
      !result.tasks.collect { it.outcome }.contains('FAILED')
   }
}
