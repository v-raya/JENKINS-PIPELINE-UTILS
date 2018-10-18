#!/usr/bin/env groovy

def call(String containerName, String containerVersion) {
  containerScanner = new ContainerScanner(this)
  containerScanner.sendNotification(containerName, containerVersion);
}
