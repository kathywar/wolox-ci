import com.wolox.*

def call(ArrayList commands) {
  return {
    if ( commands.size() > 1 ) {

      def echoscript = "#!/bin/bash\nset -eE -o pipefail\n"
      commands.each { 
          echoscript = echoscript + "echo + \"${it}\"\n" + "${it}\n" 
      }
      println "Script commands are: $echoscript"
      println "WORKSPACE2: $env.WORKSPACE"
      env.WSTOP = env.WORKSPACE.replaceAll('\\\\','/') 
      env.FILEPATH = env.WSTOP + "/icl-pipeline.sh"

      writeFile file: env.FILEPATH, text: """cd $WSTOP\n$echoscript"""

      println "FILEPATH: $env.FILEPATH"

      echo bat (returnStdout:true,
                script: """call r:\\u4win\\u4w_ksh.bat /c %FILEPATH%
                           exit %ERRORLEVEL
                        """)
      bat returnStdout: true, script: 'del %WORKSPACE%\\icl-pipeline.sh'
    } else {
      def cmd=commands[0]
      println "Running single command: $cmd"
      echo bat (returnStdout: true,
                script: """r:\\u4win\\bin\\x86_win32\\sh -x -c \"${cmd}\"""")

    }
  }
}
