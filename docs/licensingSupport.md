# updateLicenseReportStage

## Usage

```groovy
node('master') {
  ...
  
  stage ('Preparation') {
    ...
  }

  updateLicenseReportStage {
    branch = BRANCH
    sshCredentialsId = SSH_CRED_ID
    gradleRuntime = rtGradle
  }
}
```

* *branch* The branch from where the project is being built. Licence Generation will be skipped if it is not the master branch.
* *sshCredentialsId* The Credentials Id for Ssh Agent
* *gradleRuntime* Optional Gradle Runtime that is usually pre-made using `Artifactory.newGradleBuild()`
  and is used only in projects with Gradle (usually back-end).
  If the parameter is omitted for a back-end project, then the stage will try to call the `./gradlew` command.

The `updateLicenseReportStage` will try to detect if it is a front-end or a back-end project
and what plugin is used in the project for License Report Generation.
If none is found then the stage will fail the build.
Otherwise it will invoke License Report Generation and push the changes into the project repository (if any) under the `legal` folder.

It is also possible to use the functionality in a usual Jenkins `stage` instead of the `updateLicenseReportStage`.
Please look for more examples of Jenkins files under the [examples/licensing](../examples/licensing) directory.
