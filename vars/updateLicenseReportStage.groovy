package gov.ca.cwds.jenkins

import gov.ca.cwds.jenkins.licensing.LicenseReportUpdater
import gov.ca.cwds.jenkins.utils.ProjectUtils

/*
Usage in Jenkins pipeline:

  updateLicenseReportStage {
    branch = BRANCH
    sshCredentialsId = SSH_CRED_ID
    gradleRuntime = rtGradle
  }

gradleRuntime is optional
 */
def call(stageBody) {
  // evaluate the body block, and collect configuration into the object
  def stageParams = ProjectUtils.processStageParameters(stageBody)
  stage('Update License Report') {
    def licenseReportUpdater = new LicenseReportUpdater(this, stageParams.branch, stageParams.sshCredentialsId)
    licenseReportUpdater.gradleRuntime = stageParams.gradleRuntime
    licenseReportUpdater.run()
  }
}
