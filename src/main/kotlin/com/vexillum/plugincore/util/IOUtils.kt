package com.vexillum.plugincore.util

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

fun copyFilesFromPath(
    sourcePath: Path,
    destination: Path,
    copyOption: StandardCopyOption = REPLACE_EXISTING
) {
    Files.walk(sourcePath).use { paths ->
        paths.forEach { path ->
            val relativeToSource = sourcePath.relativize(path)
            val destinationPath = destination.resolve(relativeToSource.toString())
            if (Files.isDirectory(path)) {
                // Create the corresponding directory in the destination
                Files.createDirectories(destinationPath)
            } else if (Files.isRegularFile(path)) {
                // Copy the file to the corresponding destination path
                // Ensure parent directory exists
                Files.createDirectories(destinationPath.parent)
                Files.copy(path, destinationPath, copyOption)
            }
        }
    }
}
