package gov.ca.cwds.jenkins

import gov.ca.cwds.jenkins.licensing.LicensingSupport

def call(branchName, sshCredentialsId, runtimeGradle = null, dockerImage = null) {
  def licensingSupport = new LicensingSupport(this, runtimeGradle, dockerImage)
  licensingSupport.updateLicenseReport(branchName, sshCredentialsId)
}
