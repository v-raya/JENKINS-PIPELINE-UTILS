package gov.ca.cwds.jenkins.semver

class LabelChecker {
  def script

    def LabelChecker(script) {
      this.script = script
    }

  def check(projectName) {
    def pullRequestUrl = "https://api.github.com/repos/ca-cwds/${projectName}/issues/${script.env.ghprbPullId}/labels"
    def response = pullRequestUrl.toURL().text
    def labels = script.readJSON(text: response).collect { it.name }
    new VersionIncrement().increment(labels)
  }
}
