package gov.ca.cwds.jenkins

import spock.lang.Specification

class GithubPullRequestBuilderTriggerPropertiesSpecification extends Specification {
  class Environment {
    public static final String JOB_NAME = 'myjob'
    public static final String BUILD_ID = 33
  }
  class PipelineScript {
    def env = new Environment()
  }

  def "#triggerProperties returns a map with correct status url"() {
    given:
    def githubPullRequestBuilderTriggerProperties = new GithubPullRequestBuilderTriggerProperties(new PipelineScript())

    when:
    def properties = githubPullRequestBuilderTriggerProperties.triggerProperties('http://example.com:8080')

    then:
    properties['extensions'][1]['statusUrl'] == 'http://example.com:8080/job/myjob/33'
  }
}
