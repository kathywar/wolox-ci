//@Library('wolox-ci')
import com.wolox.*;
import com.wolox.steps.Step;

def call(Step step) {
  return {
    stage(step.name) {
      sh returnStdout: true, script: "if [ ! -d archive ]; then mkdir archive; fi"

      println "Step: $step.name"
      println "Step script is " + step.script()
      def myscr=step.script()
      sh(returnStdout: true, script: """$myscr""")
      archiveArtifacts artifacts: 'archive/', allowEmptyArchive: true
    }
  }
}
