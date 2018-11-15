# newSemVer

## Usage

```groovy
  newSemVer()
  newSemVer(String increment)
```

* *increment* Is the optional passing in of an increment to allow for updating outside
of pull requests. Options are ['major', 'minor', 'patch']

This returns a new SemVer tag bumped based upon a label set in the PR to `major`, `minor`, or `patch`.
The returned new version can be used in downstream Jenkins stages to tag the project maybe using
[tagGithubRepo](tagGithubRepo.md) and [updateManifest](updateManifest.md).

It also allows you to pass an increment in directly for the case of wanting to bump a tag that isnt' triggered
by a pull request.

## Examples

```groovy
stage("Increment Tag") {
  newTag = newSemVer()
}
```
