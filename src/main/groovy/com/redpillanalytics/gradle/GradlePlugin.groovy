package com.redpillanalytics.gradle

import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project

@Slf4j
class GradlePlugin implements Plugin<Project> {

   /**
   * Extension name
   */
   static final String EXTENSION = 'plugin'

   /**
    * Apply the Gradle plugin
    */
   void apply(Project project) {

      // apply Gradle built-in plugins
      project.apply plugin: 'base'
      project.apply plugin: 'com.redpillanalytics.gradle-properties'

      // apply the Gradle plugin extension and the context container
      applyExtension(project)

      // get the taskGroup
      String taskGroup = project.extensions."$EXTENSION".taskGroup

      // create run task
      project.task('run') {
         group taskGroup
         description "Execute all RUN tasks."
      }

      // create deploy task
      project.task('deploy') {
         group taskGroup
         description "Execute all DEPLOY tasks."
      }

      project.afterEvaluate {
         // Go look for any -P properties that have "$EXTENSION." in them
         // If so... update the extension value
         project.pluginProps.setParameters(project, EXTENSION)
      }
   }

   /**
    * Apply the Gradle Plugin extension.
    */
   void applyExtension(Project project) {

      project.configure(project) {
         extensions.create(EXTENSION, GradlePluginExtension)
      }
   }
}

