package gov.ca.cwds.jenkins

import spock.lang.Specification

class NewSemVerSpecification extends Specification {

  def "a label of minor"() {
    given:
    def newSemVer = new NewSemVer()
    def version = 'minor'

    when:
    def newVersion = newSemVer.getUpdatedTag(version)

    then:
    newVersion == '0.0.1'
  }
}
