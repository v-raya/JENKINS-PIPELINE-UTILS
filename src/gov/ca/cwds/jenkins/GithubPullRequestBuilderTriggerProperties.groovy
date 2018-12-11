package gov.ca.cwds.jenkins

class GithubPullRequestBuilderTriggerProperties {
  def script

  GithubPullRequestBuilderTriggerProperties(script) {
    this.script = script
  }

  def triggerProperties(jenkinsUrl) {
    [$class: 'org.jenkinsci.plugins.ghprb.GhprbTrigger',
      spec: 'H/5 * * * *',
      configVersion: 3,
      allowMembersOfWhitelistedOrgsAsAdmin: true,
      orgslist: 'ca-cwds',
      cron: 'H/5 * * * *',
      onlyTriggerPhrase: false,
      useGitHubHooks: true,
      permitAll: false,
      autoCloseFailedPullRequests: false,
      displayBuildErrorsOnDownstreamBuilds: false,
      triggerPhrase: 'please retest me',
      skipBuildPhrase: '.*\\[skip\\W+ci\\].*',
      extensions: [
                    [ 
                        $class: 'org.jenkinsci.plugins.ghprb.extensions.build.GhprbCancelBuildsOnUpdate',
                        overrideGlobal: false
                    ],
                    [
                        $class: 'org.jenkinsci.plugins.ghprb.extensions.status.GhprbSimpleStatus',
                        commitStatusContext: 'Pull Request Testing',
                        statusUrl: "${jenkinsUrl}/job/${script.env.JOB_NAME}/${script.env.BUILD_ID}",
                        addTestResults: false,
                        completedStatus: [
                          [
                            $class: 'org.jenkinsci.plugins.ghprb.extensions.comments.GhprbBuildResultMessage',
                            message: 'Success',
                            result: 'SUCCESS'
                          ],
                          [
                            $class: 'org.jenkinsci.plugins.ghprb.extensions.comments.GhprbBuildResultMessage',
                            message: 'Build Failed',
                            result: 'FAILURE'
                          ],
                          [
                            $class: 'org.jenkinsci.plugins.ghprb.extensions.comments.GhprbBuildResultMessage',
                            message: 'Build Error',
                            result: 'ERROR'
                          ]
                        ]
                      ]
                  ]
    ]
  }
}
