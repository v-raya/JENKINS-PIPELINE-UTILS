package gov.ca.cwds.jenkins
import gov.ca.cwds.jenkins.docker.Docker
import gov.ca.cwds.jenkins.common.BuildMetadata
import gov.ca.cwds.jenkins.common.ProjectTypes

class StaticAnalyzer {
  Docker docker
  def script

  StaticAnalyzer(docker, script) {
    this.docker = docker
    this.script = script
  }

  def lint(buildMetadata) {
    def projectTypes = buildMetadata.projectTypes()
    if( projectTypes.contains(ProjectTypes.JAVA) ) {
      script.withSonarQubeEnv('Core-SonarQube') {
			  buildInfo = rtGradle.run buildFile: 'build.gradle', switches: '--info', tasks: 'sonarqube'
      }  
    }
    if( projectTypes.contains(ProjectTypes.JAVASCRIPT) ) {
      docker.withTestingImage('npm run lint', buildMetadata)
    }
    if( projectTypes.contains(ProjectTypes.RUBY) ) {
      docker.withTestingImage('rubocop', buildMetadata)
    }
  }
}
