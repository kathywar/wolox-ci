package com.wolox.steps;

class Task {
    String name
    Map osMatrix = [:]
    List<String> dependencies
    String taskType
    String wsType = "scmworkspace"
    Steps steps

    def getVar(def dockerImage) {
        return "buildTask"
    }
}
