package gov.ca.cwds.jenkins
import gov.ca.cwds.jenkins.docker.Docker
import gov.ca.cwds.jenkins.common.ProjectTypesDeterminer
import gov.ca.cwds.jenkins.common.ProjectTypes

class StaticAnalyzer {
  def workspacePath
  Docker docker
  ProjectTypesDeterminer projectTypesDeterminer
  def script

  StaticAnalyzer(projectTypesDeterminer, workspacePath, docker, script) {
    this.projectTypesDeterminer = projectTypesDeterminer
    this.workspacePath = workspacePath
    this.docker = docker
    this.script = script
  }

  def lint() {
    def projectTypes = projectTypesDeterminer.determineProjectTypes(workspacePath);
    if( projectTypes.contains(ProjectTypes.JAVA) ) {
      script.withSonarQubeEnv('Core-SonarQube') {
			  buildInfo = rtGradle.run buildFile: 'build.gradle', switches: '--info', tasks: 'sonarqube'
      }  
    }
    if( projectTypes.contains(ProjectTypes.JAVASCRIPT) ) {
      docker.withTestingImage('npm run lint')
    }
    if( projectTypes.contains(ProjectTypes.RUBY) ) {
      docker.withTestingImage('rubocop')
    }

  }
}
