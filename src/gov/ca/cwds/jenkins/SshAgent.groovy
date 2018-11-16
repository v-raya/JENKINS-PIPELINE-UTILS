package gov.ca.cwds.jenkins

class SshAgent implements Serializable {
  def pipeline
  def credentialsId

  SshAgent(pipeline, credentialsId) {
    this.pipeline = pipeline
    this.credentialsId = credentialsId
  }

  def run(String command, boolean failOnStatus = false) {
    def cmd = sshCommand(command)
    pipeline.sshagent(credentials: [credentialsId]) {
      def status = pipeline.sh(script: cmd, returnStatus: true)
      if (status != 0 && failOnStatus) {
        throw new Exception("ssh command '${command}' failed")
      }
    }
  }

  private def sshCommand(String command) {
    // Used to avoid known_hosts addition, which would require each machine to have GitHub added in advance (maybe should do?)
    'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" ' + command
  }
}
