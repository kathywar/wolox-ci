import com.wolox.parser.ConfigParser
import com.wolox.*

def call(String defBranch, Boolean useDefBranch=false, String credential="github-cred", String yamlName="jenkins/jenkins.yml") {

    def buildNumber = Integer.parseInt(env.BUILD_ID)
    println "Build number= $buildNumber"

    env.BLDID = "joblock-" + env.BUILD_NUMBER.toString()
    env.CREDENTIAL = credential
    env.DEFAULT_BRANCH=defBranch

    // must clone once to retrieve yaml file
    node('LX&&SC') {

        stage('initialize job') {
            deleteDir()
            def wscreate = scmworkspace([], 15, useDefBranch)
            wscreate()
        }

        def yaml = readYaml file: "$env.REPO_PATH/$yamlName"
        println "Yaml: " + yaml

        // load project's configuration
        ProjectConfiguration projectConfig = ConfigParser.parse(yaml, env)

        buildName projectConfig.projectName
        buildDescription projectConfig.description

        println "Project config: " + projectConfig.tasks.tasks

        // define parallel task closures
        def pTasks = [:]
        projectConfig.tasks.tasks.each { k, v ->
          String fullName = k
          println "Task name: " + fullName
          if ( ! v.dependencies ) {
            println "Adding task: " + fullName
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
