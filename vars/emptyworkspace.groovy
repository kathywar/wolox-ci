import com.wolox.*

def call(String os, def projenv, def maxtime) {
  return {
    timeout(time: maxtime) {
      withEnv(projenv) {
          env.WSDIR=env.WORKSPACE + '/ws'
          fileOperations([folderCreateOperation(env.WSDIR)])
          println "Workdir=$env.WSDIR"
      }
    }
  }
}
