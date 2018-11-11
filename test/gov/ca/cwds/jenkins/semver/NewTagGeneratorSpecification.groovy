package gov.ca.cwds.jenkins.semver

import spock.lang.Specification
import spock.lang.Unroll

class NewTagGeneratorSpecification extends Specification {

  @Unroll("For #tags with increment #increment is #expected")
  def "#newTag"() {
    setup:
    def newTagGenerator = new NewTagGenerator()

    expect:
    newTagGenerator.newTag(tags, increment) == expected

    where:
    tags | increment || expected
    ['0.1.0','2.1.0'] | IncrementTypes.MAJOR || "3.0.0"
    ['0.1.0','2.1.0'] | IncrementTypes.MINOR || "2.2.0"
    ['0.1.0','2.1.0'] | IncrementTypes.PATCH || "2.1.1"
    ['100.0.1','99.1.0', '1.1.999'] | IncrementTypes.MAJOR || "101.0.0"
    ['100.0.1','99.1.0', '1.1.999'] | IncrementTypes.MINOR || "100.1.0"
    ['100.0.1','99.1.0', '1.1.999'] | IncrementTypes.PATCH || "100.0.2"
    ['0.0.0', '1.2.9', '1.3.0'] | IncrementTypes.PATCH || "1.3.1"
['0.0.0', '1.0.0', '1.0.1', '1.0.2', '1.0.3', '1.0.4', '1.0.5', '1.1.0', '1.1.1', '1.2.0', '1.2.1', '1.2.10', '1.2.11', '1.2.12', '1.2.13', '1.2.14', '1.2.15', '1.2.16', '1.2.17', '1.2.18', '1.2.19', '1.2.2', '1.2.20', '1.2.21', '1.2.22', '1.2.23', '1.2.24', '1.2.25', '1.2.26', '1.2.27', '1.2.28', '1.2.29', '1.2.3', '1.2.30', '1.2.4', '1.2.5', '1.2.6', '1.2.7', '1.2.8', '1.2.9', '1.3.0'] | IncrementTypes.PATCH || '1.3.1'
  }
}
