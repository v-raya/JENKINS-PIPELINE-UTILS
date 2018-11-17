package gov.ca.cwds.jenkins.licensing

import spock.lang.Specification

class LicensingSupportSpecification extends Specification {

  class PipeLineScript {
    PipeLineScript() {
    }

    def sh(String script) {
      actualValues.calledShScripts.add(script)
      return behaviour && behaviour.sh ? behaviour.sh[script] : 1
    }

    def sh(Map params) {
      return this.sh(params.script)
    }

    def echo(String msg) {
      actualValues.echoedMessages.add(msg)
    }

    def readFile(Map params) {
      return behaviour.readFileResult
    }

    def writeFile(Map params) {
      actualValues.textPassedToWriteFile = params.text
    }

    def sshagent(Map params, closure) {
      actualValues.usedCredentialsId = params.credentials[0]
      closure()
    }
  }

  class GradleRuntime {
    def GradleRuntime() {
    }

    def run(Map parameters) {
      actualValues.lastGradleRuntimeParameters = parameters
    }
  }

  def behaviour = [
    sh            : [:], // map of script -> result values where script is a parameter of the pipeline.sh() method
    readFileResult: ''
  ]

  def actualValues = [
    calledShScripts            : [] as Set,
    echoedMessages             : [] as Set,
    textPassedToWriteFile      : null,
    usedCredentialsId          : null,
    lastGradleRuntimeParameters: null
  ]

  def getSshGitCommand(gitCommand) {
    'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" ' + gitCommand
  }

  final def SSH_GIT_CONFIG_USER = getSshGitCommand('git config --global user.name Jenkins')
  final def SSH_GIT_CONFIG_EMAIL = getSshGitCommand('git config --global user.email cwdsdoeteam@osi.ca.gov')
  final def SSH_GIT_ADD_LEGAL = getSshGitCommand('git add legal')
  final def SSH_GIT_COMMIT = getSshGitCommand('git commit -m "updated license info"')
  final def SSH_GIT_PUSH = getSshGitCommand('git push --set-upstream origin master')

  def setUpGitSshCommands() {
    behaviour.sh[SSH_GIT_CONFIG_USER] = 0
    behaviour.sh[SSH_GIT_CONFIG_EMAIL] = 0
    behaviour.sh[SSH_GIT_ADD_LEGAL] = 0
    behaviour.sh[SSH_GIT_COMMIT] = 0
    behaviour.sh[SSH_GIT_PUSH] = 0
  }

  // assertion methods

  def isLastShScriptCalled(expectedShScript) {
    expectedShScript == actualValues.calledShScripts.last()
  }

  def isShScriptCalled(expectedShScript) {
    actualValues.calledShScripts.contains(expectedShScript)
  }

  def isMessageEchoed(expectedMessage) {
    actualValues.echoedMessages.contains(expectedMessage)
  }

  def isTextPassedToWriteFile(expectedText) {
    expectedText == actualValues.textPassedToWriteFile
  }

  def isCredentialsIdUsed(expectedCredentialsId) {
    expectedCredentialsId == actualValues.usedCredentialsId
  }

  def areLastGradleRuntimeParameters(expectedParameters) {
    expectedParameters == actualValues.lastGradleRuntimeParameters
  }

  // test methods

  def "When build is not from the master branch then skip license report generation"() {
    given:
    def pipeline = new PipeLineScript()
    def licensingSupport = new LicensingSupport(pipeline)

    when:
    licensingSupport.updateLicenseReport('myTempBranch', 'credentials-id')

    then:
    isMessageEchoed('Not working with the master branch. Skipping Update License Report for the other branch.')
  }

  def "When can't detect LicensingSupportType then Exception is thrown"() {
    given:
    def pipeline = Stub(PipeLineScript)
    pipeline.sh([script: "ls -al ./build.gradle", returnStatus: true]) >> 1
    pipeline.sh([script: "ls -al ./.ruby-version", returnStatus: true]) >> 1
    def licensingSupport = new LicensingSupport(pipeline)

    when:
    licensingSupport.updateLicenseReport('master', 'credentials-id')

    then:
    def exception = thrown(Exception)
    exception.message == LicensingSupportConstants.MSG_NO_LICENSING_SUPPORT
  }

  def "When can't execute ssh git command then Exception is thrown"() {
    given:
    final def gitCommand = 'git config --global user.name Jenkins'
    behaviour = [
      sh: [
        'ls -al ./build.gradle'                               : 0,
        'grep -c "com.github.hierynomus.license" build.gradle': 0,
        'ls -al ./.ruby-version'                              : 1,
        "${SSH_GIT_CONFIG_USER}"                              : 1
      ]
    ]
    def pipeline = new PipeLineScript()
    def licensingSupport = new LicensingSupport(pipeline)

    when:
    licensingSupport.updateLicenseReport('master', 'credentials-id')

    then:
    def exception = thrown(Exception)
    exception.message == "ssh command '${gitCommand}' failed"
  }

