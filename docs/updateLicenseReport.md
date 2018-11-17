# updateLicenseReport

## Usage

```groovy
stage('Update License Report') {
  updateLicenseReport(branch, sshCredentialsId, gradleRuntime)
}
```

* *branch* The branch from where the project is being built. Licence Generation will be skipped if it is not the master branch.
* *sshCredentialsId* The Credentials Id for Ssh Agent
* *gradleRuntime* Optional Gradle Runtime that is usually pre-made using `Artifactory.newGradleBuild()`
  and is used only in projects with Gradle (usually back-end).
  If the parameter is omitted for a back-end project, then the stage will try to call the `./gradlew` command.

The `updateLicenseReport` will try to detect if it is a front-end or a back-end project
and what plugin is used in the project for License Report Generation.
If none is found then the stage will fail the build.
Otherwise it will invoke License Report Generation and push the changes into the project repository (if any) under the `legal` folder.
