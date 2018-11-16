package gov.ca.cwds.jenkins.utils

import spock.lang.Specification

class ProjectUtilsSpecification extends Specification {

  def "processStageParameters correctly converts a closure with a stage body to a parameter map"() {
    given:
    def stageBody = {
      branch = 'master'
      command = 'git pull'
    }

    when:
    def stageParameters = ProjectUtils.processStageParameters(stageBody)

    then:
    assert stageParameters.branch == 'master'
    assert stageParameters.command == 'git pull'
  }
}
