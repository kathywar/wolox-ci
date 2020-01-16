import java.time.LocalDateTime

import com.wolox.*
import com.wolox.steps.*
import com.wolox.tasks.*

def call(String taskName, ProjectConfiguration projectConfig) {
  return {
    def now = new Date()
    println now.format("YYYY/MM/dd HH:mm:ss") + ": Running task: $taskName"

    Task task = projectConfig.tasks.tasks[taskName]
    task.state = TaskStates.RUNNING

    node("${task.nodeLabel}") {

      stage("$task.fullName-create workspace") {
        deleteDir()
        def wsType = task.wsType + "workspace"
        def wscreate = "$wsType"(task.os,
                                 projectConfig.environment,
                                 projectConfig.timeout)
        wscreate()
      }

      if (task.dependencies) {
        copyArtifacts filter: task.dependencies.getList(),
                      projectName: env.JOB_NAME,
                      selector: specific(env.BUILD_NUMBER)
      }

      List<Step> stepsA = task.steps.steps
      stepsA.each { step ->
        stage("$task.fullName-$step.name") {
          timeout(time: projectConfig.timeout) {
            withEnv(projectConfig.environment) {
              def closure = "${task.os}"(step.commands)
              closure()
            }
          }
        }
      }
      if (task.artifacts) {
        archiveArtifacts artifacts: task.artifacts.join(','), allowEmptyArchive: true
      }

    }

    println now.format("YYYY/MM/dd HH:mm:ss") + ": Completed task: $taskName"
    logparser.archiveLogsWithBranchInfo(task.fullName + ".txt",
                                        [filter:"$task.fullName",
                                         markNestedFiltered:false,
                                         showParents:false
                                        ])

    // update build description at end of every task
    buildDescription projectConfig.description

    def taskCanExecute = { String name ->
        def result
        Task t = projectConfig.tasks.tasks[ (name) ]
        switch (t.state) {
            case TaskStates.WAIT:
                def blockingTasks = t.dependencies.dependencies.find {
                    Task parent = projectConfig.tasks.tasks[ it.fullName ]
                    return parent.state != TaskStates.DONE }
                result = false
                if ( ! blockingTasks ) { result = true }
                break
            case TaskStates.READY:
                result = true
                break
            default:
                result = false
        }

        result
    }

    task.state = TaskStates.DONE

    // launch child tasks
    if ( task.dependents ) {
        def pChildSteps = [:]
        lock(env.BLDID) {
            task.dependents.each {
                Task dependent = projectConfig.tasks.tasks[ (it) ]
                if ( taskCanExecute(dependent.fullName )) {
                    println now.format("YYYY/MM/dd HH:mm:ss") + ": Task: $taskName scheduled $dependent.fullName"
                    dependent.state = TaskStates.SCHEDULED
                    pChildSteps[(dependent.fullName)] = buildSteps( dependent.fullName, projectConfig)
                } else {
                    println now.format("YYYY/MM/dd HH:mm:ss") + ": Task: $taskName cannot schedule " +
                            " $dependent.fullName as it is blocked."
                }
            }
        }
        if ( pChildSteps.size() ) { parallel pChildSteps }
    }
  }
}
