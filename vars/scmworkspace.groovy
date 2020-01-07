def call( def os, def projenv, def maxtime) {
  return {
    timeout(time: maxtime) {
      withEnv(projenv) {
        ArrayList list0 = []
        list0.add("printenv | sort")
        def closure ="$os"(list0)
        closure()

        env.REFSPEC="+refs/heads/*:refs/remotes/*"

        if ( env.CHANGE_BRANCH ) {
          env.LOCAL_BRANCH=env.CHANGE_BRANCH  //build triggered by multibranch job
        } else if ( env.BRANCH_NAME ) {
          env.LOCAL_BRANCH=env.BRANCH_NAME
        } else if ( env.GERRIT_BRANCH ) { // build triggered by gerrit change
          env.LOCAL_BRANCH=env.GERRIT_BRANCH
          if ( GERRIT_EVENT_TYPE=='change-merged') {
            env.LOCAL_BRANCH=env.GERRIT_NEWREV
          } else {
            env.REFSPEC="$env.GERRIT_REFSPEC:$env.GERRIT_REFSPEC"
            env.LOCAL_BRANCH=env.GERRIT_REFSPEC
          }
        } else if ( env.ghprbPullId ) {  // github pull request builder
          env.LOCAL_BRANCH="pull/$env.ghprbPullId/head"
          env.REFSPEC="refs/pull/$env.ghprbPullId/head:refs/pull/$env.ghprbPullId/head"
        } else if ( env.GIT_BRANCH ) {
          def str=env.GIT_BRANCH
          env.LOCAL_BRANCH=str.substring(str.lastIndexOf("origin/") + 7, str.length())
        } else {
          println "Jenkins did not set a branch name, using DEFAULT_BRANCH set in Jenkinsfile"
          env.LOCAL_BRANCH=env.DEFAULT_BRANCH
        }

        def url = scm.getUserRemoteConfigs()[0].getUrl()
        def repoName = url.tokenize('/').last().split("\\.git")[0]

        println "LOCAL BRANCH: " + env.LOCAL_BRANCH
        println "REFSPEC: " + env.REFSPEC

        script {
          println "Credential: $env.CREDENTIAL"
          gitVars = checkout([$class: 'GitSCM', 
            branches: [[name: "$LOCAL_BRANCH"]], 
            doGenerateSubmoduleConfigurations: false, 
            extensions: [[$class: 'RelativeTargetDirectory', 
            relativeTargetDir: 'ws'], [$class: 'WipeWorkspace']], 
            submoduleCfg: [], 
            userRemoteConfigs: [[credentialsId: env.CREDENTIAL, 
            refspec: "$REFSPEC", 
            url: url ]]])
        }

        env.GIT_COMMIT = gitVars.GIT_COMMIT
        env.GIT_LOCAL_BRANCH = gitVars.GIT_LOCAL_BRANCH
        env.GIT_BRANCH = gitVars.GIT_BRANCH

        println "Local branch is $env.GIT_LOCAL_BRANCH"

        env.WSDIR=env.WORKSPACE + '/ws'
        env.REPO_PATH=env.WSDIR + "/$repoName"
 
        println "Workdir=$env.WSDIR"
      }
    }
  }
}
