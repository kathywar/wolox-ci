package com.wolox.os

class windows extends OS {

    int shell( string script ) {
        shell bat returnStdout:true,
                 script: """
                      c:\\u4win\\setenv.bat
                      . C:/ics/itools/win/bin/icssetup.ksh –start C:/ics/itools/win/bin
                      $script
                      """
    }
}
