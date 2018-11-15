package gov.ca.cwds.jenkins

import spock.lang.Specification

class SmokeTesterSpecification extends Specification {

  class PipeLineScript {

    def error(hash) {}

    def PipeLineScript() {}
  }

  def "#runSmokeTestPasses"() {

    given:
    def pipeline = Mock(PipeLineScript)
    def passed = './test/resources/smoketest/passed.sh'
    def smokeTester = new SmokeTester(pipeline)

    when:
    def smokePassed = smokeTester.runSmokeTest(passed, "test")

    then:
    smokePassed == "smoke test passed"
  }

  def "#runSmokeTestFails"() {

    given:
    def pipeline = Mock(PipeLineScript)
    def failed = './test/resources/smoketest/failed.sh'
    def smokeTester = new SmokeTester(pipeline)

    when:
    smokeTester.runSmokeTest(failed, "test")

    then:
    1 * pipeline.error(_)
  }

}
