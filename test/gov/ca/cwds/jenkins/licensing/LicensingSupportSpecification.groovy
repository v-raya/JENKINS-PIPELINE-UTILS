package gov.ca.cwds.jenkins.licensing

import spock.lang.Specification

class LicensingSupportSpecification extends Specification {

  class PipeLineScript {
    PipeLineScript() {
    }

    def sshagent(hash, closure) {
      closure()
    }

    def sh(hash) {}

    def echo(String) {}
  }

  class RuntimeGradle {
    RuntimeGradle() {
    }

    def run(map) {
    }
  }

  def "When licensing support is asked to update the license report for a non-master branch then skip license report generation"() {
    given: 'a pipeline'
    def pipeline = Mock(PipeLineScript)

    and: 'a licensing support instance which is the class under test'
    def licensingSupport = new LicensingSupport(pipeline)

    when: 'it is asked to update the license report for a non-master branch'
    licensingSupport.updateLicenseReport('myTempBranch', 'credentials-id')

    then: 'it will not invoke any command for report generation'
    0 * pipeline.sh('./gradlew downloadLicenses')
    0 * pipeline.sh('yarn licenses-report')

    and: 'it will print a message about that updating license report is skipped'
    1 * pipeline.echo('Not working with the master branch. Skipping Update License Report for the other branch.')
  }

  def "When licensing support can't detect Licensing Support Type for a Java project then Exception is thrown"() {
    given: 'a pipeline'
    def pipeline = Stub(PipeLineScript)

    and: 'it is running for a Java project'
    pipeline.sh([script: 'ls -al ./build.gradle', returnStatus: true]) >> 0

    and: 'Hierynomus License gradle plugin is not applied'
    pipeline.sh([script: 'grep -c "com.github.hierynomus.license" build.gradle', returnStatus: true]) >> 1

    and: 'a licensing support instance which is the class under test'
    def licensingSupport = new LicensingSupport(pipeline)

    when: 'it is asked to update the license report for the master branch'
    licensingSupport.updateLicenseReport('master', 'credentials-id')

    then: 'it will throw an exception with a message that no known licensing support is found'
    def exception = thrown(Exception)
    exception.message == 'No known Licensing Support is found in the project'
  }

  def "When licensing support can't detect Licensing Support Type for a Ruby project then Exception is thrown"() {
    given: 'a pipeline'
    def pipeline = Stub(PipeLineScript)

    and: 'it is running for a Ruby project'
    pipeline.sh([script: 'ls -al ./.ruby-version', returnStatus: true]) >> 0

    and: 'License Finder plugin is not applied'
    pipeline.sh([script: 'grep -c "license_finder" package.json', returnStatus: true]) >> 1

    and: 'a licensing support instance which is the class under test'
    def licensingSupport = new LicensingSupport(pipeline)

    when: 'it is asked to update the license report for the master branch'
    licensingSupport.updateLicenseReport('master', 'credentials-id')

    then: 'it will throw an exception with a message that no known licensing support is found'
    def exception = thrown(Exception)
    exception.message == 'No known Licensing Support is found in the project'
  }

  def "When licensing support can't run ssh git command then Exception is thrown"() {
    given: 'a pipeline'
    def pipeline = Spy(PipeLineScript)

    and: 'it is running for a Java project'
    pipeline.sh([script: 'ls -al ./build.gradle', returnStatus: true]) >> 0

    and: 'Hierynomus License gradle plugin is applied'
    pipeline.sh([script: 'grep -c "com.github.hierynomus.license" build.gradle', returnStatus: true]) >> 0

    and: 'it cannot successfully run ssh git command'
    pipeline.sh([script: 'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" git config --global user.name Jenkins', returnStatus: true]) >> 1

    and: 'a licensing support instance which is the class under test'
    def licensingSupport = new LicensingSupport(pipeline)

    when: 'it is asked to update the license report for the master branch'
    licensingSupport.updateLicenseReport('master', 'credentials-id')

    then: 'it will throw an exception with a message that ssh command is failed'
    def exception = thrown(Exception)
    exception.message == "ssh command 'git config --global user.name Jenkins' failed"
  }

  def "When licensing support is asked to update license report for a back-end project with gradle hierynomus license plugin then gradlew is called to generate license report"() {
    given: 'a pipeline'
    def pipeline = Mock(PipeLineScript)

    and: 'it is running for a Java project'
    pipeline.sh([script: 'ls -al ./build.gradle', returnStatus: true]) >> 0

    and: 'Hierynomus License gradle plugin is applied'
    pipeline.sh([script: 'grep -c "com.github.hierynomus.license" build.gradle', returnStatus: true]) >> 0

    and: 'it can successfully run ssh git commands'
    pipeline.sh([script: 'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" git config --global user.name Jenkins', returnStatus: true]) >> 0
    pipeline.sh([script: 'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" git config --global user.email cwdsdoeteam@osi.ca.gov', returnStatus: true]) >> 0
    pipeline.sh([script: 'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" git push --set-upstream origin master', returnStatus: true]) >> 0

    and: 'a licensing support instance which is the class under test'
    def licensingSupport = new LicensingSupport(pipeline)

    when: 'it is asked to update the license report for the master branch'
    licensingSupport.updateLicenseReport('master', 'credentials-id')

    then: "Hierynomus License gradle plugin is invoked using the project's gradle wrapper"
    1 * pipeline.sh('./gradlew downloadLicenses')
    0 * pipeline.sh('yarn licenses-report')

    and: "generated license report is copied to the project's 'legal' folder"
    1 * pipeline.sh([script: 'mkdir legal', returnStatus: true])
    1 * pipeline.sh('cp build/reports/license/* legal')

    and: 'a set of ssh git commands is executed to push possible changes of license report to the master branch'
    1 * pipeline.sshagent([credentials: ['credentials-id']], _)

    and: 'corresponding messages are printed'
    1 * pipeline.echo('Detected Licensing Support Type: Gradle Hierynomus License Plugin')
    1 * pipeline.echo('Generating License Information')
    1 * pipeline.echo('Updating License Information')
  }

