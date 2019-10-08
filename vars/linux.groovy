//@Library('wolox-ci')
import com.wolox.*;
import com.wolox.steps.Step;

def call(Step step) {
  return {
    stage(step.name) {
      timeout(time: projectConfig.timeout) {
        withEnv(projectConfig.environment) {
          println "Script is " + step.script()
          def myscr=step.script()
          sh returnStdout: true, script: "$myscr"
        }
      }
    }
  }
}
