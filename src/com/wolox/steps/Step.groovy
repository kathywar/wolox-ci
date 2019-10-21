package com.wolox.steps;

class Step {
    List<String> commands = []
    Map osMatrix = [:]
    String name
    String script() {
        return commands.join('\n')
    }
}
