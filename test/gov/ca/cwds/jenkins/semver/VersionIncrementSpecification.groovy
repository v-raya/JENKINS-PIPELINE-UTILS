package gov.ca.cwds.jenkins.semver

import spock.lang.Specification

class VersionIncrementSpecification extends Specification {

  def "#increment with a patch label"() {
    given:
    def versionIncrement = new VersionIncrement()

    when:
    def increment = versionIncrement.increment(["patch"])

    then:
    increment == IncrementTypes.PATCH
  }
}
