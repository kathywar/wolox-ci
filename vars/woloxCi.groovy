//@Library('wolox-ci')
import com.wolox.parser.ConfigParser;
import com.wolox.*;

def call(String yamlName="jenkins/jenkins.yml") {
    sh label: 'Shell command execution', returnStdout: true, script: 'echo Reading yaml file';
    sh label: 'Shell command execution', returnStdout: true, script: 'echo Current dir=`pwd`';
    sh label: 'Shell command execution', returnStdout: true, script: "echo `ls -la`";

    def buildNumber = Integer.parseInt(env.BUILD_ID)
    println "Build number= $buildNumber";

    // clean workspace
    stage('CleanWS') {
        deleteDir()
    }

    // create workspace

    stage('Create Workspace') {
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
    println "yaml=$yaml";

    // load project's configuration
    ProjectConfiguration projectConfig = ConfigParser.parse(yaml, env);

    def numsteps = projectConfig.steps.steps.size();
    println "Steps count= $numsteps";

    def stepstr = projectConfig.steps.getString();
    println "Steps= $stepstr";
    println "Env= $projectConfig.environment";

    // adds the last step of the build.
    def closure = buildSteps(projectConfig);

    // we execute the top level closure so that the cascade starts.
    try {
        closure([:]);
    } finally{
    }
}
