# smokeTest

## Usage

```groovy
  smokeTest(String pathToSmokeTestScript)
```

* *pathToSmokeTestScript* is the path to the script executing the smoke test, for example './test/resources/smoketest.sh'

## Examples

```groovy
   stage('Smoke Tests') {
      smokeTest('./test/resources/smoketest.sh')
   }
```
