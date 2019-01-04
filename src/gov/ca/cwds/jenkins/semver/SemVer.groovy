package gov.ca.cwds.jenkins.semver

class SemVer {
  def script
  TagFetcher tagFetcher
  NewTagGenerator newTagGenerator
  PullRequestEvent pullRequestEvent
  VersionIncrement versionIncrement

  SemVer(script) {
    this.script = script
    this.tagFetcher = new TagFetcher(script)
    this.newTagGenerator = new NewTagGenerator()
    this.pullRequestEvent = new PullRequestEvent(script)
    this.versionIncrement = new VersionIncrement()
  }

  def newTag(label, List tagPrefixes = []) {
    def eventLabels = []
    if (tagPrefixes || !isValidIncrementLabel(label)) {
      def event = pullRequestEvent.getEvent()
      eventLabels = event.labels.collect([]) { it.name }
    }
    String tagPrefix = ''
    if (tagPrefixes) {
      tagPrefix = new TagPrefixFinder(tagPrefixes).find(eventLabels)
    }
    def existingTags = tagFetcher.getTags(tagPrefix)
    label = isValidIncrementLabel(label) ? label.toUpperCase() as IncrementTypes
      : versionIncrement.increment(eventLabels)
    (tagPrefix ? tagPrefix + '-' : '') + newTagGenerator.newTag(existingTags, label)
  }

  private boolean isValidIncrementLabel(label) {
    return label && IncrementTypes.values().collect({ it.toString() }).contains(label.toUpperCase())
  }
}
