package gov.ca.cwds.jenkins.semver

class NewTagGenerator {
  def newTag(tags, increment) {
    def latestTag = mostRecentVersion(tags)
    def (major, minor, patch) = latestTag.tokenize(".").collect { it as Integer }
    switch (increment) {
      case IncrementTypes.MAJOR:
        major++
        minor = 0
        patch = 0
        break
      case IncrementTypes.MINOR:
        minor++
        patch = 0
        break
      case IncrementTypes.PATCH:
        patch++
        break
    }
    "$major.$minor.$patch"
  }

  private String mostRecentVersion(List versions) {
    versions.sort(false) { a, b ->
      [a, b]*.tokenize('.')*.collect { it as Integer }.with { u, v ->
        [u, v].transpose().findResult { x, y -> x <=> y ?: null } ?: u.size() <=> v.size()
      }
    }.last()
  }
}
