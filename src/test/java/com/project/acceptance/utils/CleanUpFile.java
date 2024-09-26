package com.project.acceptance.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CleanUpFile {

	@Value("${app.upload.file-path}")
	String uploadFilePath;

	public void execute() throws IOException {
		if (uploadFilePath == null) return;

		Path path = Paths.get(uploadFilePath);
		if (Files.notExists(path)) return;

		Files.walk(path)
			.sorted(Comparator.reverseOrder())
			.map(Path::toFile)
			.forEach(File::delete);
	}
}
