package gov.ca.cwds.jenkins.licensing

import gov.ca.cwds.jenkins.utils.ProjectUtils

class LicensingSupportUtils implements Serializable {
    final static def LICENSE_FOLDER = 'legal'

    static LicensingSupportType getLicensingSupportType(script) {
        def result = LicensingSupportType.NONE
        if (ProjectUtils.hasGradleBuildFile(script)) {
            if (script.sh(script: 'grep -c "com.github.hierynomus.license" build.gradle',
                    returnStatus: true) == 0) {
                result = LicensingSupportType.GRADLE_HIERYNOMUS_LICENSE
            }
        } else if (ProjectUtils.hasPackageJsonFile(script)) {
            if (script.sh(script: 'grep -c "license_finder" package.json', returnStatus: true) == 0) {
                result = LicensingSupportType.RUBY_LICENSE_FINDER
            }
        }
        result
    }

    static def addLicensingGradleTasks(script) {
        def source = script.readFile file: 'build.gradle'
        source += 'task deleteLicenses(type: Delete) {\n' +
                '    delete "${buildDir}/reports/license", "${projectDir}/legal"\n' +
                '}\n' +
                'task copyLicenses(type: Copy) {\n' +
                '    from "${buildDir}/reports/license"\n' +
                '    into "${projectDir}/legal"\n' +
                '}'
        script.writeFile file:'build.gradle', text: "$source"
    }
}
