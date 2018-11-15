package gov.ca.cwds.jenkins.semver

import spock.lang.Specification

class LabelCheckerSpecification extends Specification {
  class PipeLineScript {
    Environment env

    def readJSON(hash) {
    }

    def PipeLineScript() {
      this.env = new Environment()
    }
  }

  class Environment {
    String ghprbPullId

    def Environment() {
    }
  }

  def "#check with proper labels"() {
    given: "a label checker"
    def environment = Mock(Environment)
    def pipeline = Mock(PipeLineScript)
    def labelChecker = new LabelChecker(pipeline)
    def org.codehaus.groovy.runtime.GStringImpl anyString = GroovyMock(global: true)
    def url = GroovyMock(URL)
    def responseText = '[{"name":"minor"}]'

    when: "checking with a good label"
    def increment = labelChecker.check("dashboard")

    then: "it does not throw and error and returns the increment"
    1 * pipeline.env >> environment
    1 * environment.ghprbPullId >> 122
    1 * anyString.toURL() >> url
    1 * url.text >> responseText
    1 * pipeline.readJSON([text: responseText]) >> [[name: 'minor']]
    increment == IncrementTypes.MINOR
  }

  def "#check without proper labels"() {
    given: "a label checker"
    def environment = Mock(Environment)
    def pipeline = Mock(PipeLineScript)
    def labelChecker = new LabelChecker(pipeline)
    def org.codehaus.groovy.runtime.GStringImpl anyString = GroovyMock(global: true)
    def url = GroovyMock(URL)
    def responseText = '[]'

    when: "checking with no label"
    labelChecker.check("dashboard")

    then: "it throws an exception"
    1 * pipeline.env >> environment
    1 * environment.ghprbPullId >> 122
    1 * anyString.toURL() >> url
    1 * url.text >> responseText
    1 * pipeline.readJSON([text: responseText]) >> []
    def error = thrown(Exception)
    error.message == "No labels found. Please label PR with 'major', 'minor', or 'patch'"
  }
}
