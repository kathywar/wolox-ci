import com.wolox.*;
import com.wolox.steps.*;

def call(ProjectConfiguration projectConfig) {
  return {
    List<Task> tasksA = projectConfig.tasks.tasks
    tasksA.each { task ->
      task.osMatrix.each { k,v ->
        node("${k}") {
          println "workspace type: $task.wsType"
          stage("create workspace-$k") {
            deleteDir()
            def wscreate = "$task.wsType"(projectConfig.environment,
                                          projectConfig.timeout)
            wscreate()
          }

          List<Step> stepsA = task.steps.steps
          stepsA.each { step ->
            stage("$step.name-$k") {
              timeout(time: projectConfig.timeout) {
                withEnv(projectConfig.environment) {
                  def closure = "${v}"(step)
                  closure()
                }
              }
            }
          }
        }
      }
    }
  }
}
