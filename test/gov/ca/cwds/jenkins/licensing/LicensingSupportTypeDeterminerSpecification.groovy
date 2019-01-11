package gov.ca.cwds.jenkins.licensing

import gov.ca.cwds.jenkins.common.BuildMetadata
import gov.ca.cwds.jenkins.common.ProjectTypes
import spock.lang.Specification

class LicensingSupportTypeDeterminerSpecification extends Specification {

  class PipeLineScript {
    def sh(hash) { }

    def echo(string) { }
  }

  def "When a Java project without the Hierynomus License gradle plugin"() {
    given: 'a pipeline'
    def pipeline = Mock(PipeLineScript)

    and: 'a Java project without the Hierynomus License gradle plugin'
    def buildMetadata = Mock(BuildMetadata)
    buildMetadata.projectTypes() >> [ProjectTypes.JAVA]

    and: 'a LicensingSupportTypeDeterminer instance which is the class under test'
    def licensingSupportTypeDeterminer = new LicensingSupportTypeDeterminer(pipeline)

    when: 'determine the Licensing Support Type'
    def licensingSupportType = licensingSupportTypeDeterminer.determineLicensingSupportType(buildMetadata)

    then: "the 'NONE' Licensing Support Type is determined"
    LicensingSupportType.NONE == licensingSupportType
    1 * pipeline.echo('Detected Licensing Support Type: None')
  }

  def "When a Ruby project without the License Finder plugin"() {
    given: 'a pipeline'
    def pipeline = Mock(PipeLineScript)

    and: 'a Ruby project without the License Finder plugin'
    def buildMetadata = Mock(BuildMetadata)
    buildMetadata.projectTypes() >> [ProjectTypes.RUBY]

    and: 'a LicensingSupportTypeDeterminer instance which is the class under test'
    def licensingSupportTypeDeterminer = new LicensingSupportTypeDeterminer(pipeline)

    when: 'determine the Licensing Support Type'
    def licensingSupportType = licensingSupportTypeDeterminer.determineLicensingSupportType(buildMetadata)

    then: "the 'NONE' Licensing Support Type is determined"
    LicensingSupportType.NONE == licensingSupportType
    1 * pipeline.echo('Detected Licensing Support Type: None')
  }

  def "When a Java project with the Hierynomus License gradle plugin"() {
    given: 'a pipeline'
    def pipeline = Mock(PipeLineScript)

    and: 'a Java project with the Hierynomus License gradle plugin'
    def buildMetadata = Mock(BuildMetadata)
    buildMetadata.projectTypes() >> [ProjectTypes.JAVA]
    pipeline.sh([script: 'grep -c "com.github.hierynomus.license" build.gradle', returnStatus: true]) >> 0

    and: 'a LicensingSupportTypeDeterminer instance which is the class under test'
    def licensingSupportTypeDeterminer = new LicensingSupportTypeDeterminer(pipeline)

    when: 'determine the Licensing Support Type'
    def licensingSupportType = licensingSupportTypeDeterminer.determineLicensingSupportType(buildMetadata)

    then: "the 'GRADLE_HIERYNOMUS_LICENSE' Licensing Support Type is determined"
    LicensingSupportType.GRADLE_HIERYNOMUS_LICENSE == licensingSupportType
    1 * pipeline.echo('Detected Licensing Support Type: Gradle Hierynomus License Plugin')
  }

  def "When a Ruby project with the License Finder plugin"() {
    given: 'a pipeline'
    def pipeline = Mock(PipeLineScript)

    and: 'a Ruby project with the License Finder plugin'
    def buildMetadata = Mock(BuildMetadata)
    buildMetadata.projectTypes() >> [ProjectTypes.RUBY]
    pipeline.sh([script: 'grep -c "license_finder" package.json', returnStatus: true]) >> 0

    and: 'a LicensingSupportTypeDeterminer instance which is the class under test'
    def licensingSupportTypeDeterminer = new LicensingSupportTypeDeterminer(pipeline)

    when: 'determine the Licensing Support Type'
    def licensingSupportType = licensingSupportTypeDeterminer.determineLicensingSupportType(buildMetadata)

    then: "the 'RUBY_LICENSE_FINDER' Licensing Support Type is determined"
    LicensingSupportType.RUBY_LICENSE_FINDER == licensingSupportType
    1 * pipeline.echo('Detected Licensing Support Type: Ruby License Finder Plugin')
  }
}
