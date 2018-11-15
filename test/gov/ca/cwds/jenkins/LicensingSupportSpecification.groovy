package gov.ca.cwds.jenkins

import gov.ca.cwds.jenkins.licensing.LicensingSupport
import gov.ca.cwds.jenkins.utils.ProjectUtils
import spock.lang.Specification

import static gov.ca.cwds.jenkins.licensing.LicensingSupportUtils.MSG_NO_LICENSING_SUPPORT

class LicensingSupportSpecification extends Specification {

    class PipeLineScript {
        def spec

        PipeLineScript(spec) {
            this.spec = spec
        }

        def sh(String script) {
            spec.actualValues.calledShScripts.add(script)
            return behaviour && behaviour.sh ? behaviour.sh[script] : 1
        }

        def sh(Map params) {
            return this.sh(params.script)
        }

        def echo(String msg) {
            spec.actualValues.echoedMessages.add(msg)
        }

        def readFile(Map params) {
        }

        def writeFile(Map params) {
        }

        def sshagent(Map params, closure) {
            spec.actualValues.usedCredentialsId = params.credentials[0]
            closure()
        }
    }

    class GradleRuntime {
        def spec

        def GradleRuntime(spec) {
            this.spec = spec
        }

        def run(Map parameters) {
            spec.actualValues.lastGradleRuntimeParameters = parameters
        }
    }

    def behaviour = [
            sh: [:] // map of script -> result values where script is a parameter of the pipeline.sh() method
    ]

    def actualValues = [
            calledShScripts            : [] as Set,
            echoedMessages             : [] as Set,
            usedCredentialsId          : null,
            lastGradleRuntimeParameters: null
    ]

    static def getSshGitCommand(String gitCommand) {
        'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" ' + gitCommand
    }

    final static def SSH_GIT_CONFIG_USER = getSshGitCommand('git config --global user.name Jenkins')
    final static def SSH_GIT_CONFIG_EMAIL = getSshGitCommand('git config --global user.email cwdsdoeteam@osi.ca.gov')
    final static def SSH_GIT_ADD_LEGAL = getSshGitCommand('git add legal')
    final static def SSH_GIT_COMMIT = getSshGitCommand('git commit -m "updated license info"')
    final static def SSH_GIT_PUSH = getSshGitCommand('git push --set-upstream origin master')

    def setUpGitSshCommands() {
        behaviour.sh[SSH_GIT_CONFIG_USER] = 0
        behaviour.sh[SSH_GIT_CONFIG_EMAIL] = 0
        behaviour.sh[SSH_GIT_ADD_LEGAL] = 0
        behaviour.sh[SSH_GIT_COMMIT] = 0
        behaviour.sh[SSH_GIT_PUSH] = 0
    }

    // assertion methods

    def isLastShScriptCalled(String expectedShScript) {
        expectedShScript == actualValues.calledShScripts.last()
    }

    def areShScriptsCalled(Set expectedShScripts) {
        expectedShScripts == actualValues.calledShScripts
    }

    def isMessageEchoed(String expectedMessage) {
        actualValues.echoedMessages.contains(expectedMessage)
    }

    def isCredentialsIdUsed(String expectedCredentialsId) {
        expectedCredentialsId == actualValues.usedCredentialsId
    }

    def areLastGradleRuntimeParameters(expectedParameters) {
        expectedParameters == actualValues.lastGradleRuntimeParameters
    }

    // test methods

    def "When can't detect LicensingSupportType then generateLicenseReport throws Exception"() {
        given:
        def pipeline = Mock(PipeLineScript)
        def licensingSupport = new LicensingSupport(pipeline, 'master', null)
        ProjectUtils.hasGradleBuildFile(pipeline) >> false
        ProjectUtils.hasPackageJsonFile(pipeline) >> false

        when:
        licensingSupport.generateLicenseReport()

        then:
        def exception = thrown(Exception)
        exception.message == MSG_NO_LICENSING_SUPPORT
    }

    def "When can't detect LicensingSupportType then pushLicenseReport throws Exception"() {
        given:
        def pipeline = Mock(PipeLineScript)
        def licensingSupport = new LicensingSupport(pipeline, 'master', null)
        ProjectUtils.hasGradleBuildFile(pipeline) >> false
        ProjectUtils.hasPackageJsonFile(pipeline) >> false

        when:
        licensingSupport.pushLicenseReport()

        then:
        def exception = thrown(Exception)
        exception.message == MSG_NO_LICENSING_SUPPORT
    }

    def "When can't execute ssh git command then pushLicenseReport throws Exception"() {
        given:
        final def gitCommand = 'git config --global user.name Jenkins'
        behaviour = [
                sh: [
                        'test -e build.gradle'                                : 0,
                        'grep -c "com.github.hierynomus.license" build.gradle': 0,
                        'test -e package.json'                                : 1,
                        'grep -c "license_finder" package.json'               : 1,
                        "${SSH_GIT_CONFIG_USER}"                              : 1
                ]
        ]
        def pipeline = new PipeLineScript(this)
        def sshAgent = new SshAgent(pipeline, 'credentials-id')
        def licensingSupport = new LicensingSupport(pipeline, 'master', sshAgent)

        when:
        licensingSupport.pushLicenseReport()

        then:
        isCredentialsIdUsed('credentials-id')
        isLastShScriptCalled(SSH_GIT_CONFIG_USER)
        def exception = thrown(Exception)
        exception.message == "ssh command '${gitCommand}' failed"
    }

