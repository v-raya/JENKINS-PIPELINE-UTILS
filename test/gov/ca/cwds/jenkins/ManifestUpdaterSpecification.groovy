package gov.ca.cwds.jenkins

import spock.lang.Specification

class ManifestUpdaterSpecification extends Specification {
  class PipeLineScript {

    def sshagent(hash, closure) { }

    def sh(hash) { }

    def git(hash) { }

    def readYaml(hash) { }

    def writeYaml(hash) { }

    def PipeLineScript() { }
  }

  def "#update writes to the yaml file"() {
    given: "a manifest updater"
    def PipeLineScript pipeline = Mock(PipeLineScript)
    def manifestUpdater = new ManifestUpdater(pipeline)

    when: "updating dashboard in preint"
    manifestUpdater.update("dashboard", "preint", "cr-01", "1.3.0")

    then: "it updates the file and commits the change"
    1 * pipeline.git([branch: "master", credentialsId: "cr-01", url: "git@github.com:ca-cwds/cws-cares.git"])
    1 * pipeline.readYaml([file: "preint.yaml"]) >> [dashboard: "1.2.4", cans: "1.4.5"]
    1 * pipeline.sh("rm preint.yaml")
    1 * pipeline.writeYaml([file: "preint.yaml", data: [dashboard: "1.3.0", cans: "1.4.5"]])
    1 * pipeline.sshagent(credentials: ["cr-01"], _ as Closure)
  }

  def "#commitVersionInCares commits the update"() {
    given: "a manifest updater"
    def PipeLineScript pipeline = Mock(PipeLineScript)
    def manifestUpdater = new ManifestUpdater(pipeline)

    when: "commiting the change in CARES"
    manifestUpdater.commitVersionInCares("dashboard", "1.3.0", "preint")

    then: "it commits as the Jenkins user"
    1 * pipeline.sh([script: "git status --porcelain", returnStdout: true]) >> "?? Readme.md"
    1 * pipeline.sh([script: "git config --global user.email cwdsdoeteam@osi.ca.gov"])
    1 * pipeline.sh([script: "git config --global user.name 'Jenkins'"])
    1 * pipeline.sh([script: "git commit -am \"Update dashboard to 1.3.0 from Jenkins on preint :octocat:\""])
    1 * pipeline.sh([script: "git push origin master"])
  }

  def "#commitVersionInCares does nothing if the version hasn't changed"() {
    given: "a manifest updater"
    def PipeLineScript pipeline = Mock(PipeLineScript)
    def manifestUpdater = new ManifestUpdater(pipeline)

    when: "commiting the change in CARES"
    manifestUpdater.commitVersionInCares("dashboard", "1.2.4", "preint")

    then: "it noops"
    1 * pipeline.sh([script: "git status --porcelain", returnStdout: true]) >> null
    0 * pipeline.sh(_)
  }
}
