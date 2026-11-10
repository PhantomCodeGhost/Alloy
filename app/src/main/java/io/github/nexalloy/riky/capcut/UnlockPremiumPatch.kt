package io.github.nexalloy.riky.capcut

import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.hookMethod
import io.github.nexalloy.patch

/**
 * Forces CapCut VIP gates to true.
 * Ported from riky-dev/morphe-patches (https://github.com/riky-dev/morphe-patches).
 * Targets:
 * - VipSubscribeManager (com.vega.subscribe.r.f)
 * - VipStatusGetterImpl (com.vega.subscribe.p.a)
 * - SubscribeImpl (com.vega.subscribe.h.a & b)
 * - BusinessImpl (com.lemon.editor.c.d.a)
 * - UserVipInfo (com.lm.components.subscribe.config.UserVipInfo.isVipUser & getFlag)
 * - Benefit (com.vega.subscriptionapi.b.b.b)
 */
val UnlockPremiumPatch = patch(
    name = "Unlock Premium",
    description = "Forces CapCut VIP gates to true (VipSubscribeManager, SubscribeImpl, UserVipInfo, benefits).",
) {
    val trueMethods = listOf(
        Triple("com.vega.subscribe.r", "f", emptyArray<Class<*>>()),
        Triple("com.vega.subscribe.p", "a", emptyArray<Class<*>>()),
        Triple("com.vega.subscribe.h", "a", emptyArray<Class<*>>()),
        Triple("com.vega.subscribe.h", "b", emptyArray<Class<*>>()),
        Triple("com.lemon.editor.c.d", "a", emptyArray<Class<*>>()),
        Triple("com.lm.components.subscribe.config.UserVipInfo", "isVipUser", emptyArray<Class<*>>()),
        Triple("com.lm.components.subscribe.config.UserVipInfo", "getFlag", emptyArray<Class<*>>()),
        Triple("com.vega.subscriptionapi.b.b", "b", emptyArray<Class<*>>()),
    )

    trueMethods.forEach { (className, methodName, params) ->
        runCatching {
            val clazz = XposedHelpers.findClassIfExists(className, classLoader) ?: return@forEach
            val method = XposedHelpers.findMethodExactIfExists(clazz, methodName, *params) ?: return@forEach
            method.hookMethod {
                before { param ->
                    if (method.returnType == Boolean::class.javaPrimitiveType || method.returnType == java.lang.Boolean::class.java) {
                        param.result = true
                    } else if (method.returnType == Int::class.javaPrimitiveType || method.returnType == java.lang.Integer::class.java) {
                        param.result = 1
                    }
                }
            }
        }
    }

    // Dynamic fallback for UserVipInfo or VIP model methods
    runCatching {
        val userVipClass = XposedHelpers.findClassIfExists("com.lm.components.subscribe.config.UserVipInfo", classLoader)
        if (userVipClass != null) {
            userVipClass.declaredMethods.forEach { m ->
                if ((m.name == "isVipUser" || m.name == "getFlag" || m.name.lowercase().contains("vip")) &&
                    (m.returnType == Boolean::class.javaPrimitiveType || m.returnType == Int::class.javaPrimitiveType) &&
                    m.parameterCount == 0
                ) {
                    m.hookMethod {
                        before { param ->
                            param.result = if (m.returnType == Boolean::class.javaPrimitiveType) true else 1
                        }
                    }
                }
            }
        }
    }
}

