#!/usr/bin/env groovy
import gov.ca.cwds.jenkins.docker.Docker

def call() { 
  def docker = new Docker(this.env.JOB_NAME, this.env.BUILD_ID, this, docker)
  docker.removeTestingImage()
}