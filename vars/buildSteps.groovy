//@Library('wolox-ci')
import com.wolox.*;
import com.wolox.steps.Step;

def call(ProjectConfiguration projectConfig) {
  return {
    List<Step> stepsA = projectConfig.steps.steps
    stepsA.each { step ->
      step.osMatrix.each { k,v ->
        println "Key=$k, Val=$v"
        node("${k}") {
          stage("$step.name-$k") {
            timeout(time: projectConfig.timeout) {
              withEnv(projectConfig.environment) {
                println "Script is " + step.script()
                def myscr=step.script()
                def closure = "${v}"(step)
                closure()
              }
            }
          }
        }
      }
    }
  }
}
