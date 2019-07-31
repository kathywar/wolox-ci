//@Library('wolox-ci')
import com.wolox.*;
import com.wolox.steps.Step;

def call(ProjectConfiguration projectConfig, def dockerImage) {
    println "Called buildsteps.groovy with args projectConfig: $projectConfig";
    return { variables ->
        List<Step> stepsA = projectConfig.steps.steps
        //def links = variables.collect { k, v -> "--link ${v.id}:${k}" }.join(" ")
        //dockerImage.inside(links) {
            stepsA.each { step ->
                stage(step.name) {
                    step.commands.each { command ->
                        //sh command
                        println "Command=$command"
                        //sh label: 'Shell command execution', returnStdout: true, script: "$command"
                    }
                }
            }
       // }
    }
}
