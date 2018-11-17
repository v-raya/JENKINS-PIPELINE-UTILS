package gov.ca.cwds.jenkins

/*
Usage in Jenkins pipeline:

stage('Update License Report') {
  updateLicenseReport(branch, sshCredentialsId, gradleRuntime)
}

gradleRuntime is optional
 */
def call(branchName, sshCredentialsId, gradleRuntime = null) {
  def licensingSupport = new LicensingSupport(this)
  licensingSupport.updateLicenseReport(branchName, sshCredentialsId, gradleRuntime)
}
