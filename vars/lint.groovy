#!/usr/bin/env groovy
import gov.ca.cwds.jenkins.StaticAnalyzer
import gov.ca.cwds.jenkins.docker.Docker
import gov.ca.cwds.jenkins.common.ProjectTypesDeterminer


def call() {  
    def projectTypesDeterminer = new ProjectTypesDeterminer(this)
    def docker = new Docker(this.env.JOB_NAME, this.env.BUILD_ID, this, docker)
    staticAnalyzer = new StaticAnalyzer(projectTypesDeterminer, this.env.WORKSPACE, docker, this)
    staticAnalyzer.lint();
}
