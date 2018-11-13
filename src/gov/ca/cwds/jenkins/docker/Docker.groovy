package gov.ca.cwds.jenkins.docker

class Docker {
  def script
  def docker

  Docker(script, docker) {
    this.script = script
    this.docker = docker
  }

  def createTestingImage(pathToDockerFile) {
    script.echo "Building ${testingImageName()}"
    docker.build("${testingImageName()}", "-f ${pathToDockerFile} .")
  }

  def removeTestingImage() {
    script.echo "Removing ${testingImageName()}"

    def stdout = new StringBuilder(), stderr = new StringBuilder()
    def proc = "docker rmi ${testingImageName()}".execute()
    proc.consumeProcessOutput(stdout, stderr)
    proc.waitForOrKill(1000)
    script.echo "out> ${stdout} err> ${stderr}"
  }

  def testingImageName() {
    return "cwds/${script.env.JOB_NAME}:test-build-${script.env.BUILD_ID}"
  }

  def withTestingImage(todo) {
    def dockerImage = docker.image("${testingImageName()}")

    dockerImage.withRun { container ->
      todo(container.id)
    }
  }
}