package com.project.application.file.domain;

import java.time.LocalDateTime;

import com.project.core.support.jpa.OperatorEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_file")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class AttachFileEntity extends OperatorEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "original_filename", length = 50, nullable = false)
	private String originalFilename;

	@Column(name = "physical_filename", length = 100, nullable = false)
	private String physicalFilename;

	@Column(name = "content_type", length = 50, nullable = false)
	private String contentType;

	@Column(name = "extension", length = 10, nullable = false)
	private String extension;

	@Column(name = "dir_path", length = 500, nullable = false)
	private String dirPath;

	@Column(name = "file_size", nullable = false)
	private Long fileSize;

	@Column(name = "create_file_date_time", nullable = false)
	private LocalDateTime createFileDateTime;

	@Column(name = "last_modified_file_date_time", nullable = false)
	private LocalDateTime lastModifiedFileDateTime;

	@Column(name = "last_access_file_date_time", nullable = false)
	private LocalDateTime lastAccessFileDateTime;

	@Builder.Default
	@Column(name = "active_flag")
	private Boolean active = false;

	public void active() {
		this.active = true;
	}

	public void deactivate() {
		this.active = false;
	}

	public boolean isActive() {
		return this.active != null && this.active;
	}

	public boolean isDeactivate() {
		return !isActive();
	}
}
