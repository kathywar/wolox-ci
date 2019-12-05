package com.wolox.parser;

import com.wolox.ProjectConfiguration;
import com.wolox.docker.DockerConfiguration;
import com.wolox.dependencies.*
import com.wolox.services.*;
import com.wolox.steps.*;

class ConfigParser {

    private static String LATEST = 'latest'
    private static Integer DEFAULT_TIMEOUT = 600   // 600 seconds
    private static String DEFAULT_OS = 'linux'
    private static String WORKSPACE_DEFAULT = 'default'
    private static String DEFAULT_WN_NODE = 'WN'
    private static String DEFAULT_LN_NODE = 'LX'

    static ProjectConfiguration parse(def yaml, def env) {

        ProjectConfiguration projectConfiguration = new ProjectConfiguration();

        projectConfiguration.buildNumber = env.BUILD_ID;

        // parse the environment variables
        projectConfiguration.environment    = parseEnvironment(yaml.environment);

        // parse the execution steps
        projectConfiguration.tasks          = parseTasks(yaml.tasks);

        // parse the necessary services
        projectConfiguration.services   = parseServices(yaml.services);

        // load the dockefile
        projectConfiguration.dockerfile = parseDockerfile(yaml.config);

        // load the project name
        projectConfiguration.projectName = parseProjectName(yaml.config);

        projectConfiguration.env = env;

        projectConfiguration.dockerConfiguration = new DockerConfiguration(projectConfiguration: projectConfiguration);

        projectConfiguration.timeout = yaml.timeout ?: DEFAULT_TIMEOUT;

        return projectConfiguration;
    }

    static def parseEnvironment(def environment) {
        if (!environment) {
            return "";
        }

        return environment.collect { k, v -> "${k}=${v}"};
    }

    static def parseTasks(def yamlTasks) {

        List<Task> tasks = yamlTasks.collect { k, v ->
            Task task = new Task(name: k)

            task.taskType = v.type

            if (v.workspace && v.workspace != WORKSPACE_DEFAULT ) {
                task.wsType = v.workspace
            }

            if (v.os) {
                task.osMatrix = v.os.collectEntries()
            } else {
                task.osMatrix.put DEFAULT_LX_NODE, DEFAULT_OS
            }

            task.steps = parseSteps(v.steps)

            if (v.artifacts) {
              task.artifacts = v.artifacts.paths
            }
            
            if (v.dependencies) {
              task.dependencies = parseDependencies(v.dependencies)
            }

            return task
        }
        return new Tasks(tasks: tasks)
    }

    static def parseSteps(def yamlSteps) {
        List<Step> steps = yamlSteps.collect { k, v ->
            Step step = new Step(name: k)

            // a step can have one or more commands to execute
            v.each {
                step.commands.add(it);
            }
            return step
        }
        return new Steps(steps: steps);
    }

    static def parseDependencies(def yamlDeps) {
        List<Dependency> deps = yamlDeps.collect {
            Dependency dep = new Dependency()
            it.each { k, v ->
              dep.name = k 
              dep.paths = v.paths
              if ( v.os ) { dep.os = v.os }
            }

            return dep
        }
        return new Dependencies(dependencies: deps)
    }

    static def parseServices(def steps) {
        def services = [];

        steps.each {
            def service = it.tokenize(':')
            def version = service.size() == 2 ? service.get(1) : LATEST
            def instance = getServiceClass(service.get(0).capitalize())?.newInstance()

            services.add([service: instance, version: version])
        };

        services.add([service: new Base(), version: LATEST]);

        return services
    }

    static def getServiceClass( def name ){
        switch(name) {
            case "Postgres":
                return Postgres
                break
            case "Redis":
                return Redis
                break
            case "Mssql":
                return Mssql
                break
            case "Mysql":
                return Mysql
                break
            case "Mongodb":
                return Mongodb
                break
            case "Elasticsearch":
                return Elasticsearch
                break
        }
    }

    static def parseDockerfile(def config) {
        if (!config || !config["dockerfile"]) {
            return "Dockerfile";
        }

        return config["dockerfile"];
    }

    static def parseProjectName(def config) {
        if (!config || !config["project_name"]) {
            return "woloxci-project";
        }

        return config["project_name"];
    }
}
