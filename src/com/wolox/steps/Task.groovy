package com.wolox.steps;

class Task {
    String name
    Map osMatrix = [:]
    String taskType
    String wsType = "scmworkspace"
    Steps steps

    def getVar(def dockerImage) {
        return "buildTask"
    }
}
