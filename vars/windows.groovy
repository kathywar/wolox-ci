import com.wolox.*
import com.wolox.steps.Step

def call(Step step) {
  return {

    env.WSTOP=env.WORKSPACE.replaceAll('\\\\','/')

    def echoscript = "#!/bin/bash\nset -eE -o pipefail\n"
    step.commands.each { 
        echoscript = echoscript + "echo + \"${it}\"\n" + "${it}\n" 
    }
    writeFile file: "$WORKSPACE\\icl-pipeline.sh", text: """cd $WSTOP\n$echoscript"""
    env.FILEPATH="$env.WSTOP/icl-pipeline.sh"
    echo bat (returnStdout:true,
              script: """call r:\\u4win\\u4w_ksh.bat /c %FILEPATH%
                         exit %ERRORLEVEL
                      """)
    bat returnStdout: true, script: 'del %WORKSPACE%\\icl-pipeline.sh'
  }
}
