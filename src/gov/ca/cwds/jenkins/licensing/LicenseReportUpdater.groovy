package gov.ca.cwds.jenkins.licensing

import gov.ca.cwds.jenkins.SshAgent

class LicenseReportUpdater implements Serializable {
  def pipeline
  def branch
  def sshCredentialsId
  def gradleRuntime

  LicenseReportUpdater(pipeline, branch, sshCredentialsId) {
    this.pipeline = pipeline
    this.branch = branch
    this.sshCredentialsId = sshCredentialsId
  }

  def run() {
    def sshAgent = new SshAgent(pipeline, sshCredentialsId)
    def licensingSupport = new LicensingSupport(pipeline, branch, sshAgent)
    licensingSupport.gradleRuntime = gradleRuntime
    licensingSupport.generateAndPushLicenseReport()
  }
}
