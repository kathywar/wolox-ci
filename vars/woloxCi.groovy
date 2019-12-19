import com.wolox.parser.ConfigParser;
import com.wolox.*;

def call(String yamlName="") {

    def buildNumber = Integer.parseInt(env.BUILD_ID)
    println "Build number= $buildNumber"
    env.BLDID = "joblock-" + env.BUILD_NUMBER.toString()
    if ( ! env.CREDENTIAL ) {
        env.CREDENTIAL="github-cred"
    }
    println "Credential: $credential"
    // must clone once to retrieve yaml file
    node('LX&&SC') {
        stage('initialize job') {
            echo sh(returnStdout: true, script: "printenv && ls -la")
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

        // define parallel task closures
        def pTasks = [:]
        projectConfig.tasks.tasks.each { k, v ->
          String fullName = k
          if ( ! v.dependencies ) {
            pTasks[(fullName)] =  buildSteps(fullName, projectConfig) 
          }
        }

        // we execute a map of top level closures so that the cascade starts.
        // These top level closures are the tasks which are independent, they
        // in turn will start their dependent tasks
        try {
            parallel pTasks
        } finally{
        }
    }
}
