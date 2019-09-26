//@Library('wolox-ci')
import com.wolox.parser.ConfigParser;
import com.wolox.*;

def call(String yamlName="jenkins/jenkins.yml") {
    echo 'Reading yaml file';

    def yaml = readYaml file: yamlName;
    println "yaml=$yaml";

    def buildNumber = Integer.parseInt(env.BUILD_ID)
    println "Build number= $buildNumber";

    // clean workspace
    stage('clean workspace') {
        deleteDir()
    }

    // prepare archive
    sh label: 'Shell command execution', returnStdout: true,
       script: "if [ -d $WORKSPACE/archive ]; then rm -rf $WORKSPACE/archive; fi; mkdir $WORKSPACE/archive";

    // create workspace
    // TODO: put in class structure as a subclass of Step
    stage('create workspace') {
        sh label: 'Shell command execution', returnStdout: true, script: "echo `printenv | sort`";
        def url = scm.getUserRemoteConfigs()[0].getUrl()
        def repoName = url.tokenize('/').last().split("\\.git")[0]
        println "Repo name: $repoName"
        println "Url: $url"
        script {

            gitVars = dir("ws/$repoName") {
                git changelog: false,
                credentialsId: 'kmw-github-cred',
                poll: false,
                url: "$url"
            }

            env.GIT_COMMIT = gitVars.GIT_COMMIT
            env.GIT_LOCAL_BRANCH = gitVars.GIT_LOCAL_BRANCH
            env.GIT_BRANCH = gitVars.GIT_BRANCH
            println "Branch=$env.GIT_LOCAL_BRANCH"
            println "Branch is $env.BRANCH_NAME"
            println "Local branch is $env.GIT_LOCAL_BRANCH"

            sh label: 'Shell command execution', returnStdout: true,
               script: "printenv | sort";
        }

        env.WSDIR=env.WORKSPACE + '/ws'
        env.REPO_PATH=env.WSDIR + "/$repoName"

        println "Workdir=$env.WSDIR"

        if ( env.CHANGE_BRANCH ) {
            env.LOCAL_BRANCH=env.CHANGE_BRANCH
        } else if ( env.BRANCH_NAME ) {
            env.LOCAL_BRANCH=env.BRANCH_NAME
        } else {
            env.LOCAL_BRANCH=env.GIT_BRANCH.drop(env.GIT_BRANCH.indexOf('/')+1)
        }

        sh label: 'Shell command execution', returnStdout: true,
           script: "cd $REPO_PATH && git checkout $LOCAL_BRANCH";

        buildDescription 'Workspace checkout is complete.'
    }

    yamlName = "$env.REPO_PATH/$yamlName"
    def yaml = readYaml file: yamlName;

    // load project's configuration
    ProjectConfiguration projectConfig = ConfigParser.parse(yaml, env);

    println "Services: $projectConfig.services"
    println "OS: $projectConfig.os"
    def numsteps = projectConfig.steps.steps.size();

    def stepstr = projectConfig.steps.getString();

    // adds the last step of the build.
    //def closure = buildSteps(projectConfig);

    // each service is a closure that when called it executes its logic and then calls a closure, the next ste
    def closure = "${projectConfig.os}"(projectConfig);

    // we execute the top level closure so that the cascade starts.
    try {
        closure([:]);
    } finally{
    }

    // archive - TODO: put in class structure as a subclass of Step
    archiveArtifacts artifacts: 'archive/', allowEmptyArchive: true

}
