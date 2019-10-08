package com.wolox.steps;

class Step {
    List<String> commands = []
    List<String> ostypes = []
    String name
    String script() {
        return commands.join('\n')
    }
}
