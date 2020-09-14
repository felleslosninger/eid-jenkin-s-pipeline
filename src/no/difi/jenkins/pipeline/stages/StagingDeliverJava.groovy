package no.difi.jenkins.pipeline.stages

import no.difi.jenkins.pipeline.Git
import no.difi.jenkins.pipeline.Jira
import no.difi.jenkins.pipeline.Maven

Jira jira
Git git
Maven maven

void script(def params) {
    git.checkoutVerificationBranch()
    maven.deliver(env.version, params.MAVEN_OPTS, params.parallelMavenDeploy, params.stagingEnvironment)
}

void failureScript(def params) {
    cleanup(params)
    jira.addFailureComment()
}

void abortedScript(def params) {
    cleanup(params)
    jira.addAbortedComment()
}

private void cleanup(def params) {
    if (jira.isTechTask()) {
        env.errorMessage = "Is disk full on Artifactory? Technical task is closed directly by Jenkins without testing."
    }else{
        env.errorMessage = "Is disk full on Artifactory? Jira Admin has to close this issue manually since it has failed after integrated on master branch."
    }
    jira.stagingFailed()
    maven.deletePublished params.stagingEnvironment, env.version
    git.deleteWorkBranch()
}
