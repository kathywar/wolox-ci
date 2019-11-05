package com.wolox;

import com.wolox.docker.DockerConfiguration;
import com.wolox.os.*;
import com.wolox.steps.*;

class ProjectConfiguration {
    def environment;
    def services;
    Tasks tasks;
    def dockerfile;
    def projectName;
    def buildNumber;
    DockerConfiguration dockerConfiguration;
    def env;
    def timeout;
}
