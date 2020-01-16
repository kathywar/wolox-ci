def call( def projenv, def maxtime, def sparsePath="" ) {
  return {
    timeout(time: maxtime) {
      withEnv(projenv) {

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
            env.REFSPEC="$GERRIT_REFSPEC:$GERRIT_REFSPEC"
            env.LOCAL_BRANCH=env.GERRIT_REFSPEC
          }
        } else if ( env.ghprbPullId ) {  // github pull request builder
          env.LOCAL_BRANCH="pull/$ghprbPullId/head"
          env.REFSPEC="refs/pull/$ghprbPullId/head:refs/pull/$ghprbPullId/head"
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
          def classDef = [[$class: 'RelativeTargetDirectory', relativeTargetDir: "ws/$repoName"],
                          [$class: 'WipeWorkspace']]
          if ( sparsePath && sparsePath != "" ) {
              echo "Using a sparse checkout to path $sparsePath"
              classDef = [[$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: sparsePath ]]],
                          [$class: 'RelativeTargetDirectory', relativeTargetDir: "ws/$repoName"],
                          [$class: 'WipeWorkspace']]
          }
          gitVars = checkout(
            changelog: false,
            poll: false,
            scm: [$class: 'GitSCM',
                  branches: [[name: "$LOCAL_BRANCH"]],
                  doGenerateSubmoduleConfigurations: false,
                  extensions: classDef,
                  submoduleCfg: [],
                  userRemoteConfigs: [[credentialsId: "$CREDENTIAL",
                                       url: url,
                                       refspec: "$REFSPEC"]]]
          )
        }

        env.GIT_COMMIT = gitVars.GIT_COMMIT
        env.GIT_BRANCH = gitVars.GIT_BRANCH

        env.WSDIR=env.WORKSPACE + '/ws'
        env.REPO_PATH=env.WSDIR + "/$repoName"
 
        println "Working dir =$env.WSDIR"
      }
    }
  }
}
