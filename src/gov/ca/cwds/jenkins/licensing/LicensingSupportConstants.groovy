package gov.ca.cwds.jenkins.licensing

class LicensingSupportConstants {
  final static def GIT_USER = 'Jenkins'
  final static def GIT_EMAIL = 'cwdsdoeteam@osi.ca.gov'
  final static def LICENSE_FOLDER = 'legal'
  final static def MSG_NO_LICENSING_SUPPORT = 'No known Licensing Support is found in the project'
  final static def ADDITIONAL_LICENSING_GRADLE_TASKS = '\ntask deleteLicenses(type: Delete) {\n' +
    '    delete "${buildDir}/reports/license", "${projectDir}/legal"\n' +
    '}\n' +
    'task copyLicenses(type: Copy) {\n' +
    '    from "${buildDir}/reports/license"\n' +
    '    into "${projectDir}/legal"\n' +
    '}'
}
