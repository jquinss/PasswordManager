package com.jquinss.passwordmanager.util.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    private ZipUtil() {}

    public static void zip(List<Path> files, Path zipFile, boolean replaceFile) throws IOException {
        try (ZipOutputStream zipOut = new ZipOutputStream(getOutputStream(zipFile, replaceFile))) {
            for (Path file : files) {
                zipOut.putNextEntry(new ZipEntry(file.getFileName().toString()));
                Files.copy(file, zipOut);
                zipOut.closeEntry();
            }
        }
    }

    private static OutputStream getOutputStream(Path filePath, boolean replaceFile) throws IOException {
        if (replaceFile) {
            return Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        else {
            return Files.newOutputStream(filePath);
        }
    }

    public static void unzip(Path zipFile, Path dstDirPath, boolean replaceFiles) throws IOException {
        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                Path outputFile = dstDirPath.resolve(zipEntry.getName());

                if (zipEntry.isDirectory()) {
                    Files.createDirectories(outputFile);
                }
                else {
                    Files.createDirectories(outputFile.getParent());
                    if (replaceFiles) {
                        Files.copy(zipIn, outputFile, StandardCopyOption.REPLACE_EXISTING);
                    }
                    else {
                        Files.copy(zipIn, outputFile);
                    }

                }

                zipIn.closeEntry();
            }
        }
    }
}
