package com.wolox.steps;

class Tasks {
    List<Task> tasks;

    def getVar(def dockerImage) {
        return "buildTasks"
    }

    int size() {
        return tasks.size()
    }

    String getString() {
        List<String> strvals = tasks.collect {
            'Name=' + it.name + ' ' + 'OS-Types=' + it.osMatrix.toString() + ',' +
            'Steps=' + it.steps.toString() + ',' }
        return strvals.join(',')
    }
}
