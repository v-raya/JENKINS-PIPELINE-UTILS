package gov.ca.cwds.jenkins.docker

import spock.lang.Specification

class DockerSpecification extends Specification {

    class PipelineScript {
        def echo(string) {}

        def env = [
            'JOB_NAME': 'myjob',
            'BUILD_ID': '22'
        ] 
    }

    class GlobalDocker {
      
    }


  def "testing image name corect"() {
    given:
    def docker = new Docker(new PipelineScript(), new GlobalDocker())
  
    when:
    def testingImageName = docker.testingImageName()

    then:
    testingImageName == 'cwds/myjob:test-build-22'
  }
}
