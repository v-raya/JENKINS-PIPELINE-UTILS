package gov.ca.cwds.jenkins.semver

class VersionIncrement {
  def increment(labels) {
    def versionIncrement
    def versionIncrementsFound = 0

    labels.each { label ->
      switch(label) {
        case "major":
          versionIncrement = IncrementTypes.MAJOR
          versionIncrementsFound++
          break
        case "minor":
          versionIncrement = IncrementTypes.MINOR
          versionIncrementsFound++
          break
        case "patch":
          versionIncrement = IncrementTypes.PATCH
          versionIncrementsFound++
          break
      }
    }

    versionIncrement
  }
}
