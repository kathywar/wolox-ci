import com.wolox.*;

def call(def projenv, def maxtime) {
  return {
    timeout(time: maxtime) {
      withEnv(projenv) {
          if ( env.CHANGE_BRANCH ) {
            env.LOCAL_BRANCH=env.CHANGE_BRANCH
          } else if ( env.BRANCH_NAME ) {
            env.LOCAL_BRANCH=env.BRANCH_NAME
          } else {
            env.LOCAL_BRANCH='master'
          }
 
          println "Branch is $env.BRANCH_NAME"
          println "Local branch is $env.GIT_LOCAL_BRANCH"
          env.WSDIR=env.WORKSPACE + '/ws'

          fileOperations([folderCreateOperation(env.WSDIR)])
          env.WSDIR=env.WORKSPACE + '/ws'
          println "Workdir=$env.WSDIR"
      }
    }
  }
}
