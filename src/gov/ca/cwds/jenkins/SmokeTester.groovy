package gov.ca.cwds.jenkins

class SmokeTester {
  def script
  def path

  SmokeTester(script, path) {
    this.script = script
    this.path = path
  }
  
  def runSmokeTest(path) {
    def test = path.execute().text
    if (test.contains("smoketest failed")) {
        script.error ("'${test}'")
        return "smoke test failed"
    }
    script.echo "smoke test passed"
    return "smoke test passed"
  }
  
}
