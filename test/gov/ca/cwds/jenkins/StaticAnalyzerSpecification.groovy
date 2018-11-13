package gov.ca.cwds.jenkins

import spock.lang.Specification

class StaticAnalyzerSpecification extends Specification {

    class PipelineScript {
        def echo(string) {}

        def env = [
            'WORKSPACE': '.'
        ] 
    }

    class Docker {
        def withTestingImage(closure) {}
    }


  def "does not contain file"() {
    given:
    def staticAnalyzer = new StaticAnalyzer(new PipelineScript(), new Docker())
  
    when:
    def containsFile = staticAnalyzer.containsFile('youcannotfindme.file')

    then:
    containsFile == false
  }

    def "does contain file"() {
    given:
    def staticAnalyzer = new StaticAnalyzer(new PipelineScript(), new Docker())
  
    when:
    def containsFile = staticAnalyzer.containsFile('build.gradle')

    then:
    containsFile == true
  }
}
