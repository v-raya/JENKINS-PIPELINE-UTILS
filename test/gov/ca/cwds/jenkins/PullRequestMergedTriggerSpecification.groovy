package gov.ca.cwds.jenkins

import spock.lang.Specification

class PullRequestMergedTriggerSpecification extends Specification {
  def "#triggerProperties returns a map with a regex based on trigger key"() {
    given:
    def pullRequestMergedTrigger = new PullRequestMergedTrigger()

    when:
    def properties = pullRequestMergedTrigger.triggerProperties("triggerKeyParameter")

    then:
    properties['$class'] == 'GenericTrigger'
    properties['regexpFilterExpression'] == '^closed:triggerKeyParameter:true$'
    properties['token'] == 'triggerKeyParameter'
  }
}
