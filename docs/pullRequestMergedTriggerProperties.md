# pullRequestMergedTriggerProperties

## Usage

```groovy
  pullRequestMergedTriggerProperties(String triggerKeyParameter)
```

* *triggerKeyParameter* This is the parameter passed from Github to the Jenkins Generic Webhook Trigger
to make sure it only matches for a specific project.

This can be used with any master build that is triggered by a PR merge to simplify configuration and make sure
it is not changed from the UI.

## Examples

```groovy
node('linux') {
  triggerProperties = pullRequestMergedTriggerProperties('dashboard-master')
  properties([
    pipelineTriggers([triggerProperties])
  ])
}
```

## Configuration

In order to setup this up, you will need to have a Github webook configured with the following properies:

Payload URL: http://jen-proxy.dev.cwds.io/generic-webhook-trigger/invoke?trigger_key=dashboard-master&token=dashboard-master
Content type: application/json
Events: Individual events > Pull Requests

On the Jenkins side the first time you setup you will need to configure the `trigger_key` and `token` in the Jenkins UI:

Under Generic Webhook Trigger add the following fields:

Token: dashboard-master
Expression: ^closed:dashboard-master:true$
Text: $pull_request_action:$trigger_key:$pull_request_merged
