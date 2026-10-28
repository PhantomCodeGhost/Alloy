package io.github.nexalloy.hoodles.duolingo

import io.github.nexalloy.morphe.findClassDirect

val loggedInStateFingerprint = findClassDirect {
    findClass {
        matcher {
            usingStrings(listOf("LoggedIn(user="))
        }
    }.first()
}

val userClassFingerprint = findClassDirect {
    findClass {
        matcher {
            usingStrings(listOf("User(adsConfig=", ", id="))
        }
    }.first()
}

val buildConfigFingerprint = findClassDirect {
    findClass {
        matcher {
            usingStrings(listOf("BUILD_TARGET", "debug", "release"))
        }
    }.first()
}
