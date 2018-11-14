package gov.ca.cwds.jenkins

import spock.lang.Specification
import gov.ca.cwds.jenkins.common.ProjectTypesDeterminer
import gov.ca.cwds.jenkins.common.ProjectTypes
import gov.ca.cwds.jenkins.docker.Docker

class StaticAnalyzerSpecification extends Specification {

  class PipelineScript {
      def withSonarQubeEnv(name, closure) {
      }
  }

  def "#lint javascript project linted properly"() {
    given:
    def projectTypesDeterminer = Stub(ProjectTypesDeterminer)
    projectTypesDeterminer.determineProjectTypes(_) >> [ProjectTypes.JAVASCRIPT]
    def pipelineScript = Stub(PipelineScript)  
    def docker = Mock(Docker)

    def staticAnalyzer = new StaticAnalyzer(projectTypesDeterminer, 'some_path', docker, pipelineScript)
  
    when:
    staticAnalyzer.lint()

    then:
    1 * docker.withTestingImage('npm run lint')
  }

  def "#lint ruby project linted properly"() {
    given:
    def projectTypesDeterminer = Stub(ProjectTypesDeterminer)
    projectTypesDeterminer.determineProjectTypes(_) >> [ProjectTypes.RUBY]
    def pipelineScript = Stub(PipelineScript)
    def docker = Mock(Docker)

    def staticAnalyzer = new StaticAnalyzer(projectTypesDeterminer, 'some_path', docker, pipelineScript)
  
    when:
    staticAnalyzer.lint()

    then:
    1 * docker.withTestingImage('rubocop')
  }

  def "#lint java project linted properly"() {
    given:
    def projectTypesDeterminer = Stub(ProjectTypesDeterminer)
    projectTypesDeterminer.determineProjectTypes(_) >> [ProjectTypes.JAVA]
    def pipelineScript = Mock(PipelineScript)
    def docker = Stub(Docker)

    def staticAnalyzer = new StaticAnalyzer(projectTypesDeterminer, 'some_path', docker, pipelineScript)
  
    when:
    staticAnalyzer.lint()

    then:
    1 * pipelineScript.withSonarQubeEnv('Core-SonarQube', _ as Closure)
  }  
}