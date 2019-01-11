package gov.ca.cwds.jenkins.semver

import spock.lang.Specification

class PullRequestEventSpecification extends Specification {
  class PipeLineScript {
    Environment env

    def readJSON(hash) {
    }

    def PipeLineScript() {
      this.env = new Environment()
    }
  }

  class Environment {
    String pull_request_event

    def Environment() {
      this.pull_request_event = "pr_event"
    }
  }

  def "#getEvent"() {
    def buildArguments

    given:
    def PipeLineScript pipeline = Spy()
    def pullRequestEvent = new PullRequestEvent(pipeline)

    when:
    pullRequestEvent.getEvent()

    then:
    1 * pipeline.readJSON(_) >> { arguments -> buildArguments = arguments[0] }
    buildArguments['text'] == "pr_event"
  }
}
