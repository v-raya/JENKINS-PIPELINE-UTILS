#!/usr/bin/env groovy
import gov.ca.cwds.jenkins.StaticAnalyzer
import gov.ca.cwds.jenkins.docker.Docker

def call(Map lintParameters) {    
    docker = new Docker(this, docker)
    staticAnalyzer = new StaticAnalyzer(this, docker)
    staticAnalyzer.lint();
}
