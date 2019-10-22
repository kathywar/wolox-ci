//@Library('wolox-ci')
import com.wolox.*;
import com.wolox.steps.Step;

def call(Step step) {
  return {
    stage(step.name) {
      sh returnStdout: true, script: "if [ ! -d archive ]; then mkdir archive; fi"

      println "Step: $step.name)"
      timeout(time: projectConfig.timeout) {
        withEnv(projectConfig.environment) {
          println "Script is " + step.script()
          def myscr=step.script()
          echo sh(returnStdout: true, script: "$myscr")
        }
      }
      archiveArtifacts artifacts: 'archive/', allowEmptyArchive: true
    }
  }
}
