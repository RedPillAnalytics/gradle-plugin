import groovy.util.logging.Slf4j
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

@Slf4j
@Title("Check basic configuration")
class TasksTest extends Specification {

   @Shared
   File projectDir, buildDir, settingsFile, resourcesDir, buildFile

   @Shared
   def result, tasks, taskList

   @Shared
   String projectName = 'run-tasks'

   @Shared
   String taskGroup = System.getProperty("pipeline") ?: 'pipeline'

   @Shared
   String analyticsVersion = System.getProperty("analyticsVersion")

   def setupSpec() {

      projectDir = new File("${System.getProperty("projectDir")}/$projectName")
      buildDir = new File(projectDir, 'build')
      taskList = ['build']

      resourcesDir = new File('src/test/resources')

      new AntBuilder().copy(todir: projectDir) {
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

      result = GradleRunner.create()
              .withProjectDir(projectDir)
              .withArguments('-Si', 'tasks', '--all')
              .withPluginClasspath()
              .build()

      tasks = result.output.readLines().grep(~/(> Task :)(.+)/).collect {
         it.replaceAll(/(> Task :)(\w+)( UP-TO-DATE)*/, '$2')
      }

      log.warn result.getOutput()
   }

   def "All tasks run and in the correct order"() {

      given:
      ":tasks execution is successful"

      expect:
      ['SUCCESS', 'UP_TO_DATE'].contains(result.task(":tasks").outcome.toString())
   }

   @Unroll
   def "Executing :tasks contains :#task"() {

      when:
      "Gradle build runs"

      then:
      result.output.contains(task)

      where:
      task << ['build']
   }
}
