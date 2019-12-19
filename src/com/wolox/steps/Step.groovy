package com.wolox.steps

class Step {
    String SPECIAL_CHARS=":\\ "
    String NEW_CHARS=": "

    Step( String stepname, List<String> list ) {
        name = stepname
        commands = list.collect {
            String item = it.replace(SPECIAL_CHARS, NEW_CHARS )
            return item
        }
    }
    private List<String> commands = []
    String name
    String script() {
        return commands.join('\n')
    }
}
