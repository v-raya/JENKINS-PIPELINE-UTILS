#!/usr/bin/env groovy
import gov.ca.cwds.jenkins.docker.Docker

def call() {
  echo 'building image for test'
  def docker = new Docker(this.env.JOB_NAME, this.env.BUILD_ID, this, docker)
  docker.createTestingImage()
}
