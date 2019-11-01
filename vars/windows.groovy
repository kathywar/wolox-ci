//@Library('wolox-ci')
import com.wolox.*;
import com.wolox.steps.Step;

def call(Step step) {
  return {

    env.WKSPACE=env.WORKSPACE.replaceAll('\\\\','/')

    // create archive
    bat returnStdout: true, script: "if not exist archive (mkdir archive)"

    println "Step: $step.name"
    def echoscript = "#!/bin/bash\nset -eE -o pipefail\n"
    step.commands.each { 
        echoscript = echoscript + "echo + \"${it}\"\n" + "${it}\n" 
    }
    writeFile file: "$WORKSPACE\\icl-pipeline.sh", text: """$echoscript"""
    env.FILEPATH="$env.WKSPACE/icl-pipeline.sh"
    echo bat (returnStdout:true,
              script: """call r:\\u4win\\u4w_ksh.bat /c %FILEPATH%
                         exit %ERRORLEVEL
                      """)
    bat returnStdout: true, script: 'del %WORKSPACE%\\icl-pipeline.sh'

    archiveArtifacts artifacts: 'archive/', allowEmptyArchive: true
  }
}
