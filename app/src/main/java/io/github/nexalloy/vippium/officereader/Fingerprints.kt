package io.github.nexalloy.vippium.officereader

import io.github.nexalloy.morphe.findClassDirect

val optionServiceClass = findClassDirect {
    runCatching {
        findClass {
            matcher {
                className("net.sjava.officereader.services.OptionService")
            }
        }.single()
    }.getOrElse {
        findClass {
            matcher {
                addMethod {
                    name = "isPurchased"
                    returnType = "boolean"
                }
            }
        }.first()
    }
}

val aboutFragmentClass = findClassDirect {
    findClass {
        matcher {
            className("net.sjava.officereader.ui.fragments.AboutFragment")
        }
    }.single()
}
