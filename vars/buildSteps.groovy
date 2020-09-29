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

    def nodeName = "${task.nodeLabel}"

    if (task.dispatcher) {
      node("${task.dispatcher}") {
        nodeName = "${task.nodeLabel}_" + now.format("YYYYMMdd_HHmmss")
        if (!Create_Node_Step(platform: task.os,
                              NodeClass: "${task.nodeLabel}",
                              NodeLabel: "${nodeName}",
                              TokenID: "${env.JENKINS_API_CREDENTIAL}",
                              CIBranch: "private/kathywar/generic_server_token" )) {
          currentBuild.result = 'FAILURE'
        }
      }
    }

    node("${nodeName}") {

      try {

        stage("$task.fullName-create workspace") {
          deleteDir()
          def wsType = task.wsType + "workspace"
          def wscreate = "$wsType"(projectConfig.environment, projectConfig.timeout)
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

      } catch (org.jenkinsci.plugins.workflow.steps.FlowInterruptedException fie) {
        // this ambiguous condition means a user probably aborted
        println "FlowInterruptedException fired"
        def descr = fie.causes[0].getShortDescription()
        println "Cause description: $descr"
        if (descr.contains('Aborted')) {
          env.ABORTED=1
        } else {
          throw fie
        }
      } catch (hudson.AbortException ae) {
        // this ambiguous condition means during a shell step, user probably aborted
        println "AbortException fired"
        println "Message sent: " + ae.getMessage()
        if (ae.getMessage().contains('script returned exit code 143')) {
          env.ABORTED=1
        } else {
          throw ae
        }
      } finally {

        if (task.artifacts) {
          archiveArtifacts artifacts: task.artifacts.join(','), allowEmptyArchive: true
        }

        if ( task.abortSteps && env.ABORTED ) {
          task.abortSteps.steps.each { step ->
            stage("$task.fullName-abort-$step.name") {
              timeout(time: projectConfig.timeout) {
                withEnv(projectConfig.environment) {
                  def closure = "${task.os}"(step.commands)
                  closure()
                }
              }
            }
          }
        }
      }

    }

    println now.format("YYYY/MM/dd HH:mm:ss") + ": Completed task: $taskName"

    // update build description
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
