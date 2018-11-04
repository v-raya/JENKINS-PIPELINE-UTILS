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

  def "#increment with a minor label"() {
    given:
    def versionIncrement = new VersionIncrement()

    when:
    def increment = versionIncrement.increment(["minor"])

    then:
    increment == IncrementTypes.MINOR
  }

  def "#increment with a major label"() {
    given:
    def versionIncrement = new VersionIncrement()

    when:
    def increment = versionIncrement.increment(["major"])

    then:
    increment == IncrementTypes.MAJOR
  }
}
