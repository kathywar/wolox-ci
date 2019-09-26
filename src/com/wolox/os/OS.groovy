package wolox.com.os

enum osType { linux, windows, mac };

abstract class OS {
    private osType osKind;
    OS(string kind ) {
      osKind = kind as osType;
    }
    string name() { return osKind as String; }
    abstract int shell( string script ) { }
}
