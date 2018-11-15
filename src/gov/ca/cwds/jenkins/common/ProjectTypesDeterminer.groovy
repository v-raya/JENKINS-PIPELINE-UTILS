package gov.ca.cwds.jenkins.common

class ProjectTypesDeterminer {
  def script

  def LINT_CONFIGS_RUBY = '.rubocop.yml'
  def LINT_CONFIGS_JAVASCRIPT = '.eslintrc'
  def LINT_CONFIGS_JAVA = 'build.gradle' 

  def ProjectTypesDeterminer(script) {
    this.script = script
  }
  
  def determineProjectTypes(directory) {
    script.echo "the directoy is ${directory}"
    def projectTypes = []
    if(isJavaProject(directory)) {
      projectTypes.add(ProjectTypes.JAVA)
    }
    if(isJavascriptProject(directory)) {
      projectTypes.add(ProjectTypes.JAVASCRIPT)
    }
    if(isRubyProject(directory)) {
      projectTypes.add(ProjectTypes.RUBY)
    }

    projectTypes
  }

  private isJavaProject(directory) {
    def filename = "${directory}/${LINT_CONFIGS_JAVA}"
    fileExists(filename)
  }

  private isJavascriptProject(directory) {
    def filename = "${directory}/${LINT_CONFIGS_JAVASCRIPT}"
    fileExists(filename)
  }

  private isRubyProject(directory) {
    def filename = "${directory}/${LINT_CONFIGS_RUBY}"
    fileExists(filename)
  }

  private fileExists(filename) {
    script.sh(script: "ls -al ${filename}", returnStatus: true) == 0
  }
}
