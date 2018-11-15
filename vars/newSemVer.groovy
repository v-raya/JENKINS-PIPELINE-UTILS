#!/usr/bin/env groovy
import gov.ca.cwds.jenkins.semver.SemVer

def call(String label = '') {
  semVer = new SemVer(this)
  semVer.newTag(label)
}
