//@Library('wolox-ci')
import com.wolox.*;
import com.wolox.steps.Step;

def call(ProjectConfiguration projectConfig) {
    println "Called buildsteps.groovy"
    return {
        List<Step> stepsA = projectConfig.steps.steps
        stepsA.each { step ->
            stage(step.name) {
                command ->
                    withEnv(projectConfig.environment) {
                      println "Script is " + step.script()
                      def myscr=step.script()
                      sh """
                        $step.script()
                      """
                    }
            }
        }
    }
}
