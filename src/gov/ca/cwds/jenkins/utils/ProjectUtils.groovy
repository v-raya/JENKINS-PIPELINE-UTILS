package gov.ca.cwds.jenkins.utils

class ProjectUtils implements Serializable {
  static def GIT_USER = 'Jenkins'
  static def GIT_EMAIL = 'cwdsdoeteam@osi.ca.gov'

  static boolean hasGradleBuildFile(pipeline) {
    pipeline.sh(script: 'test -e build.gradle', returnStatus: true) == 0
  }

  static boolean hasPackageJsonFile(pipeline) {
    pipeline.sh(script: 'test -e package.json', returnStatus: true) == 0
  }
}
