package gov.ca.cwds.jenkins.licensing

import static gov.ca.cwds.jenkins.licensing.LicensingSupportUtils.LICENSE_FOLDER
import static gov.ca.cwds.jenkins.licensing.LicensingSupportUtils.MSG_NO_LICENSING_SUPPORT
import static gov.ca.cwds.jenkins.utils.ProjectUtils.GIT_EMAIL
import static gov.ca.cwds.jenkins.utils.ProjectUtils.GIT_USER

class LicensingSupport implements Serializable {
  def pipeline
  def branchName
  def sshAgent
  def licensingSupportType = null
  def gradleRuntime = null

  LicensingSupport(pipeline, branchName, sshAgent) {
    this.pipeline = pipeline
    this.branchName = branchName
    this.sshAgent = sshAgent
  }

  def initLicensingSupportType() {
    if (null == this.licensingSupportType) {
      this.licensingSupportType = LicensingSupportUtils.getLicensingSupportType(pipeline)
      this.pipeline.echo("Detected Licensing Support Type: ${this.licensingSupportType.title}")
    }
    if (LicensingSupportType.NONE == this.licensingSupportType) {
      throw new Exception(MSG_NO_LICENSING_SUPPORT)
    }
  }

  def generateLicenseReport() {
    if ('master' == this.branchName) {
      initLicensingSupportType()
      pipeline.echo 'Generating License Information'
      switch (this.licensingSupportType) {
        case LicensingSupportType.GRADLE_HIERYNOMUS_LICENSE:
          LicensingSupportUtils.addLicensingGradleTasks(this.pipeline)
          if (null == this.gradleRuntime) {
            this.pipeline.sh './gradlew deleteLicenses downloadLicenses copyLicenses'
          } else {
            this.gradleRuntime.run buildFile: 'build.gradle',
              tasks: 'deleteLicenses downloadLicenses copyLicenses'
          }
          break
        case LicensingSupportType.RUBY_LICENSE_FINDER:
          this.pipeline.sh 'yarn licenses-report'
          break
      }
    } else {
      pipeline.echo 'Not working with the master branch. Skipping License Generation for the other branch.'
    }
  }

  def pushLicenseReport() {
    if ('master' == this.branchName) {
      initLicensingSupportType()
      pipeline.echo 'Updating License Information'
      switch (this.licensingSupportType) {
        case LicensingSupportType.GRADLE_HIERYNOMUS_LICENSE:
        case LicensingSupportType.RUBY_LICENSE_FINDER:
          this.sshAgent.run("git config --global user.name ${GIT_USER}", true)
          this.sshAgent.run("git config --global user.email ${GIT_EMAIL}", true)
          this.sshAgent.run("git add ${LICENSE_FOLDER}")
          this.sshAgent.run('git commit -m "updated license info"')
          this.sshAgent.run('git push --set-upstream origin master', true)
          break
      }
    } else {
      pipeline.echo 'Not working with the master branch. Skipping Push License Report for the other branch.'
    }
  }

  def generateAndPushLicenseReport() {
    generateLicenseReport()
    pushLicenseReport()
  }
}