  def "When it is the back-end project with gradle and it uses hierynomus license then gradlew is called to generate license report"() {
    given:
    behaviour = [
      sh            : [
        'ls -al ./build.gradle'                               : 0,
        'grep -c "com.github.hierynomus.license" build.gradle': 0,
        'ls -al ./.ruby-version'                              : 1,
      ],
      readFileResult: 'package gov.ca.cwds'
    ]
    setUpGitSshCommands()
    def pipeline = new PipeLineScript()
    def licensingSupport = new LicensingSupport(pipeline)

    when:
    licensingSupport.updateLicenseReport('master', 'credentials-id')

    then:
    isCredentialsIdUsed('credentials-id')
    isShScriptCalled('grep -c "com.github.hierynomus.license" build.gradle')
    isShScriptCalled('./gradlew deleteLicenses downloadLicenses copyLicenses')
    isShScriptCalled(SSH_GIT_CONFIG_USER)
    isShScriptCalled(SSH_GIT_CONFIG_EMAIL)
    isShScriptCalled(SSH_GIT_ADD_LEGAL)
    isShScriptCalled(SSH_GIT_COMMIT)
    isShScriptCalled(SSH_GIT_PUSH)
    isTextPassedToWriteFile('package gov.ca.cwds' + LicensingSupportConstants.ADDITIONAL_LICENSING_GRADLE_TASKS)
    isMessageEchoed('Detected Licensing Support Type: Gradle Hierynomus License Plugin')
    isMessageEchoed('Generating License Information')
    isMessageEchoed('Updating License Information')
  }

  def "When a GradleRuntime is provided and it is the back-end project with gradle and it uses hierynomus license then the GradleRuntime is called to generate license report"() {
    given:
    behaviour = [
      sh            : [
        'ls -al ./build.gradle'                               : 0,
        'grep -c "com.github.hierynomus.license" build.gradle': 0,
        'ls -al ./.ruby-version'                              : 1,
      ],
      readFileResult: 'package gov.ca.cwds'
    ]
    setUpGitSshCommands()
    def pipeline = new PipeLineScript()
    def licensingSupport = new LicensingSupport(pipeline)
    def gradleRuntime = new GradleRuntime()

    when:
    licensingSupport.updateLicenseReport('master', 'credentials-id', gradleRuntime)

    then:
    isCredentialsIdUsed('credentials-id')
    isShScriptCalled('grep -c "com.github.hierynomus.license" build.gradle')
    isShScriptCalled(SSH_GIT_CONFIG_USER)
    isShScriptCalled(SSH_GIT_CONFIG_EMAIL)
    isShScriptCalled(SSH_GIT_ADD_LEGAL)
    isShScriptCalled(SSH_GIT_COMMIT)
    isShScriptCalled(SSH_GIT_PUSH)
    isTextPassedToWriteFile('package gov.ca.cwds' + LicensingSupportConstants.ADDITIONAL_LICENSING_GRADLE_TASKS)
    areLastGradleRuntimeParameters([buildFile: 'build.gradle', tasks: 'deleteLicenses downloadLicenses copyLicenses'])
    isMessageEchoed('Detected Licensing Support Type: Gradle Hierynomus License Plugin')
    isMessageEchoed('Generating License Information')
    isMessageEchoed('Updating License Information')
  }

  def "When it is the front-end project with package.json and it uses license finder then yarn is called to generate license report"() {
    given:
    behaviour = [
      sh: [
        'ls -al ./build.gradle'                : 1,
        'ls -al ./.ruby-version'               : 0,
        'grep -c "license_finder" package.json': 0
      ]
    ]
    setUpGitSshCommands()
    def pipeline = new PipeLineScript()
    def licensingSupport = new LicensingSupport(pipeline)

    when:
    licensingSupport.updateLicenseReport('master', 'credentials-id')

    then:
    isCredentialsIdUsed('credentials-id')
    isShScriptCalled('grep -c "license_finder" package.json')
    isShScriptCalled('yarn licenses-report')
    isShScriptCalled(SSH_GIT_CONFIG_USER)
    isShScriptCalled(SSH_GIT_CONFIG_EMAIL)
    isShScriptCalled(SSH_GIT_ADD_LEGAL)
    isShScriptCalled(SSH_GIT_COMMIT)
    isShScriptCalled(SSH_GIT_PUSH)
    isMessageEchoed('Detected Licensing Support Type: Ruby License Finder Plugin')
    isMessageEchoed('Generating License Information')
    isMessageEchoed('Updating License Information')
  }
}
