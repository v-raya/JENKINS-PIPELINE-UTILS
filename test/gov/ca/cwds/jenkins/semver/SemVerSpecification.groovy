package gov.ca.cwds.jenkins.semver

import spock.lang.Specification

class SemVerSpecification extends Specification {
  class PipeLineScript {
    def sh(hash) {
    }
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
    1 * tagFetcher.getTags(_) >> ["1.1.1", "0.3.4"]
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
    1 * tagFetcher.getTags(_) >> ["1.1.1", "0.3.4"]
    1 * pullRequestEvent.getEvent() >> [labels: [[name: 'major']]]
    1 * versionIncrement.increment(['major']) >> IncrementTypes.MAJOR
    1 * newTagGenerator.newTag(["1.1.1", "0.3.4"], IncrementTypes.MAJOR)
  }

  def "#newTag with empty increment label and a tag prefix"() {
    given: "a PR with an increment label and a tag prefix label"
    def pullRequestEvent = Mock(PullRequestEvent)
    pullRequestEvent.getEvent() >> [labels: [[name: 'minor'], [name: 'lis']]]

    and: "a new Semver"
    def pipeline = Stub(PipeLineScript)
    // only labels prefixed with 'lis-' will be taken into account because of the label in PR
    pipeline.sh(_) >> "cwscms-1.0.4\nlis-0.3.4\nlis-0.1.2"
    def semVer = new SemVer(pipeline)
    semVer.pullRequestEvent = pullRequestEvent

    when: "expecting an increment label and one of the tag prefix labels in the PR"
    def newTag = semVer.newTag('', ['cwscms', 'lis', 'capu'])

    then:
    newTag == 'lis-0.4.0'
  }

  def "#newTag with passed increment label and a tag prefix"() {
    given: "a PR with a tag prefix label"
    def pullRequestEvent = Mock(PullRequestEvent)
    pullRequestEvent.getEvent() >> [labels: [[name: 'lis']]]

    and: "a new Semver"
    def pipeline = Stub(PipeLineScript)
    // only labels without any possible prefix will be taken into account
    // because there is no tag with the prefix that is defined in a label of the PR
    pipeline.sh(_) >> "1.0.4\njobs_0.3.4\ncapu-3.1.2"
    def semVer = new SemVer(pipeline)
    semVer.pullRequestEvent = pullRequestEvent

    when: "using explicit increment label and expecting one of the tag prefix labels in the PR"
    def newTag = semVer.newTag('minor', ['cwscms', 'lis', 'capu'])

    then:
    newTag == 'lis-1.1.0'
  }
}
