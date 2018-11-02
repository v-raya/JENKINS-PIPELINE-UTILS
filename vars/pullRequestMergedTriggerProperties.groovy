#!/usr/bin/env groovy
import gov.ca.cwds.jenkins.PullRequestMergedTrigger

def call(String triggerKeyParameter) {
  pullRequestMergedTrigger = new PullRequestMergedTrigger(this)
  pullRequestMergedTrigger.triggerProperties(triggerKeyParameter)
}
