package gov.ca.cwds.jenkins

import gov.ca.cwds.jenkins.docker.Docker

class StaticAnalyzer {
  def script
  def docker

  def LINT_CONFIGS_RUBY = '.rubocop.yml'
  def LINT_CONFIGS_JAVASCRIPT = '.eslintrc'
  def LINT_CONFIGS_JAVA = 'build.gradle' 

  StaticAnalyzer(script, docker) {
    this.script = script
    this.docker = docker
  }

  def lint() {
    if(isRubyProject()) {
      script.echo 'Running rubocop'
      docker.withTestingImage(lintRubyClosure)
    } 
    if(isJavascriptProject()) {
      script.echo 'Running eslint'
      docker.withTestingImage(lintJavascriptClosure)
    } 
    if(isJavaProject()) {
      script.echo 'Running sonarQube'
      withSonarQubeEnv('Core-SonarQube') {
			  buildInfo = rtGradle.run buildFile: 'build.gradle', switches: '--info', tasks: 'sonarqube'
      }  
    } 
  }

  def isRubyProject() {
    return containsFile(LINT_CONFIGS_RUBY)
  }

  def isJavascriptProject() {
    return containsFile(LINT_CONFIGS_JAVASCRIPT) || packageJsonContainsEslintConfig()
  }

  def isJavaProject() {
    return containsFile(LINT_CONFIGS_JAVA)
  }

  def containsFile(configFileName) {
    def command = "ls -al ${script.env.WORKSPACE}/${configFileName} 2>/dev/null"
    return !command.execute().text.trim().isEmpty()
  }

  def packageJsonContainsEslintConfig() {
    def searchPackageJsonCommand = "grep eslintConfig ${script.env.WORKSPACE}/package.json 2>/dev/null"
    return !searchPackageJsonCommand.execute().text.trim().isEmpty()
  }

  def lintJavascriptClosure = { String containerId ->
      script.sh "docker exec -t ${containerId} npm run lint"
    }

  def lintRubyClosure = { String containerId ->
      script.sh "docker exec -t ${containerId} rubocop"
    }
}