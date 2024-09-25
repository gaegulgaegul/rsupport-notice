package com.project.application.file.usecase;

import java.util.List;

import com.project.application.file.vo.AttachFileInfo;

public interface GetAttachFiles {

	List<AttachFileInfo> read(List<Long> fileIds);
}
