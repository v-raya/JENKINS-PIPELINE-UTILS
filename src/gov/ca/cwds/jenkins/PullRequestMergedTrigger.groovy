package gov.ca.cwds.jenkins

class PullRequestMergedTrigger {
  def triggerProperties(triggerKeyParameter) {
    [$class: 'GenericTrigger',
      genericVariables: [
        [key: 'pull_request_action', value: 'action'],
        [key: 'pull_request_merged', value: 'pull_request.merged'],
        [key: 'pull_request_event', value: 'pull_request']
      ],
      genericRequestVariables: [
        [key: 'trigger_key']
      ],
      token: "$triggerKeyParameter",
      regexpFilterText: '$pull_request_action:$trigger_key:$pull_request_merged',
      regexpFilterExpression: '^closed:' + triggerKeyParameter + ':true$',
      causeString: 'Triggered by PR merge',
      printContributedVariables: false
    ]
  }
}
