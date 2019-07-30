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
    println "Steps count= $projectConfig.steps.size()";
    println "Steps= $projectConfig.steps.getString()";
    println "Env= $projectConfig.environment";
    
    //def imageName = projectConfig.dockerConfiguration.imageName().toLowerCase();

    //def file = readFile(projectConfig.dockerfile)
    //def from = file.split('\n')[0]

    //projectConfig.baseImage = from

    // build the image specified in the configuration
    // def customImage = doscker.build(imageName, "--file ${projectConfig.dockerfile} .");

    // adds the last step of the build.
    def closure = buildSteps(projectConfig, null);
    println "Closure step= $closure";
    
    // each service is a closure that when called it executes its logic and then calls a closure, the next step.
    projectConfig.services.each {

        closure = "${it.service.getVar()}"(projectConfig, it.version, closure);
        println "Closure= $closure";
    }

    // we execute the top level closure so that the cascade starts.
    try {
        closure([:]);
    } finally{
    }
}
