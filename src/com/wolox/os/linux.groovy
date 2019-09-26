package com.wolox.os

class linux extends os {

    int shell( string script ) {
        shell sh returnStdout:true,
                 script: """
                      $script
                      """
    }
}
