package com.project.application.file.usecase;

import java.util.List;

public interface CheckFileOwner {

	void check(Long accountId, List<Long> fileIds);
}
