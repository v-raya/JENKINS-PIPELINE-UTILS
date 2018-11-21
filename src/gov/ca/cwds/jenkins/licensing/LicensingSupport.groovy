package gov.ca.cwds.jenkins.licensing

class LicensingSupport {
  final def GIT_USER = 'Jenkins'
  final def GIT_EMAIL = 'cwdsdoeteam@osi.ca.gov'
  final def LICENSE_BUILD_FOLDER = 'build/reports/license'
  final def LICENSE_FOLDER = 'legal'
  final def MSG_NO_LICENSING_SUPPORT = 'No known Licensing Support is found in the project'

  def pipeline
  def licensingSupportType

  LicensingSupport(pipeline) {
    this.pipeline = pipeline
  }

  def updateLicenseReport(branchName, sshCredentialsId, runtimeGradle = null) {
    if ('master' == branchName) {
      determineLicensingSupportType()
      generateLicenseReport(runtimeGradle)
      pushLicenseReport(sshCredentialsId)
    } else {
      pipeline.echo 'Not working with the master branch. Skipping Update License Report for the other branch.'
    }
  }

  private def determineLicensingSupportType() {
    licensingSupportType = new LicensingSupportTypeDeterminer(pipeline).determineLicensingSupportType()
    pipeline.echo("Detected Licensing Support Type: ${licensingSupportType.title}")
    if (LicensingSupportType.NONE == licensingSupportType) {
      throw new Exception(MSG_NO_LICENSING_SUPPORT)
    }
  }

  private def generateLicenseReport(runtimeGradle = null) {
    pipeline.echo 'Generating License Information'
    if (licensingSupportType == LicensingSupportType.GRADLE_HIERYNOMUS_LICENSE) {
      if (null == runtimeGradle) {
        pipeline.sh './gradlew downloadLicenses'
      } else {
        runtimeGradle.run buildFile: 'build.gradle', tasks: 'downloadLicenses'
      }
      pipeline.sh script: "mkdir ${LICENSE_FOLDER}", returnStatus: true
      pipeline.sh "cp ${LICENSE_BUILD_FOLDER}/* ${LICENSE_FOLDER}"
    } else if (licensingSupportType == LicensingSupportType.RUBY_LICENSE_FINDER) {
      pipeline.sh 'yarn licenses-report'
    }
  }

  private def pushLicenseReport(sshCredentialsId) {
    pipeline.echo 'Updating License Information'
    pipeline.sshagent(credentials: [sshCredentialsId]) {
      runSshCommand("git config --global user.name ${GIT_USER}", true)
      runSshCommand("git config --global user.email ${GIT_EMAIL}", true)
      runSshCommand("git add ${LICENSE_FOLDER}")
      runSshCommand('git commit -m "updated license info"')
      runSshCommand('git push --set-upstream origin master', true)
    }
  }

  private def runSshCommand(command, failOnNonZeroStatus = false) {
    // Used to avoid known_hosts addition, which would require each machine to have GitHub added in advance (maybe should do?)
    def cmd = 'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" ' + command
    def status = pipeline.sh script: cmd, returnStatus: true
    if (status != 0 && failOnNonZeroStatus) {
      throw new Exception("ssh command '${command}' failed")
    }
  }
}
