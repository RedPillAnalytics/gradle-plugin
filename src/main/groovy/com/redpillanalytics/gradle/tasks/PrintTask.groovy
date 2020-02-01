package com.redpillanalytics.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * List all topics available to KSQL
 */
@Slf4j
class PrintTask extends DefaultTask {

   PrintTask() {
      description = "Print stuff."
      group = project.extensions.plugin.taskGroup
   }

   @TaskAction
   def printTask(){
      doLast{ println "Stuff"}
   }
}
