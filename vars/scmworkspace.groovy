import com.wolox.*

def call( def os, def projenv, def maxtime) {
  return {
    timeout(time: maxtime) {
      withEnv(projenv) {
        if ( env.CHANGE_BRANCH ) {
          env.LOCAL_BRANCH=env.CHANGE_BRANCH  //build triggered by multibranch job
        } else if ( env.BRANCH_NAME ) {
          env.LOCAL_BRANCH=env.BRANCH_NAME
        } else if ( env.GERRIT_BRANCH ) { // build triggered by gerrit change
          env.LOCAL_BRANCH=env.GERRIT_BRANCH
          if ( GERRIT_EVENT_TYPE=='change-merged') {
            env.TREEISH=env.GERRIT_NEWREV
          } else {
            env.CHANGE_SPEC=env.GERRIT_REFSPEC
            env.TREEISH=env.GERRIT_BRANCH
          }
        } else if ( env.ghprbPullId ) {  // github pull request builder
          env.LOCAL_BRANCH=env.ghprbTargetBranch
          env.CHANGE_SPEC=env.ghprbPullId
          env.TREEISH=env.ghprbActualCommit
        } else if ( env.GIT_BRANCH ) {
          def str=env.GIT_BRANCH
          env.LOCAL_BRANCH=str.substring(str.lastIndexOf("origin/") + 7, str.length())
        } else {
          println "Jenkins did not set a branch name, using DEFAULT_BRANCH set in Jenkinsfile"
          env.LOCAL_BRANCH=env.DEFAULT_BRANCH
        }

        def url = scm.getUserRemoteConfigs()[0].getUrl()
        def repoName = url.tokenize('/').last().split("\\.git")[0]
        script {

          println "Credential: $env.CREDENTIAL"
          gitVars = dir("ws/$repoName") {
            git changelog: false,
            credentialsId: env.CREDENTIAL,
            poll: false,
            url: "$url",
            branch: "$env.LOCAL_BRANCH"
          }

          if ( env.CHANGE_SPEC ) {
            os "cd ws/$repoName && git fetch origin $env.CHANGE_SPEC:jenkins-branch"
          }

          env.GIT_COMMIT = gitVars.GIT_COMMIT
          env.GIT_LOCAL_BRANCH = gitVars.GIT_LOCAL_BRANCH
          env.GIT_BRANCH = gitVars.GIT_BRANCH

          println "Local branch is $env.GIT_LOCAL_BRANCH"

        }

        env.WSDIR=env.WORKSPACE + '/ws'
        env.REPO_PATH=env.WSDIR + "/$repoName"
 
        println "Workdir=$env.WSDIR"
      }
    }
  }
}
