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
    this.newTagGenerator = new NewTagGenerator(script)
    this.pullRequestEvent = new PullRequestEvent(script)
    this.versionIncrement = new VersionIncrement()
  }

  def newTag(label) {
    def existingTags = tagFetcher.getTags()
    if (!IncrementTypes.values().collect({ it.toString()}).contains(label.toUpperCase())) {
      def event = pullRequestEvent.getEvent()
      def labels = event.labels.collect([]) { it.name }
      label = versionIncrement.increment(labels)
    } else {
      label = label.toUpperCase() as IncrementTypes
    }
    script.echo "Existing Tags: ${existingTags}"
    script.echo "Last tag class: ${existingTags.last().class}"
    newTagGenerator.newTag(existingTags, label)
  }
}
