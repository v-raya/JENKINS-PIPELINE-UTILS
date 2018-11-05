package gov.ca.cwds.jenkins.semver

class TagFetcher {
  def script

  def TagFetcher(script) {
    this.script = script
  }

  def getTags() {
    def rawTags = script.sh(script: "git tag", returnStdout: true)
    rawTags.split("\n").findAll { it =~ /^\d+\.\d+\.\d+$/ }
  }
}
