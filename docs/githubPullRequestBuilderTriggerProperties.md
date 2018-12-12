# githubPullRequestBuilderTriggerProperties

## Usage

```groovy
  githubPullRequestBuilderTriggerProperties(String jenkinsUrl)
```

* *jenkinsUrl* The url to the jenkins instance.  This is optional and defaults to 'http://jenkins.dev.cwds.io:8080'


## Examples

```groovy
node('linux') {
  triggerProperties = githubPullRequestBuilderTriggerProperties('http://jenkins.dev.cwds.io:8080')
  properties([
    pipelineTriggers([triggerProperties])
  ])
}
```

```groovy
node('linux') {
  triggerProperties = githubPullRequestBuilderTriggerProperties()
  properties([
    pipelineTriggers([triggerProperties])
  ])
}
```

## Configuration

In order to setup this up, you will need to have a Github webook configured with the following properies:

Payload URL: http://jen-proxy.dev.cwds.io/ghprbhook/
Content type: application/json
Events: Individual events > Issue Comments and Pull Requests

On the Jenkins side no configuration is needed, although the first build will not "work" rather it will bootstrap the configuration...all subsequent builds will work as expected.
