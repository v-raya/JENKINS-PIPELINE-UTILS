package gov.ca.cwds.jenkins.semver

import spock.lang.Specification

class TagFetcherSpecification extends Specification {
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

  def "#getTags with non semVer tags"() {
    given:
    PipeLineScript pipeline = Stub(PipeLineScript)
    pipeline.sh(_) >> "0.1.0\ncsec_initial_load\n1.0\nes_6x__1"
    def tagFetcher = new TagFetcher(pipeline)

    when:
    def tags = tagFetcher.getTags()

    then:
    tags == ["0.1.0"]
  }

  def "#getTags with SemVer tags that have extra metadata"() {
    given:
    PipeLineScript pipeline = Stub(PipeLineScript)
    pipeline.sh(_) >> "0.2.4.567\n1.2.3_1098-RC\njobs_0.60.185"
    def tagFetcher = new TagFetcher(pipeline)

    when:
    def tags = tagFetcher.getTags()

    then:
    tags == ["0.2.4", "1.2.3", "0.60.185"]
  }

  def "#getTags with SemVer tags that have duplicates"() {
    given:
    PipeLineScript pipeline = Stub(PipeLineScript)
    pipeline.sh(_) >> "0.2.4.567\n1.2.3_1098-RC\n0.2.4.568"
    def tagFetcher = new TagFetcher(pipeline)

    when:
    def tags = tagFetcher.getTags()

    then:
    tags == ["0.2.4", "1.2.3"]
  }
}
