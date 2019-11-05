package com.wolox.steps;

class Task {
    String name
    Map osMatrix = [:]
    String taskType
    Steps steps

    def getVar(def dockerImage) {
        return "buildTask"
    }
}
