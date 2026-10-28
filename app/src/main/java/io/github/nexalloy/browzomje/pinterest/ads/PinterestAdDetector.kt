package io.github.nexalloy.browzomje.pinterest.ads

import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

object PinterestAdDetector {
    private val BOOLEAN_MARKERS = setOf(
        "is_promoted",
        "is_active_ad",
        "is_cpc_ad",
        "is_third_party_ad",
        "is_downstream_promotion",
        "promoted_is_lead_ad"
    )

    private val STRING_MARKERS = setOf(
        "ad_destination_url",
        "pin_promotion_id",
        "pin_promotion_id_reformatted",
        "promoted_android_deep_link"
    )

    private val OBJECT_MARKERS = setOf(
        "ad_data"
    )

    private val markerCache = ConcurrentHashMap<Class<*>, List<Field>>()

    private fun getSerializedName(field: Field): String? {
        for (ann in field.declaredAnnotations) {
            val name = ann.annotationClass.java.simpleName
            if (name.contains("SerializedName") || ann.annotationClass.java.name.endsWith(".b")) {
                try {
                    val valueMethod = ann.annotationClass.java.getMethod("value")
                    val value = valueMethod.invoke(ann) as? String
                    if (value != null) return value
                } catch (_: Throwable) {
                }
            }
        }
        return field.name
    }

    private fun getMarkersForClass(clazz: Class<*>): List<Field> {
        return markerCache.computeIfAbsent(clazz) { c ->
            val markers = mutableListOf<Field>()
            var current: Class<*>? = c
            while (current != null && current != Any::class.java) {
                val name = current.name
                if (name.startsWith("java.") || name.startsWith("android.") || name.startsWith("kotlin.")) break
                for (field in current.declaredFields) {
                    val serializedName = getSerializedName(field)
                    if (serializedName in BOOLEAN_MARKERS || serializedName in STRING_MARKERS || serializedName in OBJECT_MARKERS) {
                        field.isAccessible = true
                        markers.add(field)
                    }
                }
                current = current.superclass
            }
            markers
        }
    }

    fun isAd(model: Any?): Boolean {
        if (model == null) return false
        val markers = getMarkersForClass(model.javaClass)
        for (field in markers) {
            try {
                val value = field.get(model) ?: continue
                if (value is Boolean && value) return true
                if (value is CharSequence && value.isNotEmpty()) return true
                if (value is Collection<*> && value.isNotEmpty()) return true
                if (value !is Boolean && value !is CharSequence && value !is Collection<*>) return true
            } catch (_: Throwable) {
            }
        }
        return false
    }

    fun filterFeedList(list: Any?) {
        if (list !is MutableList<*>) return
        try {
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (isAd(item)) {
                    iterator.remove()
                }
            }
        } catch (_: Throwable) {
        }
    }
}
