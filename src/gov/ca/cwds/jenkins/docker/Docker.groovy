package gov.ca.cwds.jenkins.docker

class Docker {
  def script
  def globalDocker
  def jobName
  def buildId

  def DOCKEFILE_TEST_PATH = './docker/test/Dockerfile'

  Docker(jobName, buildId, script, globalDocker) {
    this.jobName = jobName
    this.buildId = buildId
    this.script = script
    this.globalDocker = globalDocker
  }

  def createTestingImage() {
    globalDocker.build("${testingImageName()}", "-f ${DOCKEFILE_TEST_PATH} .")
  }

  def removeTestingImage() {
    def status = script.sh(script: "docker rmi ${testingImageName()}", returnStatus: true)
  }

  def withTestingImage(command) {
    def dockerImage = globalDocker.image("${testingImageName()}")
    dockerImage.withRun { container -> script.sh "docker exec -t ${container.id} ${command}"}
  }

  private testingImageName() {
    return "cwds/${jobName}:test-build-${buildId}"
  }

}
