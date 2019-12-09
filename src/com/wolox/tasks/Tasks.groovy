package com.wolox.tasks

class Tasks {
    def tasks = [:]

    def getVar(def dockerImage) {
        return "buildTasks"
    }

    int size() {
        return tasks.size()
    }

}
