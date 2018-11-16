package gov.ca.cwds.jenkins.licensing

import gov.ca.cwds.jenkins.SshAgent

import static gov.ca.cwds.jenkins.licensing.LicensingSupportUtils.LICENSE_FOLDER
import static gov.ca.cwds.jenkins.licensing.LicensingSupportUtils.MSG_NO_LICENSING_SUPPORT
import static gov.ca.cwds.jenkins.utils.ProjectUtils.GIT_EMAIL
import static gov.ca.cwds.jenkins.utils.ProjectUtils.GIT_USER

class LicensingSupport implements Serializable {
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
        def sshAgent = new SshAgent(pipeline, sshCredentialsId)
        sshAgent.run("git config --global user.name ${GIT_USER}", true)
        sshAgent.run("git config --global user.email ${GIT_EMAIL}", true)
        sshAgent.run("git add ${LICENSE_FOLDER}")
        sshAgent.run('git commit -m "updated license info"')
        sshAgent.run('git push --set-upstream origin master', true)
        break
    }
  }
}
