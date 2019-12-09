package com.wolox.tasks

import com.wolox.dependencies.*
import com.wolox.steps.*

class Task {
    String name
    String fullName // with node
    String taskType
    String wsType = "scm"

    String os = "linux"
    String nodeLabel = "LX_EL8"

    // object containing information on
    // other Tasks this Task depends on
    Dependencies dependencies

    // contains names of the other tasks
    // depending on this Task
    List<String> dependents = []
    List<String> artifacts = []

    TaskStates state
    Steps steps

    def getVar(def dockerImage) {
        return "buildTask"
    }
}
