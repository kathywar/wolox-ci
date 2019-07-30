package com.wolox.steps;

class Steps {
    List<Step> steps;

    def getVar(def dockerImage) {
        return "buildSteps"
    }

    int size() {
        return steps.size()
    }

    String getString() {
        steps.collect { 'Name=' + it.value.name + ' ' + 'Commands=' + it.value.commands.ToString() + ',' }
        return steps.sum()
    }
}
