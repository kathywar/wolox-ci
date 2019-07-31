//@Library('wolox-ci')
import com.wolox.*;
import com.wolox.steps.Step;

def call(ProjectConfiguration projectConfig) {
    println "Called buildsteps.groovy with args projectConfig: $projectConfig";
    return { 
        List<Step> stepsA = projectConfig.steps.steps
        stepsA.each { step ->
            stage(step.name) {
                step.commands.each { command ->
                    //sh command
                    println "Command=$command"
                    //sh label: 'Shell command execution', returnStdout: true, script: "$command"
                }
            }
       }
    }
}
