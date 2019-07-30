//@Library('wolox-ci')
import com.wolox.*;

def call(ProjectConfiguration projectConfig) {
    println "shellcmd called";
    def reference = projectConfig.dockerConfiguration.reference();
    try {
        sh "echo this is just a stub service "

    } catch(ignored) {
        // this would make the entire popeline fail. We don't want that
        println ignored
    }
}
