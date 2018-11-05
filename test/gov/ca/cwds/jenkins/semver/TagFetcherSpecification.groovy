package gov.ca.cwds.jenkins.semver

import spock.lang.Specification

class TagFetcherSpecifiction extends Specification {
  class PipeLineScript {

    def sh(hash) {
    }

    def PipeLineScript() { }
  }

  def "#getTags with only SemVer tags"() {
    given:
    PipeLineScript pipeline = Stub(PipeLineScript)
    pipeline.sh(_) >> "0.1.0\n2.1.32\n1.2.4"
    def tagFetcher = new TagFetcher(pipeline)

    when:
    def tags = tagFetcher.getTags()

    then:
    tags == ["0.1.0", "2.1.32", "1.2.4"]
  }
}
