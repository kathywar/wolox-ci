package com.wolox.steps;

class Step {
    List<String> commands = []
    String name
    String script() {
        return commands.join('\n')
    }
}
