# checkForLabel

## Usage

```groovy
  checkForLabel(String projectName)
```

* *projectName* Is the name of the project in Github without ca-cwds (cans, intake, cals-api, etc).

This is designed for checking for a valid SemVer label in github on a Pull Request. If the PR does
not have a label of 'major', 'minor', or 'patch' it will prouduce a non-zero return code.

## Examples

```groovy
stage('Check SemVer Label') {
 checkForLabel('dashboard')
}
```
