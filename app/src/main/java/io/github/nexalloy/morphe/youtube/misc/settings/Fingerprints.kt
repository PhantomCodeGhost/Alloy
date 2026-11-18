package io.github.nexalloy.morphe.youtube.misc.settings

import io.github.nexalloy.morphe.findClassDirect
import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.findMethodListDirect
import io.github.nexalloy.morphe.resourceMappings
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

val PreferenceFragmentCompatClass = findClassDirect {
    findClass {
        matcher {
            usingStrings(
                "Could not create RecyclerView",
                "Content has view with id attribute 'android.R.id.list_container' that is not a ViewGroup class",
                "androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT"
            )
        }
    }.single()
}

val PreferenceFragmentCompat_addPreferencesFromResource = findMethodDirect {
    PreferenceFragmentCompatClass().let { preferenceFragmentCompat ->
        preferenceFragmentCompat.findMethod {
            matcher {
                returnType = "void"
                paramTypes("int")
            }
        }.singleOrNull() ?: preferenceFragmentCompat.findMethod {
            matcher {
                name = "addPreferencesFromResource"
            }
        }.single()
    }
}

val licenseActivityClass = findClassDirect {
    findClass {
        matcher {
            className(".LicenseActivity", StringMatchType.EndsWith)
        }
    }.first()
}

val licenseActivitySuperOnCreate = findMethodDirect {
    licenseActivityClass().superClass!!.findMethod { matcher { name = "onCreate" } }.first()
}

val licenseActivityOnCreateFingerprint = findMethodDirect {
    licenseActivityClass().findMethod { matcher { name = "onCreate" } }.first()
}

val licenseActivityNOTonCreate = findMethodListDirect {
    licenseActivityClass().methods.filter { it.name != "onCreate" && it.isMethod }
}

val appearanceStringId get() = runCatching { resourceMappings["string", "app_theme_appearance_dark"] }.getOrDefault(0)

val setThemeFingerprint = findMethodDirect {
    findMethod {
        matcher {
            modifiers = Modifier.PUBLIC or Modifier.FINAL
            paramCount = 0
            if (appearanceStringId != 0) {
                addUsingNumber(appearanceStringId)
            }
        }
    }.first { it.returnTypeName != "void" }
}