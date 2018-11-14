package gov.ca.cwds.jenkins

import gov.ca.cwds.jenkins.licensing.LicensingSupport
import gov.ca.cwds.jenkins.SshAgent

def call(stageBody) {
    // evaluate the body block, and collect configuration into the object
    def stageParams = [:]
    stageBody.resolveStrategy = Closure.DELEGATE_FIRST
    stageBody.delegate = stageParams
    stageBody()

    stage('Update License Report') {
        def sshAgent = new SshAgent(this, stageParams.sshCredentialsId)
        def licensingSupport = new LicensingSupport(this, stageParams.branch, sshAgent)
        licensingSupport.generateAndPushLicenseReport()
    }
}