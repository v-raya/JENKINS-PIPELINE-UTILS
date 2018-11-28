package gov.ca.cwds.jenkins.licensing

import gov.ca.cwds.jenkins.common.ProjectTypes

class LicensingSupportTypeDeterminer {
  def pipeline

  LicensingSupportTypeDeterminer(pipeline) {
    this.pipeline = pipeline
  }

  LicensingSupportType determineLicensingSupportType(buildMetadata) {
    def licensingSupportType = LicensingSupportType.NONE
    def projectTypes = buildMetadata.projectTypes()
    if (projectTypes.contains(ProjectTypes.JAVA)) {
      if (pipeline.sh(script: 'grep -c "com.github.hierynomus.license" build.gradle',
        returnStatus: true) == 0) {
        licensingSupportType = LicensingSupportType.GRADLE_HIERYNOMUS_LICENSE
      }
    } else if (projectTypes.contains(ProjectTypes.RUBY)) {
      if (pipeline.sh(script: 'grep -c "license_finder" package.json', returnStatus: true) == 0) {
        licensingSupportType = LicensingSupportType.RUBY_LICENSE_FINDER
      }
    }
    pipeline.echo "Detected Licensing Support Type: ${licensingSupportType.title}"
    licensingSupportType
  }
}
