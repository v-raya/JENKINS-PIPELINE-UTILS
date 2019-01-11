package gov.ca.cwds.jenkins.semver

class TagFetcher {
  def script

  def TagFetcher(script) {
    this.script = script
  }

  def getTags(tagPrefix = '') {
    def rawTags = script.sh(script: "git tag", returnStdout: true)
    def regex = tagPrefix ? /$tagPrefix\-(\d+\.\d+\.\d+)/ : /(\d+\.\d+\.\d+)/
    def tags = extractSemVerTags(rawTags, regex)
    if (tagPrefix && !tags) {
      // yet no tags with the prefix but could be tags with just versions (no other prefixes) to use further
      tags = extractSemVerTags(rawTags, /(\d+\.\d+\.\d+)/, /\w+\-\d+\.\d+\.\d+/)
    }
    tags
  }

  private def extractSemVerTags(rawTags, regex, exceptRegex = '') {
    def list = rawTags.split("\n").findAll { it =~ regex }
    if (exceptRegex) {
      list = list.findAll { it -> !(it =~ exceptRegex) }
    }
    list.collect { tag ->
      (tag =~ regex).with { it.hasGroup() ? it[0][1] : null }
    }.unique()
  }
}
