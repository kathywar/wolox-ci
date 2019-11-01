//@Library('wolox-ci')
import com.wolox.*;
import com.wolox.steps.Step;

def call(Step step) {
  return {
      sh returnStdout: true, script: "if [ ! -d archive ]; then mkdir archive; fi"

      println "Step: $step.name"
      println "Step script is " + step.script()
      def myscr=step.script()
      sh """$myscr"""
      archiveArtifacts artifacts: 'archive/', allowEmptyArchive: true
  }
}