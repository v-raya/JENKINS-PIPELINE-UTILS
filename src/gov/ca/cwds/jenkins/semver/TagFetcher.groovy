package gov.ca.cwds.jenkins.semver

class TagFetcher {
  def script

  def TagFetcher(script) {
    this.script = script
  }

  def getTags() {
    def rawTags = script.sh(script: "git tag", returnStdout: true)
    def list = rawTags.split("\n").findAll { it =~ /(^\d+\.\d+\.\d+)/ }
    list.collect { tag ->
       (tag =~ /(^\d+\.\d+\.\d+)/).with { hasGroup() ? it[0][0] : null }
    }.unique()
  }
}
