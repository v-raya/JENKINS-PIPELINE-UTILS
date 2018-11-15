package gov.ca.cwds.jenkins

class SshAgent implements Serializable {
  def script
  def credentialsId

  SshAgent(script, credentialsId) {
    this.script = script
    this.credentialsId = credentialsId
  }

  def exec(String command, boolean failOnStatus = false) {
    def cmd = sshCommand(command)
    script.sshagent(credentials: [credentialsId]) {
      def status = script.sh(script: cmd, returnStatus: true)
      if (status != 0 && failOnStatus) {
        throw new Exception("ssh command '${command}' failed")
      }
    }
  }

  static def sshCommand(String command) {
    // Used to avoid known_hosts addition, which would require each machine to have GitHub added in advance (maybe should do?)
    'GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" ' + command
  }
}
