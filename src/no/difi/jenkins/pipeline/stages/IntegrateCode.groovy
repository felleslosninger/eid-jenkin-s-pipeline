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
    if (jira.failIfCodeNotApproved()) {
        env.errorMessage = "Sent back from code review. Developer: Fix it! :)"
        error "Code review rejected by reviewer"
    }
    jira.createAndSetFixVersion env.version
    git.integrateCode()
    echo "Checking if should tag master..."
    if(params.tagMaster){
        git.tagMaster env.version
    }
    git.deleteVerificationBranch()
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
    git.deleteVerificationBranch()
    if (params.stagingEnvironment != null) {
        dockerClient.deletePublished params.stagingEnvironment, env.version
        if (maven.isMavenProject())
            maven.deletePublished params.stagingEnvironment, env.version
    }
    jira.resumeWork()
}
