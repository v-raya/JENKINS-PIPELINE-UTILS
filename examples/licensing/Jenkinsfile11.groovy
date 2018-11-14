def BRANCH = 'master'
def SSH_CRED_ID = '1db97a1a-6604-4d90-9790-a0fd931af6f4'

@Library('jenkins_pipeline_utils@master') _

node('master') {
  def serverArti = Artifactory.server 'CWDS_DEV'
  def rtGradle = Artifactory.newGradleBuild()
  
  stage ('Preparation') {
    git branch: BRANCH, credentialsId: SSH_CRED_ID, url: 'git@github.com:ca-cwds/a_project.git'
    rtGradle.tool = "Gradle_35"
    rtGradle.resolver repo: 'repo', server: serverArti
    rtGradle.useWrapper = false
  }

  updateLicenseReportStage {
    branch = BRANCH
    sshCredentialsId = SSH_CRED_ID
    gradleRuntime = rtGradle
  }
}
