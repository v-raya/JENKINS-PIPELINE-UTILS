package gov.ca.cwds.jenkins.semver

class TagPrefixFinder {

  def tagPrefixes

  TagPrefixFinder(List tagPrefixes) {
    this.tagPrefixes = tagPrefixes
  }

  def find(labels) {
    def foundTagPrefixes = labels.findAll { label -> tagPrefixes.contains(label) }
    if(foundTagPrefixes.size() > 1) {
      throw new Exception('More than one label with tag prefix found. Please label PR with only one of ' + tagPrefixes)
    }
    if(foundTagPrefixes.isEmpty()) {
      throw new Exception('No labels with tag prefix found. Please label PR with one of ' + tagPrefixes)
    }
    foundTagPrefixes[0]
  }
}
