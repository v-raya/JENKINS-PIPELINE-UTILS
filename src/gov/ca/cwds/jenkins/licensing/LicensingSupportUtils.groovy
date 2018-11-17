package gov.ca.cwds.jenkins.licensing

import gov.ca.cwds.jenkins.common.ProjectTypes
import gov.ca.cwds.jenkins.common.ProjectTypesDeterminer

class LicensingSupportUtils implements Serializable {
  final static def LICENSE_FOLDER = 'legal'
  final static def MSG_NO_LICENSING_SUPPORT = 'No known Licensing Support is found in the project'
  final static def ADDITIONAL_LICENSING_GRADLE_TASKS = '\ntask deleteLicenses(type: Delete) {\n' +
    '    delete "${buildDir}/reports/license", "${projectDir}/legal"\n' +
    '}\n' +
    'task copyLicenses(type: Copy) {\n' +
    '    from "${buildDir}/reports/license"\n' +
    '    into "${projectDir}/legal"\n' +
    '}'

  static LicensingSupportType getLicensingSupportType(pipeline) {
    def result = LicensingSupportType.NONE
    def projectTypesDeterminer = new ProjectTypesDeterminer(pipeline)
    def projectTypes = projectTypesDeterminer.determineProjectTypes('.')
    if (projectTypes.contains(ProjectTypes.JAVA)) {
      if (pipeline.sh(script: 'grep -c "com.github.hierynomus.license" build.gradle',
        returnStatus: true) == 0) {
        result = LicensingSupportType.GRADLE_HIERYNOMUS_LICENSE
      }
    } else if (projectTypes.contains(ProjectTypes.RUBY)) {
      if (pipeline.sh(script: 'grep -c "license_finder" package.json', returnStatus: true) == 0) {
        result = LicensingSupportType.RUBY_LICENSE_FINDER
      }
    }
    result
  }

  static def addLicensingGradleTasks(pipeline) {
    def source = pipeline.readFile file: 'build.gradle'
    source += ADDITIONAL_LICENSING_GRADLE_TASKS
    pipeline.writeFile file: 'build.gradle', text: "$source"
  }
}
