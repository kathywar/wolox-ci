package com.wolox.steps

import com.wolox.dependencies.*

class Task {
    String name
    Map osMatrix = [:]
    Dependencies dependencies
    List<String> artifacts
    String taskType
    String wsType = "scm"
    Steps steps

    def getVar(def dockerImage) {
        return "buildTask"
    }
}
