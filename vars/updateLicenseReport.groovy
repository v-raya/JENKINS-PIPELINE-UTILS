package gov.ca.cwds.jenkins

import gov.ca.cwds.jenkins.licensing.LicensingSupport

/*
Usage in Jenkins pipeline:

stage('Update License Report') {
  updateLicenseReport(branch, sshCredentialsId, runtimeGradle)
}

gradleRuntime is optional
 */
def call(branchName, sshCredentialsId, runtimeGradle = null, dockerImage = null) {
  def licensingSupport = new LicensingSupport(this, runtimeGradle, dockerImage)
  licensingSupport.updateLicenseReport(branchName, sshCredentialsId)
}