  def "When licensing support is asked to update license report for a back-end project with gradle hierynomus license plugin and it RuntimeGradle is provided then the GradleRuntime is used to generate license report"() {
    given: 'a pipeline'
    def pipeline = Mock(PipeLineScript)

    and: 'it is running for a Java project'
    pipeline.sh([script: 'ls -al ./build.gradle', returnStatus: true]) >> 0

    and: 'Hierynomus License gradle plugin is applied'
    pipeline.sh([script: 'grep -c "com.github.hierynomus.license" build.gradle', returnStatus: true]) >> 0

    and: 'it can successfully run ssh git commands'
    pipeline.sh([script: 'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" git config --global user.name Jenkins', returnStatus: true]) >> 0
    pipeline.sh([script: 'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" git config --global user.email cwdsdoeteam@osi.ca.gov', returnStatus: true]) >> 0
    pipeline.sh([script: 'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" git push --set-upstream origin master', returnStatus: true]) >> 0

    and: 'runtime gradle is provided'
    def runtimeGradle = Mock(RuntimeGradle)

    and: 'a licensing support instance which is the class under test'
    def licensingSupport = new LicensingSupport(pipeline)

    when: 'it is asked to update the license report for the master branch'
    licensingSupport.updateLicenseReport('master', 'credentials-id', runtimeGradle)

    then: 'Hierynomus License gradle plugin is invoked using the provided runtime gradle'
    1 * runtimeGradle.run([buildFile: 'build.gradle', tasks: 'downloadLicenses'])
    0 * pipeline.sh('./gradlew downloadLicenses')
    0 * pipeline.sh('yarn licenses-report')

    and: "generated license report is copied to the project's 'legal' folder"
    1 * pipeline.sh([script: 'mkdir legal', returnStatus: true])
    1 * pipeline.sh('cp build/reports/license/* legal')

    and: 'a set of ssh git commands is executed to push possible changes of license report to the master branch'
    1 * pipeline.sshagent([credentials: ['credentials-id']], _)

    and: 'corresponding messages are printed'
    1 * pipeline.echo('Detected Licensing Support Type: Gradle Hierynomus License Plugin')
    1 * pipeline.echo('Generating License Information')
    1 * pipeline.echo('Updating License Information')
  }

  def "When licensing support is asked to update license report for a front-end project with license finder plugin then the plugin is used to generate license report"() {
    given: 'a pipeline'
    def pipeline = Mock(PipeLineScript)

    and: 'it is running for a Ruby project'
    pipeline.sh([script: 'ls -al ./build.gradle', returnStatus: true]) >> 1
    pipeline.sh([script: 'ls -al ./.ruby-version', returnStatus: true]) >> 0

    and: 'license finder plugin is applied'
    pipeline.sh([script: 'grep -c "license_finder" package.json', returnStatus: true]) >> 0

    and: 'it can successfully run ssh git commands'
    pipeline.sh([script: 'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" git config --global user.name Jenkins', returnStatus: true]) >> 0
    pipeline.sh([script: 'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" git config --global user.email cwdsdoeteam@osi.ca.gov', returnStatus: true]) >> 0
    pipeline.sh([script: 'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" git push --set-upstream origin master', returnStatus: true]) >> 0

    and: 'a licensing support instance which is the class under test'
    def licensingSupport = new LicensingSupport(pipeline)

    when: 'it is asked to update the license report for the master branch'
    licensingSupport.updateLicenseReport('master', 'credentials-id')

    then: 'Ruby license finder plugin is invoked'
    1 * pipeline.sh('yarn licenses-report')
    0 * pipeline.sh('./gradlew downloadLicenses')

    and: 'no additional file operations are performed'
    0 * pipeline.sh([script: 'mkdir legal', returnStatus: true])
    0 * pipeline.sh('cp build/reports/license/* legal')

    and: 'a set of ssh git commands is executed to push possible changes of license report to the master branch'
    1 * pipeline.sshagent([credentials: ['credentials-id']], _)

    and: 'corresponding messages are printed'
    1 * pipeline.echo('Detected Licensing Support Type: Ruby License Finder Plugin')
    1 * pipeline.echo('Generating License Information')
    1 * pipeline.echo('Updating License Information')
  }
}
