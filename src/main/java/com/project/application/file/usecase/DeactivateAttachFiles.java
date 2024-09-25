package com.project.application.file.usecase;

import java.util.List;

public interface DeactivateAttachFiles {

	void deactivate(List<Long> fileIds);
}
