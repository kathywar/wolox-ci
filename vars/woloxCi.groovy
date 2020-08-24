
import com.wolox.parser.ConfigParser
import com.wolox.*

def call(String defBranch, Boolean useDefBranch=false, String credential="github-cred", String yamlName="jenkins/jenkins.yml", Boolean updateRepo=true) {

    def buildNumber = Integer.parseInt(env.BUILD_ID)
    println "Build number= $buildNumber"

    env.BLDID = "joblock-" + env.BUILD_NUMBER.toString()
    env.CREDENTIAL = credential
    env.DEFAULT_BRANCH=defBranch

    // must clone once to retrieve yaml file
    //node('scels80_debug') {

        if (updateRepo) {
            stage('initialize job') {
                deleteDir()
                def wscreate = scmworkspace([], 15, useDefBranch)
                wscreate()
            }
        }

        println "env.REPO_PATH= $env.REPO_PATH"
        if(env.REPO_PATH) {
            def yaml = readYaml file: "$env.REPO_PATH/$yamlName"
        }
        else {
            def yaml = readYaml file: "$yamlName"
        }

        // load project's configuration
        ProjectConfiguration projectConfig = ConfigParser.parse(yaml, env)

        buildName projectConfig.projectName
        buildDescription projectConfig.description

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
    //}
}
