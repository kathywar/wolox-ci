import com.wolox.*;
import com.wolox.steps.Step;

def call(Step step) {
  return {
      env.WSTOP = env.WORKSPACE
      sh returnStdout: true, script: "if [ ! -d archive ]; then mkdir archive; fi"

      def scr = step.script()
      sh """$scr"""
      archiveArtifacts artifacts: 'archive/', allowEmptyArchive: true
  }
}
