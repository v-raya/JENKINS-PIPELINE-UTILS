package gov.ca.cwds.jenkins

import spock.lang.Specification

class SmokeTesterSpecification extends Specification {

  class PipeLineScript {

    def error(hash) {}

    def sh(hash) {}
    
    def echo(hash) {}

    def PipeLineScript() {}
  }

  def "#runSmokeTestPasses"() {

    given:
    def PipeLineScript pipeline = Mock(PipeLineScript)
    pipeline.sh (_) >> "smoketest passed"
    def smokeTester = new SmokeTester(pipeline)

    when:
    def testResult = smokeTester.runSmokeTest("passed", "test")

    then:
    0 * pipeline.error(_)
    testResult == "smoke test passed"
  }

  def "#runSmokeTestFails"() {

    given:
    def pipeline = Mock(PipeLineScript)
    def smokeTester = new SmokeTester(pipeline)
    pipeline.sh (_) >> "smoketest failed"

    when:
    smokeTester.runSmokeTest("failed", "test")

    then:
    1 * pipeline.error(_)
  }

}
