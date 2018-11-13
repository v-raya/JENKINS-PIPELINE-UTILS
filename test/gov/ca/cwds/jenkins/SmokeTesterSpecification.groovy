package gov.ca.cwds.jenkins

import spock.lang.Specification

class SmokeTesterSpecification extends Specification {

  class PipeLineScript {
    def build(hash) {
    }

    def PipeLineScript() { }
  }

  def "#runSmokeTest"() {

    given:
    def pipeline = Mock(PipeLineScript)
    def passed = './test/resources/smoketest/passed.sh'
    def failed = './test/resources/smoketest/failed.sh'
    def smokeTester = new SmokeTester(pipeline)

    when:
    def smokePassed = smokeTester.runSmokeTest(passed)
    def smokeFailed = smokeTester.runSmokeTest(failed)

    then:
    smokePassed == "smoke test passed"
    smokeFailed == "smoke test failed"

  }
}
