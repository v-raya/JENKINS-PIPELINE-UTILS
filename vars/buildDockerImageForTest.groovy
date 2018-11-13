#!/usr/bin/env groovy
import gov.ca.cwds.jenkins.docker.Docker

def call() {
  def pathToDockerFile './docker/test/Dockerfile'
  echo 'building image for test'
  docker = new Docker(this, docker)
  docker.createTestingImage(pathToDockerFile)
}