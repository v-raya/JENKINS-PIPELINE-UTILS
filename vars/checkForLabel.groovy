#!/usr/bin/env groovy

import gov.ca.cwds.jenkins.semver.LabelChecker

def call(String projectName) {
  labelChecker = new LabelChecker(this)
  labelChecker.check(projectName)
}
