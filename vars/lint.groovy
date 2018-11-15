#!/usr/bin/env groovy
import gov.ca.cwds.jenkins.StaticAnalyzer
import gov.ca.cwds.jenkins.docker.Docker
import gov.ca.cwds.jenkins.common.BuildMetadata


def call() {  
    BuildMetadata buildMetadata = new BuildMetadata(this, this.env.JOB_NAME, this.env.BUILD_ID, this.env.WORKSPACE)
    def docker = new Docker(this)
    def rtGradle = Artifactory.newGradleBuild()
    staticAnalyzer = new StaticAnalyzer(docker, rtGradle, this)  
    staticAnalyzer.lint(buildMetadata);
}
