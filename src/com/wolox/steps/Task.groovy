package com.wolox.steps

import com.wolox.dependency.*

class Task {
    String name
    Map osMatrix = [:]
    List<Dependency> dependencies
    List<String> artifacts
    String taskType
    String wsType = "scmworkspace"
    Steps steps

    def getVar(def dockerImage) {
        return "buildTask"
    }
}
