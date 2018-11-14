package gov.ca.cwds.jenkins

import gov.ca.cwds.jenkins.licensing.LicensingSupport
import gov.ca.cwds.jenkins.licensing.LicensingSupportUtils
import gov.ca.cwds.jenkins.utils.ProjectUtils
import spock.lang.Specification

import static gov.ca.cwds.jenkins.licensing.LicensingSupportUtils.MSG_NO_LICENSING_SUPPORT

class LicensingSupportSpecification extends Specification {

    class PipeLineScript {
        def build(hash) {
        }

        def PipeLineScript() {}
    }

    def "If can't detect LicensingSupportType then generateLicenseReport throw Exception"() {
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

    def "If can't detect LicensingSupportType then pushLicenseReport throw Exception"() {
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
}
