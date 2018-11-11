package gov.ca.cwds.jenkins.semver

class NewTagGenerator {
  def script

  NewTagGenerator(script) {
    this.script = script
  }

  def newTag(tags, increment) {
    def latestTag = mostRecentVersion(tags)
    script.echo "Latest Tag: ${latestTag}"
    println "latestTag: ${latestTag}"
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

  private String mostRecentVersion(tags) {
    tags.max { a, b ->
      def versionA = a.tokenize('.')
      def versionB = b.tokenize('.')
      def commonIndices = Math.min(versionA.size(), versionB.size())
      for (int index = 0; index < commonIndices; ++index) {
        def numberA = versionA[index].toInteger()
        def numberB = versionB[index].toInteger()
        if (numberA != numberB) {
          return numberA <=> numberB
        }
      }
      versionA.size() <=> versionB.size()
    }
  }
}
