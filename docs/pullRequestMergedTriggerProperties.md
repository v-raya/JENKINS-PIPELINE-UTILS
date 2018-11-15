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
  triggerProperties = pullRequestMergedTriggerProperties('EHAqns5oYtCb')
  properties([
    pipelineTriggers([triggerProperties])
  ])
}
```
