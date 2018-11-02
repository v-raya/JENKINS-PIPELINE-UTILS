package gov.ca.cwds.jenkins

import spock.lang.Specification

class PullRequestMergedTriggerSpecification extends Specification {
  class PipeLineScript {
    def build(hash) {
    }

    def PipeLineScript() { }
  }

  def "#triggerProperties returns a map with a regex based on trigger key"() {
    given:
    def pipeline = Mock(PipeLineScript)
    def pullRequestMergedTrigger = new PullRequestMergedTrigger(pipeline)

    when:
    def properties = pullRequestMergedTrigger.triggerProperties("triggerKeyParameter")

    then:
    properties['$class'] == 'GenericTrigger'
    properties['regexpFilterExpression'] == '^closed:triggerKeyParameter:true$'
  }
}
