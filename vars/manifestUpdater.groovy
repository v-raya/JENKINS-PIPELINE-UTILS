#!/usr/bin/env groovy

def call(applicationName, manifestName, credentialsId, version) {
  manifestUpdater = new ManifestUpdater(this)
  manifestUpdater.update(applicationName, manifestName, credentialsId, version)
}