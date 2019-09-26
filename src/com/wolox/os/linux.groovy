package com.wolox.os

class linux extends OS {

    int shell( string script ) {
        shell sh returnStdout:true,
                 script: """
                      $script
                      """
    }
}
