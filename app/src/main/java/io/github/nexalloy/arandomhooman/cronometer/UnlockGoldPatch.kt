package io.github.nexalloy.arandomhooman.cronometer

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import io.github.nexalloy.patch
import java.io.File
import java.io.RandomAccessFile
import java.util.Locale
import kotlin.concurrent.thread

val UnlockGoldPatch = patch(
    name = "Unlock Gold",
    description = "Unlocks Cronometer Gold features, custom charts, advanced reports, fasting tracker, and ad-free view.",
    use = true
) {
    // 1. Safe SharedPreferencesImpl hook (concrete class, NEVER interface)
    runCatching {
        val prefImplClass = XposedHelpers.findClassIfExists("android.app.SharedPreferencesImpl", classLoader)
            ?: XposedHelpers.findClassIfExists("android.app.SharedPreferencesImpl", ClassLoader.getSystemClassLoader())

        if (prefImplClass != null) {
            val getBooleanMethod = XposedHelpers.findMethodExactIfExists(
                prefImplClass,
                "getBoolean",
                String::class.java,
                Boolean::class.javaPrimitiveType
            )
            getBooleanMethod?.let {
                XposedBridge.hookMethod(it, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val key = (param.args[0] as? String)?.lowercase(Locale.ROOT) ?: return
                        if (key.contains("gold") || key.contains("subscriber") || key.contains("subscription") || key.contains("premium")) {
                            param.result = true
                        }
                    }
                })
            }
        }
    }

    // 2. Google Play Billing purchase spoofing
    runCatching {
        val purchaseClass = XposedHelpers.findClassIfExists("com.android.billingclient.api.Purchase", classLoader)
        if (purchaseClass != null) {
            XposedHelpers.findMethodExactIfExists(purchaseClass, "getPurchaseState")?.let {
                XposedBridge.hookMethod(it, XC_MethodReplacement.returnConstant(1)) // PURCHASED
            }
            XposedHelpers.findMethodExactIfExists(purchaseClass, "isAcknowledged")?.let {
                XposedBridge.hookMethod(it, XC_MethodReplacement.returnConstant(true))
            }
        }
    }

    // 3. Native Flutter memory patch in libapp.so (arandomhooman signature)
    runCatching {
        patchCronometerNative()
    }
}

private fun patchCronometerNative() {
    thread(name = "CronometerGoldPatcher", isDaemon = true) {
        try {
            var attempts = 0
            while (attempts < 20) {
                attempts++
                val mapsFile = File("/proc/self/maps")
                if (!mapsFile.exists()) break

                var targetStart = 0L
                var targetEnd = 0L
                mapsFile.forEachLine { line ->
                    if (line.contains("libapp.so") && line.contains("r-x")) {
                        val parts = line.split(" ")[0].split("-")
                        if (parts.size == 2) {
                            runCatching {
                                targetStart = parts[0].toLong(16)
                                targetEnd = parts[1].toLong(16)
                            }
                        }
                    }
                }

                if (targetStart != 0L && targetEnd > targetStart) {
                    val size = (targetEnd - targetStart).toInt().coerceAtMost(40 * 1024 * 1024)
                    runCatching {
                        RandomAccessFile("/proc/self/mem", "rw").use { memFile ->
                            memFile.seek(targetStart)
                            val buffer = ByteArray(size)
                            memFile.readFully(buffer)

                            // ARM64 signature from arandomhooman/hoomans-morphe-patches:
                            // tbnz w0, #4, +0xc
                            // add  x2, NULL, #0x20
                            // b    +0x8
                            // add  x2, NULL, #0x30
                            // ldur x0, [fp, #-0x18]
                            // ldur x1, [fp, #-8]
                            // stur w2, [x0, #0x63]
                            val sig = byteArrayOf(
                                0x60.toByte(), 0x00.toByte(), 0x20.toByte(), 0x37.toByte(),
                                0xC2.toByte(), 0x82.toByte(), 0x00.toByte(), 0x91.toByte(),
                                0x02.toByte(), 0x00.toByte(), 0x00.toByte(), 0x14.toByte(),
                                0xC2.toByte(), 0xC2.toByte(), 0x00.toByte(), 0x91.toByte(),
                                0xA0.toByte(), 0x83.toByte(), 0x5E.toByte(), 0xF8.toByte(),
                                0xA1.toByte(), 0x83.toByte(), 0x5F.toByte(), 0xF8.toByte(),
                                0x02.toByte(), 0x30.toByte(), 0x06.toByte(), 0xB8.toByte()
                            )

                            val matchIndex = indexOfPattern(buffer, sig)
                            if (matchIndex >= 0) {
                                // Overwrite leading tbnz with NOP (0x1F, 0x20, 0x03, 0xD5)
                                val nop = byteArrayOf(0x1F.toByte(), 0x20.toByte(), 0x03.toByte(), 0xD5.toByte())
                                memFile.seek(targetStart + matchIndex)
                                memFile.write(nop)
                                XposedBridge.log("[Alloy] Successfully patched Cronometer Gold in libapp.so at 0x" + java.lang.Long.toHexString(targetStart + matchIndex))
                                return@thread
                            }
                        }
                    }
                }
                Thread.sleep(500)
            }
        } catch (t: Throwable) {
            XposedBridge.log(t)
        }
    }
}

private fun indexOfPattern(data: ByteArray, pattern: ByteArray): Int {
    if (pattern.isEmpty() || data.size < pattern.size) return -1
    val limit = data.size - pattern.size
    outer@ for (i in 0..limit) {
        for (j in pattern.indices) {
            if (data[i + j] != pattern[j]) continue@outer
        }
        return i
    }
    return -1
}
