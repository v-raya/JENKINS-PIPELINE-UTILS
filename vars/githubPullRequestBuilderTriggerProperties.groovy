#!/usr/bin/env groovy
import gov.ca.cwds.jenkins.GithubPullRequestBuilderTriggerProperties

def call(jenkinsUrl = 'http://jenkins.dev.cwds.io:8080') {
  trigger = new GithubPullRequestBuilderTrigger(this)
  trigger.triggerProperties(jenkinsUrl)
}
