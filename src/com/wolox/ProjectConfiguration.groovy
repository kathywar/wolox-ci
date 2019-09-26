package com.wolox;

import com.wolox.docker.DockerConfiguration;
import com.wolox.steps.Steps;

class ProjectConfiguration {
    def environment;
    def services;
    Steps steps;
    OS os;
    def dockerfile;
    def projectName;
    def buildNumber;
    DockerConfiguration dockerConfiguration;
    def env;
    def timeout;
}
