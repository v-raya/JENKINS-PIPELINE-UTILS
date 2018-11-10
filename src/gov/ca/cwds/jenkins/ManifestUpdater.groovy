package gov.ca.cwds.jenkins

class ManifestUpdater {
  def CARES_GITHUB_URL = "git@github.com:ca-cwds/cws-cares.git"
  def GIT_USER = "Jenkins"
  def GIT_EMAIL = "cwdsdoeteam@osi.ca.gov"
  def script

  def ManifestUpdater(script) {
    this.script = script
  }

  def update(applicationName, manifestName, credentialsId, version) {
    checkoutCares(credentialsId)
    updateManifestFile(applicationName, manifestName, version)
    script.sshagent(credentials: credentialsId) { commitVersionInCares() }
  }

  def commitVersionInCares() {
    if (script.sh(script: "git status --porcelain", returnStdout: true)) {
      script.sh(script: "git config --global user.email ${GIT_EMAIL}")
      script.sh(script: "git config --global user.name '${GIT_USER}'")
      script.sh(script: "git commit -am \"Update component version from script\"")
      script.sh(script: "git push origin master")
    }
  }

  private checkoutCares(credentialsId) {
    script.git branch: "master", credentialsId: credentialsId, url: CARES_GITHUB_URL
  }

  private updateManifestFile(applicationName, manifestName, version) {
    def properties = script.readYaml file: "${manifestName}.yaml"
    properties."${applicationName}" = version
    script.writeYaml file: "${manifestName}.yaml", data: properties
  }
}
