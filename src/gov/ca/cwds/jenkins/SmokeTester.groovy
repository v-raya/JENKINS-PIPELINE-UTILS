package gov.ca.cwds.jenkins

class SmokeTester {
  def script

  SmokeTester(script) {
    this.script = script
  }
  
  def runSmokeTest(path, url) {

    def cmd = [path, url]
    def test = cmd.execute().text
    if (test.contains("smoketest passed")) {
        script.echo "smoke test passed"
        return "smoke test passed"
    }
    script.error ("'${test}'")
  }
  
}
