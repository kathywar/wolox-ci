import com.wolox.*

def call(ArrayList commands) {
  return {
      env.WSTOP = env.WORKSPACE
      def scr = commands.join('\n')
      println "Commands are: $scr"
      sh """$scr"""
  }
}
