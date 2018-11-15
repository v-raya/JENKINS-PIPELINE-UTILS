package gov.ca.cwds.jenkins.licensing

enum LicensingSupportType {
  NONE('None'),
  GRADLE_HIERYNOMUS_LICENSE('Gradle Hierynomus License Plugin'),
  RUBY_LICENSE_FINDER('Ruby License Finder Plugin');

  def title

  LicensingSupportType(title) {
    this.title = title
  }
}
