package com.wolox.dependencies

class Dependencies {
    List<Dependency> dependencies

    int size() {
        return dependencies.size()
    }

    String getList() {
        List<String> strvals = dependencies.collect {
            it.paths.join(",")
        }
        return strvals.join(',')
    }
}
