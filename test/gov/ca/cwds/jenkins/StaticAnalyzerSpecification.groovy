package gov.ca.cwds.jenkins

import spock.lang.Specification
import gov.ca.cwds.jenkins.common.BuildMetadata
import gov.ca.cwds.jenkins.common.ProjectTypes
import gov.ca.cwds.jenkins.docker.Docker

class StaticAnalyzerSpecification extends Specification {

  class PipelineScript {
      def withSonarQubeEnv(name, closure) {
      }
  }

  def "#lint javascript project linted properly"() {
    given:
    def pipelineScript = Stub(PipelineScript)  
    def docker = Mock(Docker)
    def buildMetadata = Stub(BuildMetadata)
    buildMetadata.projectTypes() >> [ProjectTypes.JAVASCRIPT]

    def staticAnalyzer = new StaticAnalyzer(docker, pipelineScript)
  
    when:
    staticAnalyzer.lint(buildMetadata)

    then:
    1 * docker.withTestingImage('npm run lint', buildMetadata)
  }

  def "#lint ruby project linted properly"() {
    given:
    def pipelineScript = Stub(PipelineScript)  
    def docker = Mock(Docker)
    def buildMetadata = Stub(BuildMetadata)
    buildMetadata.projectTypes() >> [ProjectTypes.RUBY]

    def staticAnalyzer = new StaticAnalyzer(docker, pipelineScript)
  
    when:
    staticAnalyzer.lint(buildMetadata)

    then:
    1 * docker.withTestingImage('rubocop',  buildMetadata)
  }

  def "#lint java project linted properly"() {
    given:
    def pipelineScript = Mock(PipelineScript)
    def docker = Stub(Docker)
    def buildMetadata = Stub(BuildMetadata)
    buildMetadata.projectTypes() >> [ProjectTypes.JAVA]
        
    def staticAnalyzer = new StaticAnalyzer(docker, pipelineScript)
  
    when:
    staticAnalyzer.lint(buildMetadata)

    then:
    1 * pipelineScript.withSonarQubeEnv('Core-SonarQube', _ as Closure)
  }  
}
