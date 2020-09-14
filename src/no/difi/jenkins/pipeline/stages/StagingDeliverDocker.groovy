package no.difi.jenkins.pipeline.stages

import no.difi.jenkins.pipeline.Docker
import no.difi.jenkins.pipeline.Git
import no.difi.jenkins.pipeline.Jira
import no.difi.jenkins.pipeline.Maven

Jira jira
Git git
Docker dockerClient
Maven maven

void script(def params) {
    git.checkoutVerificationBranch()
    dockerClient.buildAndPublish params.stagingEnvironment, env.version
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
        env.errorMessage = "Technical task is closed directly by Jenkins without testing."
    }else{
        env.errorMessage = "Jira Admin has to close this issue manually since it has failed after integrated on master branch."
    }
    jira.stagingFailed()
    dockerClient.deletePublished params.stagingEnvironment, env.version
    if (maven.isMavenProject())
        maven.deletePublished params.stagingEnvironment, env.version
    git.deleteWorkBranch()
}

