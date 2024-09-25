package com.project.core.schedule;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;

import com.project.application.file.usecase.CleanUpDeactivatedFiles;
import com.project.core.support.annotation.ScheduledBatch;

import lombok.RequiredArgsConstructor;

@ScheduledBatch
@RequiredArgsConstructor
public class CleanUpFilesBatch {
	private final CleanUpDeactivatedFiles cleanUpDeactivatedFiles;

	@Scheduled(cron = "0 0 0 * * ?")
	public void run() {
		cleanUpDeactivatedFiles.clean(LocalDate.now());
	}

}
