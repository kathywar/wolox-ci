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
        List<String> strvals = steps.collect {
            'Name=' + it.name + ' ' + 'Commands=' + it.commands.toString() + ',' }
        return strvals.join(',')
    }
}
