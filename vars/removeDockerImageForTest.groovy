#!/usr/bin/env groovy
import gov.ca.cwds.jenkins.docker.Docker

def call() { 
  echo 'removing image for test'
  docker = new Docker(this, docker)
  docker.removeTestingImage()
}