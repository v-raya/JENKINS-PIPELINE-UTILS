package gov.ca.cwds.jenkins.semver

class VersionIncrement {
  def increment(labels) {
    println "HUH"
    def versionIncrement
    def versionIncrementsFound = 0

    labels.each { label ->
      switch(label) {
        case "patch":
          versionIncrement = IncrementTypes.PATCH
          versionIncrementsFound++
          break
      }
    }

    versionIncrement
  }
}
