package com.leroy.utils;

import io.qameta.allure.Attachment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class FileUtils {
    private static final String RESOURCES_PATH = "src/test/resources";

    private FileUtils() {
    }

    @Attachment(value = "{shownFileName}", type = "application/json", fileExtension = "{extension}")
    public static byte[] attachFile(String shownFileName, String resourceName, String extension) throws IOException {
        return Files.readAllBytes(Paths.get(RESOURCES_PATH, resourceName));
    }

    @Attachment(value = "{shownFileName}", type = "image/jpg", fileExtension = "{extension}")
    public static byte[] attachPicture(String shownFileName, String resourceName, String extension) throws IOException {
        return Files.readAllBytes(Paths.get(RESOURCES_PATH, resourceName));
    }

}