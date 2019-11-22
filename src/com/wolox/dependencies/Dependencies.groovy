package com.wolox.dependencies

class Dependencies {
    List<Dependency> dependencies

    int size() {
        return dependencies.size()
    }

    String getString() {
        List<String> strvals = dependencies.collect {
            'Name=' + it.name + ',' +
            'Paths=' + it.paths.toString() + ',' +
            it.os }
        return strvals.join(',')
    }
}
