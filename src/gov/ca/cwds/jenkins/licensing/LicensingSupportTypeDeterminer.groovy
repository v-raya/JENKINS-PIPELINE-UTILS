package gov.ca.cwds.jenkins.licensing

import gov.ca.cwds.jenkins.common.ProjectTypes
import gov.ca.cwds.jenkins.common.ProjectTypesDeterminer

class LicensingSupportTypeDeterminer {
  def pipeline

  LicensingSupportTypeDeterminer(pipeline) {
    this.pipeline = pipeline
  }

  LicensingSupportType determineLicensingSupportType() {
    def projectTypesDeterminer = new ProjectTypesDeterminer(pipeline)
    def projectTypes = projectTypesDeterminer.determineProjectTypes('.')
    if (projectTypes.contains(ProjectTypes.JAVA)) {
      if (pipeline.sh(script: 'grep -c "com.github.hierynomus.license" build.gradle',
        returnStatus: true) == 0) {
        return LicensingSupportType.GRADLE_HIERYNOMUS_LICENSE
      }
    } else if (projectTypes.contains(ProjectTypes.RUBY)) {
      if (pipeline.sh(script: 'grep -c "license_finder" package.json', returnStatus: true) == 0) {
        return LicensingSupportType.RUBY_LICENSE_FINDER
      }
    }
    LicensingSupportType.NONE
  }
}
