package com.project.application.file.usecase;

import java.time.LocalDate;

public interface CleanUpDeactivatedFiles {

	void clean(LocalDate now);
}
