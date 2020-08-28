def call(ArrayList commands) {
  return {
    if ( commands.size() > 1 ) {
      def echoscript = "#!/bin/bash\nset -eE -o pipefail\n"
      commands.each {
          echoscript = echoscript + "${it}\n"
		  //+ "echo + \"${it}\"\n" + "${it}\n"
      }
      env.WSTOP = env.WORKSPACE.replaceAll('\\\\','/')
      env.FILEPATH = env.WSTOP + "/icl-pipeline.sh"

      writeFile file: env.FILEPATH, text: """cd $WSTOP\n$echoscript"""

      echo bat (returnStdout:true,
                script: """if not exist "R:\\" (net use R: \\\\nncv03a-cifs.inn.intel.com\\rdrive\\wref1)
                           exit %ERRORLEVEL
                        """)
	  echo bat (returnStdout:true,
                script: """R:\\u4win\\u4w_ksh.bat /c %FILEPATH%
                        """)
      //bat returnStdout: true, script: 'del %WORKSPACE%\\icl-pipeline.sh'
    } else {
      def cmd=commands[0]
      println "Running single command: $cmd"
      echo bat (returnStdout: true,
                script: """c:\\u4win\\bin\\x86_win32\\sh -x -c \"${cmd}\"""")

    }
  }
}
