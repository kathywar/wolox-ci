//@Library('wolox-ci')
import com.wolox.*;

def call(ProjectConfiguration projectConfig, def _, def nextClosure) {
    println "Calling base.groovy";
    return { variables ->
            timeout(time: projectConfig.timeout, unit: 'SECONDS') {
                withEnv(projectConfig.environment) {
                    println "inside base.groovy loop"
                    println "Class:$class"

                    wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                        nextClosure(variables)
                    }
                }
            }
        }
    }
}
