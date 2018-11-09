package gov.ca.cwds.jenkins

import spock.lang.Specification

class SmokeTesterSpecification extends Specification {

  class PipeLineScript {
    def build(hash) {
    }

    def PipeLineScript() { }
  }

  def "#runSmokeTest"() {
    def buildArguments

    given:
    def pipeline = Mock(PipeLineScript)
    def passed = './test/resources/smoketest/passed.sh'
    def failed = './test/resources/smoketest/failed.sh'
    def smokeTestPassed = new SmokeTester(pipeline, passed)
    def smokeTestFailed = new SmokeTester(pipeline, failed)
    

    when:
    def smokePassed = smokeTestPassed.runSmokeTest(passed)
    def smokeFailed = smokeTestFailed.runSmokeTest(failed)

    then:
    smokePassed == "smoke test passed"
    smokeFailed == "smoke test failed"

  }
}