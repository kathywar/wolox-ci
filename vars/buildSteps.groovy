//@Library('wolox-ci')
import com.wolox.*;
import com.wolox.steps.Step;

def call(ProjectConfiguration projectConfig) {
  return {
    List<Step> stepsA = projectConfig.steps.steps
    stepsA.each { step ->
      step.ostypes.each {
        node("${it}") {
          stage(step.name) {
            timeout(time: projectConfig.timeout) {
              withEnv(projectConfig.environment) {
                println "Script is " + step.script()
                def myscr=step.script()
                "${it}"(step)
              }
            }
          }
        }
      }
    }
  }
}
