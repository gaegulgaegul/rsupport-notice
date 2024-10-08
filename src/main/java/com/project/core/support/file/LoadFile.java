package com.project.core.support.file;

import org.springframework.core.io.Resource;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoadFile {
	private Resource resource;
	private String contentType;
}
