//@Library('wolox-ci')
import com.wolox.*;
import com.wolox.steps.Step;

def call(ProjectConfiguration projectConfig) {
  return {
    List<Step> stepsA = projectConfig.steps.steps
    stepsA.each { step ->
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
}
