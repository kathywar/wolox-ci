//@Library('wolox-ci')
import com.wolox.parser.ConfigParser;
import com.wolox.*;

def call(String yamlName) {
    sh label: 'Shell command execution', returnStdout: true, script: 'echo Reading yaml file';
    sh label: 'Shell command execution', returnStdout: true, script: 'echo Current dir=`pwd`';
    sh label: 'Shell command execution', returnStdout: true, script: 'echo Listing=\n';
    sh label: 'Shell command execution', returnStdout: true, script: "echo `ls -la`";

    def yaml = readYaml file: yamlName;
    println "yaml=$yaml";

    def buildNumber = Integer.parseInt(env.BUILD_ID)
    println "Build number= $buildNumber";

    // load project's configuration
    ProjectConfiguration projectConfig = ConfigParser.parse(yaml, env);
    println "Services= $projectConfig.services";

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
