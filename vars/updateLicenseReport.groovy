package gov.ca.cwds.jenkins

import gov.ca.cwds.jenkins.licensing.LicensingSupport

def call(branchName, sshCredentialsId, runtimeGradle = null, dockerImage = null) {
  def buildMetadata = new BuildMetadata(this, this.env.JOB_NAME, this.env.BUILD_ID, this.env.WORKSPACE)
  def licensingSupport = new LicensingSupport(this, runtimeGradle, dockerImage)
  licensingSupport.updateLicenseReport(branchName, sshCredentialsId, buildMetadata)
}
