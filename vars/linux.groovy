import com.wolox.*;
import com.wolox.steps.Step;

def call(Step step) {
  return {
      sh returnStdout: true, script: "if [ ! -d archive ]; then mkdir archive; fi"
      sh label: 'Shell command execution', returnStdout: true,
         script: "echo `printenv | sort`";

      println "Step: $step.name"
      def scr = step.script()
      println "Step script is \n$scr"
      sh """$scr"""
      archiveArtifacts artifacts: 'archive/', allowEmptyArchive: true
  }
}
