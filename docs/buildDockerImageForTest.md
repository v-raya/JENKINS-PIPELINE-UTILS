# buildDockerImageForTest

## Usage

```groovy
  buildDockerImageForTest()
```

This is used to create a docker image used for linting ( and in the future other testing ) against.

## Examples

```groovy
stage('Testing Statge') {
 buildDockerImageForTest()
}
```

## Dockerfile
By convention the library builds the docker image for test from **./docker/test/Dockerfile**
There currently is no method for customizing the location of the Dockerfile.

## Docker Image
The library creates a docker image with the name **cwds/{JOB_NAME}:test-build-${BUILD\_ID}**

* *JOB_NAME* is the name of the Jenkins Job that is running
* *BUILD_ID*  is the build number for the Jenkins Job.