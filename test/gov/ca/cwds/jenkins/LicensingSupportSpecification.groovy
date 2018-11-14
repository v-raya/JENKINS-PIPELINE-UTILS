package gov.ca.cwds.jenkins

import gov.ca.cwds.jenkins.licensing.LicensingSupport
import gov.ca.cwds.jenkins.utils.ProjectUtils
import spock.lang.Specification

import static gov.ca.cwds.jenkins.licensing.LicensingSupportUtils.MSG_NO_LICENSING_SUPPORT

class LicensingSupportSpecification extends Specification {

    class PipeLineScript {
        def behaviour = [
                sh : [:] // map of script -> result values where script is a parameter of the pipeline.sh() method
        ]
        def lastShScriptCalled = null

        def echoedMessages = []

        PipeLineScript() {
        }

        def sh(String script) {
            lastShScriptCalled = script
            return behaviour && behaviour.sh ? behaviour.sh[script] : 1
        }

        def sh(Map params) {
            return this.sh(params.script)
        }

        def echo(String msg) {
            this.echoedMessages.add(msg)
        }

        def readFile(Map params) {
        }

        def writeFile(Map params) {
        }

        def isLastShScriptCalled(String script) {
            script == lastShScriptCalled
        }

        def isMessageEchoed(String msg) {
            echoedMessages.contains(msg)
        }
    }

    def "When can't detect LicensingSupportType then generateLicenseReport throw Exception"() {
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

    def "When can't detect LicensingSupportType then pushLicenseReport throw Exception"() {
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

    def "When build is not from the master branch then skip license report generation"() {
        given:
        def pipeline = new PipeLineScript()
        def licensingSupport = new LicensingSupport(pipeline, 'myTempBranch', null)

        when:
        licensingSupport.generateAndPushLicenseReport()

        then:
        pipeline.isMessageEchoed('Not working with the master branch. Skipping License Generation for the other branch.')
        pipeline.isMessageEchoed('Not working with the master branch. Skipping Push License Report for the other branch.')
    }


    def "When it is the back-end project with gradle and it uses hierynomus license then gradlew is called to generate license report"() {
        given:
        def pipeline = new PipeLineScript()
        pipeline.behaviour = [
                sh : [
                        'grep -c "com.github.hierynomus.license" build.gradle' : 0,
                        'test -e build.gradle' : 0,
                        'test -e package.json' : 1,
                        'grep -c "license_finder" package.json' : 1
                ]
        ]
        def licensingSupport = new LicensingSupport(pipeline, 'master', null)

        when:
        licensingSupport.generateLicenseReport()

        then:
        pipeline.isLastShScriptCalled('./gradlew deleteLicenses downloadLicenses copyLicenses')
        pipeline.isMessageEchoed('Detected Licensing Support Type: Gradle Hierynomus License Plugin')
        pipeline.isMessageEchoed('Generating License Information')
    }

    def "When it is the front-end project with package.json and it uses license finder then yarn is called to generate license report"() {
        given:
        def pipeline = new PipeLineScript()
        pipeline.behaviour = [
                sh : [
                        'grep -c "com.github.hierynomus.license" build.gradle' : 1,
                        'test -e build.gradle' : 1,
                        'test -e package.json' : 0,
                        'grep -c "license_finder" package.json' : 0
                ]
        ]
        def licensingSupport = new LicensingSupport(pipeline, 'master', null)

        when:
        licensingSupport.generateLicenseReport()

        then:
        pipeline.isLastShScriptCalled('yarn licenses-report')
        pipeline.isMessageEchoed('Detected Licensing Support Type: Ruby License Finder Plugin')
        pipeline.isMessageEchoed('Generating License Information')
    }
}
