package gov.ca.cwds.jenkins.semver

import spock.lang.Specification

class SemVerSpecification extends Specification {
  class PipeLineScript {
  }

  def "#newTag with label passed in"() {
    given: "a new Semver"
    def pipeline = Stub(PipeLineScript)
    def semVer = new SemVer(pipeline)
    def tagFetcher = Mock(TagFetcher)
    semVer.tagFetcher = tagFetcher
    def newTagGenerator = Mock(NewTagGenerator)
    semVer.newTagGenerator = newTagGenerator

    when:
    semVer.newTag('minor')

    then:
    1 * tagFetcher.getTags() >> ["1.1.1", "0.3.4"]
    1 * newTagGenerator.newTag(["1.1.1", "0.3.4"], IncrementTypes.MINOR)
  }

  def "#newTag with empty label"() {
    given: "a new Semver"
    def pipeline = Stub(PipeLineScript)
    def semVer = new SemVer(pipeline)
    def tagFetcher = Mock(TagFetcher)
    semVer.tagFetcher = tagFetcher
    def newTagGenerator = Mock(NewTagGenerator)
    semVer.newTagGenerator = newTagGenerator
    def pullRequestEvent = Mock(PullRequestEvent)
    semVer.pullRequestEvent = pullRequestEvent
    def versionIncrement = Mock(VersionIncrement)
    semVer.versionIncrement = versionIncrement

    when:
    semVer.newTag('')

    then:
    1 * tagFetcher.getTags() >> ["1.1.1", "0.3.4"]
    1 * pullRequestEvent.getEvent() >> [labels: [[name: 'major']]]
    1 * versionIncrement.increment(['major']) >> IncrementTypes.MAJOR
    1 * newTagGenerator.newTag(["1.1.1", "0.3.4"], IncrementTypes.MAJOR)
  }
}
