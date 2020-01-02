//@Library('wolox-ci')
import com.wolox.*;
import com.wolox.steps.Step;

def call(Step step) {
  return {
    def scr = step.script()
    echo bat( returnStdout: true, script: """ sh -x -c \"${scr}\" """).trim()
  }
}