    def "When build is not from the master branch then skip license report generation"() {
        given:
        def pipeline = new PipeLineScript(this)
        def licensingSupport = new LicensingSupport(pipeline, 'myTempBranch', null)

        when:
        licensingSupport.generateAndPushLicenseReport()

        then:
        isMessageEchoed('Not working with the master branch. Skipping License Generation for the other branch.')
        isMessageEchoed('Not working with the master branch. Skipping Push License Report for the other branch.')
    }

    def "When it is the back-end project with gradle and it uses hierynomus license then gradlew is called to generate license report"() {
        given:
        behaviour = [
                sh: [
                        'test -e build.gradle'                                : 0,
                        'grep -c "com.github.hierynomus.license" build.gradle': 0,
                        'test -e package.json'                                : 1,
                        'grep -c "license_finder" package.json'               : 1
                ]
        ]
        setUpGitSshCommands()
        def pipeline = new PipeLineScript(this)
        def sshAgent = new SshAgent(pipeline, 'credentials-id')
        def licensingSupport = new LicensingSupport(pipeline, 'master', sshAgent)

        when:
        licensingSupport.generateAndPushLicenseReport()

        then:
        isCredentialsIdUsed('credentials-id')
        def expectedShScriptsCalled = [
                'test -e build.gradle',
                'grep -c "com.github.hierynomus.license" build.gradle',
                './gradlew deleteLicenses downloadLicenses copyLicenses',
                SSH_GIT_CONFIG_USER,
                SSH_GIT_CONFIG_EMAIL,
                SSH_GIT_ADD_LEGAL,
                SSH_GIT_COMMIT,
                SSH_GIT_PUSH
        ] as Set
        areShScriptsCalled(expectedShScriptsCalled)
        isMessageEchoed('Detected Licensing Support Type: Gradle Hierynomus License Plugin')
        isMessageEchoed('Generating License Information')
        isMessageEchoed('Updating License Information')
    }

    def "When a GradleRuntime is provided and it is the back-end project with gradle and it uses hierynomus license then the GradleRuntime is called to generate license report"() {
        given:
        behaviour = [
                sh: [
                        'test -e build.gradle'                                : 0,
                        'grep -c "com.github.hierynomus.license" build.gradle': 0,
                        'test -e package.json'                                : 1,
                        'grep -c "license_finder" package.json'               : 1
                ]
        ]
        setUpGitSshCommands()
        def pipeline = new PipeLineScript(this)
        def sshAgent = new SshAgent(pipeline, 'credentials-id')
        def licensingSupport = new LicensingSupport(pipeline, 'master', sshAgent)
        def gradleRuntime = new GradleRuntime(this)
        licensingSupport.gradleRuntime = gradleRuntime

        when:
        licensingSupport.generateAndPushLicenseReport()

        then:
        isCredentialsIdUsed('credentials-id')
        def expectedShScriptsCalled = [
                'test -e build.gradle',
                'grep -c "com.github.hierynomus.license" build.gradle',
                SSH_GIT_CONFIG_USER,
                SSH_GIT_CONFIG_EMAIL,
                SSH_GIT_ADD_LEGAL,
                SSH_GIT_COMMIT,
                SSH_GIT_PUSH
        ] as Set
        areShScriptsCalled(expectedShScriptsCalled)
        areLastGradleRuntimeParameters([buildFile: 'build.gradle', tasks: 'deleteLicenses downloadLicenses copyLicenses'])
        isMessageEchoed('Detected Licensing Support Type: Gradle Hierynomus License Plugin')
        isMessageEchoed('Generating License Information')
        isMessageEchoed('Updating License Information')
    }

    def "When it is the front-end project with package.json and it uses license finder then yarn is called to generate license report"() {
        given:
        behaviour = [
                sh: [
                        'test -e build.gradle'                                : 1,
                        'grep -c "com.github.hierynomus.license" build.gradle': 1,
                        'test -e package.json'                                : 0,
                        'grep -c "license_finder" package.json'               : 0
                ]
        ]
        setUpGitSshCommands()
        def pipeline = new PipeLineScript(this)
        def sshAgent = new SshAgent(pipeline, 'credentials-id')
        def licensingSupport = new LicensingSupport(pipeline, 'master', sshAgent)

        when:
        licensingSupport.generateAndPushLicenseReport()

        then:
        isCredentialsIdUsed('credentials-id')
        def expectedShScriptsCalled = [
                'test -e build.gradle',
                'test -e package.json',
                'grep -c "license_finder" package.json',
                'yarn licenses-report',
                SSH_GIT_CONFIG_USER,
                SSH_GIT_CONFIG_EMAIL,
                SSH_GIT_ADD_LEGAL,
                SSH_GIT_COMMIT,
                SSH_GIT_PUSH
        ] as Set
        areShScriptsCalled(expectedShScriptsCalled)
        isMessageEchoed('Detected Licensing Support Type: Ruby License Finder Plugin')
        isMessageEchoed('Generating License Information')
        isMessageEchoed('Updating License Information')
    }
}
