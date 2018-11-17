package gov.ca.cwds.jenkins.licensing

import static gov.ca.cwds.jenkins.licensing.LicensingSupportUtils.LICENSE_FOLDER
import static gov.ca.cwds.jenkins.licensing.LicensingSupportUtils.MSG_NO_LICENSING_SUPPORT

class LicensingSupport {
  def GIT_USER = 'Jenkins'
  def GIT_EMAIL = 'cwdsdoeteam@osi.ca.gov'

  def pipeline
  def licensingSupportType = null

  LicensingSupport(pipeline) {
    this.pipeline = pipeline
  }

  def updateLicenseReport(branchName, sshCredentialsId, gradleRuntime = null) {
    if ('master' == branchName) {
      initLicensingSupportType()
      generateLicenseReport(gradleRuntime)
      pushLicenseReport(sshCredentialsId)
    } else {
      pipeline.echo 'Not working with the master branch. Skipping Update License Report for the other branch.'
    }
  }

  private def initLicensingSupportType() {
    if (null == licensingSupportType) {
      licensingSupportType = LicensingSupportUtils.getLicensingSupportType(pipeline)
      pipeline.echo("Detected Licensing Support Type: ${licensingSupportType.title}")
    }
    if (LicensingSupportType.NONE == licensingSupportType) {
      throw new Exception(MSG_NO_LICENSING_SUPPORT)
    }
  }

  private def generateLicenseReport(gradleRuntime = null) {
    pipeline.echo 'Generating License Information'
    switch (licensingSupportType) {
      case LicensingSupportType.GRADLE_HIERYNOMUS_LICENSE:
        LicensingSupportUtils.addLicensingGradleTasks(pipeline)
        if (null == gradleRuntime) {
          pipeline.sh './gradlew deleteLicenses downloadLicenses copyLicenses'
        } else {
          gradleRuntime.run buildFile: 'build.gradle',
            tasks: 'deleteLicenses downloadLicenses copyLicenses'
        }
        break
      case LicensingSupportType.RUBY_LICENSE_FINDER:
        pipeline.sh 'yarn licenses-report'
        break
    }
  }

  private def pushLicenseReport(sshCredentialsId) {
    pipeline.echo 'Updating License Information'
    switch (licensingSupportType) {
      case LicensingSupportType.GRADLE_HIERYNOMUS_LICENSE:
      case LicensingSupportType.RUBY_LICENSE_FINDER:
        pipeline.sshagent(credentials: [sshCredentialsId]) {
          runSshCommand("git config --global user.name ${GIT_USER}", true)
          runSshCommand("git config --global user.email ${GIT_EMAIL}", true)
          runSshCommand("git add ${LICENSE_FOLDER}")
          runSshCommand('git commit -m "updated license info"')
          runSshCommand('git push --set-upstream origin master', true)
        }
        break
    }
  }

  private def runSshCommand(command, failOnNonZeroStatus = false) {
    // Used to avoid known_hosts addition, which would require each machine to have GitHub added in advance (maybe should do?)
    def cmd = 'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" ' + command
    def status = pipeline.sh(script: cmd, returnStatus: true)
    if (status != 0 && failOnNonZeroStatus) {
      throw new Exception("ssh command '${command}' failed")
    }
  }
}
