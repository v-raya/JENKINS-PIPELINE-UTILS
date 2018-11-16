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
    if (null == licensingSupportType) {
      licensingSupportType = LicensingSupportUtils.getLicensingSupportType(pipeline)
      pipeline.echo("Detected Licensing Support Type: ${licensingSupportType.title}")
    }
    if (LicensingSupportType.NONE == licensingSupportType) {
      throw new Exception(MSG_NO_LICENSING_SUPPORT)
    }
  }

  def generateLicenseReport() {
    if ('master' == branchName) {
      initLicensingSupportType()
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
    } else {
      pipeline.echo 'Not working with the master branch. Skipping License Generation for the other branch.'
    }
  }

  def pushLicenseReport() {
    if ('master' == branchName) {
      initLicensingSupportType()
      pipeline.echo 'Updating License Information'
      switch (licensingSupportType) {
        case LicensingSupportType.GRADLE_HIERYNOMUS_LICENSE:
        case LicensingSupportType.RUBY_LICENSE_FINDER:
          sshAgent.run("git config --global user.name ${GIT_USER}", true)
          sshAgent.run("git config --global user.email ${GIT_EMAIL}", true)
          sshAgent.run("git add ${LICENSE_FOLDER}")
          sshAgent.run('git commit -m "updated license info"')
          sshAgent.run('git push --set-upstream origin master', true)
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
