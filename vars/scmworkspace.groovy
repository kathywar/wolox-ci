import com.wolox.*;

def call( def projenv, def maxtime) {
  return {
    timeout(time: maxtime) {
      withEnv(projenv) {
        def url = scm.getUserRemoteConfigs()[0].getUrl()
        def repoName = url.tokenize('/').last().split("\\.git")[0]
        echo sh(returnStdout: true, script: 'printenv | sort')
        script {

          if ( env.CHANGE_BRANCH ) {
            env.LOCAL_BRANCH=env.CHANGE_BRANCH
          } else if ( env.BRANCH_NAME ) {
            env.LOCAL_BRANCH=env.BRANCH_NAME
          } else if ( env.GERRIT_BRANCH ) {
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
          } else {
              env.LOCAL_BRANCH='master'
          }

          println "Credential: $env.CREDENTIAL" 
          gitVars = dir("ws/$repoName") {
            git changelog: false,
            credentialsId: env.CREDENTIAL,
            poll: false,
            url: "$url",
            branch: "$env.LOCAL_BRANCH"
          }
          
          if ( env.CHANGE_SPEC ) {
              sh 'pwd'
              sh "cd ws/$repoName && git fetch origin $env.CHANGE_SPEC:jenkins-branch"
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
