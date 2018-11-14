package gov.ca.cwds.jenkins.docker

import spock.lang.Specification

class DockerSpecification extends Specification {
  def jobName = 'myjob'
  def buildId = '22'

  class PipelineScript {
    def sh(hash) {
    }
  }
    
  class GlobalDocker {
    def build(imageName, parameters) {
    }

    def image(imageName) {
    }
  }

  class DockerImage {
    def withRun(closure) {

    }
  }


  def "#createTestingImage implemented correctly"() {
    given:
    def globalDocker = Mock(GlobalDocker)
    def pipelineScript = new PipelineScript()
    def docker = new Docker(jobName, buildId, pipelineScript, globalDocker)
  
    when:
    docker.createTestingImage()

    then:
    1 * globalDocker.build('cwds/myjob:test-build-22', '-f ./docker/test/Dockerfile .')
  }

  def "#removeTestingImage implemented correctly"() {
    given:
    def globalDocker = Stub(GlobalDocker)
    def pipelineScript = Mock(PipelineScript)
    def docker = new Docker(jobName, buildId, pipelineScript, globalDocker)
  
    when:
    docker.removeTestingImage()

    then:
    1 * pipelineScript.sh([script: "docker rmi cwds/myjob:test-build-22", returnStatus: true]) >> 0
  }

  def "#withTestingImage implemented correctly"() {
    given:
    def dockerImage = Mock(DockerImage)
    def globalDocker = Stub(GlobalDocker)
    globalDocker.image(_) >> dockerImage
    
    def pipelineScript = Mock(PipelineScript)
    def docker = new Docker(jobName, buildId, pipelineScript, globalDocker)
  
    when:
    docker.withTestingImage('some_command')

    then:
    1 * dockerImage.withRun(_ as Closure)
  }  
}