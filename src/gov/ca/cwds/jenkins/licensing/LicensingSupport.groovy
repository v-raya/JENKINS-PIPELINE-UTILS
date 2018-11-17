package gov.ca.cwds.jenkins.licensing

class LicensingSupport {
  def pipeline
  def licensingSupportType

  LicensingSupport(pipeline) {
    this.pipeline = pipeline
  }

  def updateLicenseReport(branchName, sshCredentialsId, gradleRuntime = null) {
    if ('master' == branchName) {
      determineLicensingSupportType()
      generateLicenseReport(gradleRuntime)
      pushLicenseReport(sshCredentialsId)
    } else {
      pipeline.echo 'Not working with the master branch. Skipping Update License Report for the other branch.'
    }
  }

  private def determineLicensingSupportType() {
    licensingSupportType = new LicensingSupportTypeDeterminer(pipeline).determineLicensingSupportType()
    pipeline.echo("Detected Licensing Support Type: ${licensingSupportType.title}")
    if (LicensingSupportType.NONE == licensingSupportType) {
      throw new Exception(LicensingSupportConstants.MSG_NO_LICENSING_SUPPORT)
    }
  }

  private def generateLicenseReport(gradleRuntime = null) {
    pipeline.echo 'Generating License Information'
    if (licensingSupportType == LicensingSupportType.GRADLE_HIERYNOMUS_LICENSE) {
      addLicensingGradleTasks(pipeline)
      if (null == gradleRuntime) {
        pipeline.sh './gradlew deleteLicenses downloadLicenses copyLicenses'
      } else {
        gradleRuntime.run buildFile: 'build.gradle',
          tasks: 'deleteLicenses downloadLicenses copyLicenses'
      }
    } else if (licensingSupportType == LicensingSupportType.RUBY_LICENSE_FINDER) {
      pipeline.sh 'yarn licenses-report'
    }
  }

  private def addLicensingGradleTasks(pipeline) {
    def source = pipeline.readFile file: 'build.gradle'
    source += LicensingSupportConstants.ADDITIONAL_LICENSING_GRADLE_TASKS
    pipeline.writeFile file: 'build.gradle', text: "$source"
  }

  private def pushLicenseReport(sshCredentialsId) {
    pipeline.echo 'Updating License Information'
    pipeline.sshagent(credentials: [sshCredentialsId]) {
      runSshCommand("git config --global user.name ${LicensingSupportConstants.GIT_USER}", true)
      runSshCommand("git config --global user.email ${LicensingSupportConstants.GIT_EMAIL}", true)
      runSshCommand("git add ${LicensingSupportConstants.LICENSE_FOLDER}")
      runSshCommand('git commit -m "updated license info"')
      runSshCommand('git push --set-upstream origin master', true)
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
