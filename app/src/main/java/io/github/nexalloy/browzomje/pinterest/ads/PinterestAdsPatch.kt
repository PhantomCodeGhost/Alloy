package io.github.nexalloy.browzomje.pinterest.ads

import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

val PinterestAdsPatch = patch(
    name = "Disable ads",
    description = "Removes sponsored pins and promoted items from home, search, related and board feeds.",
    use = true
) {
    fun hookFeedResponseClass(clazz: Class<*>) {
        clazz.constructors.forEach { ctor ->
            ctor.hookMethod {
                after { param ->
                    param.args.forEach { arg ->
                        if (arg is MutableList<*>) {
                            PinterestAdDetector.filterFeedList(arg)
                        }
                    }
                    // Also scan fields on the constructed response object
                    val target = param.thisObject ?: return@after
                    for (field in target.javaClass.declaredFields) {
                        if (List::class.java.isAssignableFrom(field.type)) {
                            field.isAccessible = true
                            try {
                                PinterestAdDetector.filterFeedList(field.get(target))
                            } catch (_: Throwable) {
                            }
                        }
                    }
                }
            }
        }
    }

    // 1. Hook constructors of feed containers
    try {
        val feedClass = ::feedClassFingerprint.clazz
        hookFeedResponseClass(feedClass)

        // 2. Hook feed items getter to ensure subsequent pages scrolled are sanitized
        feedClass.declaredMethods.filter {
            it.parameterTypes.isEmpty() && List::class.java.isAssignableFrom(it.returnType)
        }.forEach { method ->
            method.hookMethod {
                after { param ->
                    PinterestAdDetector.filterFeedList(param.result)
                }
            }
        }
    } catch (_: Throwable) {
    }

    try {
        val pagedResponseClass = ::pagedResponseClassFingerprint.clazz
        hookFeedResponseClass(pagedResponseClass)
    } catch (_: Throwable) {
    }

    try {
        val modelListClass = ::modelListWithBookmarkClassFingerprint.clazz
        hookFeedResponseClass(modelListClass)
    } catch (_: Throwable) {
    }
}
