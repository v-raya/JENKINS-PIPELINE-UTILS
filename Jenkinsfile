@Library('jenkins-pipeline-utils') _

switch(env.BUILD_JOB_TYPE) {
  case "master": buildMaster(); break;
  default: buildPullRequest();
}

/*def buildPullRequest() {*/
/*  node('linux') {*/
/*    try {*/
/*      checkoutStage()*/
/*      buildDockerImageForTestStage()*/
/*      lintingStage()*/
/*      buildDockerImageStage()*/
/*      checkForLabelPullRequest()*/
/*      unitTestStage()*/
/*    } catch(Exception exception) {*/
/*      currentBuild.result = "FAILURE"*/
/*      throw exception*/
/*    } finally {*/
/*      cleanupStage()*/
/*    }*/
/*  }*/
/*}*/

def buildPullRequest() {
  node('linux') {
    try {
      checkoutStage()
      docker.image('groovy:alpine').inside {
        lintingStage()
      }
      /*unitTestStage()*/
      /*checkForLabelPullRequest()*/
    } catch(Exception exception) {
      currentBuild.result = "FAILURE"
      throw exception
    } finally {
      cleanWs()
    }
  }
}

def checkoutStage() {
  stage('Checkout') {
    deleteDir()
    checkout scm
  }
}

def lintingStage() {
  stage('Linting') {
    echo "Should run ./gradlew"
    sh './gradlew check'
  }
}

