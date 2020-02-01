package com.redpillanalytics.gradle

import groovy.util.logging.Slf4j

@Slf4j
class GradlePluginExtension {

   /**
    * The group name to use for all tasks. Default: 'plugin'.
    */
   String taskGroup = 'plugin'

   /**
    * Returns a simple string.
    *
    * @return a simple string
    */
   String getSimpleString() {

      return "simple string"
   }
}
