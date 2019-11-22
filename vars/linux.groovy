import com.wolox.*;
import com.wolox.steps.Step;

def call(Step step) {
  return {
      env.WSTOP = env.WORKSPACE

      def scr = step.script()
      sh """$scr"""
  }
}
