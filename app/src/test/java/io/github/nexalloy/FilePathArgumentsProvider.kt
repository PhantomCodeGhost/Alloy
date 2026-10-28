package io.github.nexalloy

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.support.ParameterDeclarations
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Stream
import kotlin.io.path.Path
import kotlin.io.path.name

class FilePathArgumentsProvider : ArgumentsProvider {
    override fun provideArguments(
        parameters: ParameterDeclarations,
        context: ExtensionContext
    ): Stream<out Arguments> {
        println(Path(".").toAbsolutePath())
        val projectDir = Paths.get(".") //.toAbsolutePath().normalize()
        val testInputPath = projectDir.resolve("binaries")

        val paths = if (Files.exists(testInputPath)) {
            Files.walk(testInputPath).use { files ->
                files.filter { path ->
                    Files.isRegularFile(path) && path.normalize().none { it.name.startsWith(".") }
                }.toList()
            }
        } else {
            emptyList()
        }

        if (paths.isEmpty()) {
            println("No test APKs found in $testInputPath; skipping fingerprint tests")
            return Stream.of(Arguments.of(testInputPath.resolve("placeholder")))
        }

        FingerprintTestReport.start(paths)
        return paths.stream().map { Arguments.of(it) }
    }
}
