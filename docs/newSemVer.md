# newSemVer

## Usage

```groovy
  newSemVer()
  newSemVer(String increment, List tagPrefixes)
```

* *increment* Is the optional passing in of an increment to allow for updating outside
of pull requests. Options are ['major', 'minor', 'patch']

* *tagPrefixes* Is the optional list of prefixes of tags for pipelines that may produce multiple artifacts.
If the list is passed, then one and only one of its values should be present among PR labels. 

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

stage("Increment Tag") {
  newTag = newSemVer('', ['lis', 'cwscms', 'capu'])
}
```
