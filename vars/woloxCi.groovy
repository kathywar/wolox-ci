import com.wolox.parser.ConfigParser;
import com.wolox.*;

def call(String yamlName="") {

    def buildNumber = Integer.parseInt(env.BUILD_ID)
    println "Build number= $buildNumber"

    // must clone once to retrieve yaml file
    node('LX&&SC') {
        stage('initialize job') {
            deleteDir()
            def wscreate = scmworkspace([], 15)
            wscreate()
        }

        if ( yamlName == "" ) {
          yamlName = "$env.REPO_PATH/jenkins/jenkins.yml"
        }
        def yaml = readYaml file: yamlName;
 
        // load project's configuration
        ProjectConfiguration projectConfig = ConfigParser.parse(yaml, env)

        // adds the last step of the build.
        def closure = buildSteps(projectConfig)

        // we execute the top level closure so that the cascade starts.
        try {
            closure([:])
        } finally{
        }
    }
}
