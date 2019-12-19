import com.wolox.*;

def call(def projenv, def maxtime) {
  return {
    timeout(time: maxtime) {
      withEnv(projenv) {
        def url = scm.getUserRemoteConfigs()[0].getUrl()
        def repoName = url.tokenize('/').last().split("\\.git")[0]

        script {

          if ( env.CHANGE_BRANCH ) {
            env.LOCAL_BRANCH=env.CHANGE_BRANCH
          } else if ( env.BRANCH_NAME ) {
            env.LOCAL_BRANCH=env.BRANCH_NAME
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
            
          env.GIT_COMMIT = gitVars.GIT_COMMIT
          env.GIT_LOCAL_BRANCH = gitVars.GIT_LOCAL_BRANCH
          env.GIT_BRANCH = gitVars.GIT_BRANCH

          println "Branch is $env.BRANCH_NAME"
          println "Local branch is $env.GIT_LOCAL_BRANCH"
            
        }

        env.WSDIR=env.WORKSPACE + '/ws'
        env.REPO_PATH=env.WSDIR + "/$repoName"

        println "Workdir=$env.WSDIR"

      }
    }
  }
}
