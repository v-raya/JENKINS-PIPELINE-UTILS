package gov.ca.cwds.jenkins

class SmokeTester {
  def script

  SmokeTester(script) {
    this.script = script
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
