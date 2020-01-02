import com.wolox.*

def call(ArrayList commands) {
  return {

    if ( commands.size() > 1 ) {
      env.WSTOP=env.WORKSPACE.replaceAll('\\\\','/')

      def echoscript = "#!/bin/bash\nset -eE -o pipefail\n"
      commands.each { 
          echoscript = echoscript + "echo + \"${it}\"\n" + "${it}\n" 
      }
      writeFile file: "$WORKSPACE\\icl-pipeline.sh", text: """cd $WSTOP\n$echoscript"""
      env.FILEPATH="$env.WSTOP/icl-pipeline.sh"
      echo bat (returnStdout:true,
                script: """call r:\\u4win\\u4w_ksh.bat /c %FILEPATH%
                           exit %ERRORLEVEL
                        """)
      bat returnStdout: true, script: 'del %WORKSPACE%\\icl-pipeline.sh'
    } else {
      echo bat (returnStdout: true,
                script: """r:\\u4win\\bin\\x86_win32\\sh -x -c \"${commands[0]}\"""")

    }
  }
}
