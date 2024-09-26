package com.project.application.notice.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.ObjectUtils;

import com.project.core.support.jpa.OperatorEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_notice")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class NoticeEntity extends OperatorEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@Column(name = "content", nullable = false, length = 2000)
	private String content;

	@Column(name = "from_date_time", nullable = false)
	private LocalDateTime from;

	@Column(name = "to_date_time", nullable = false)
	private LocalDateTime to;

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "notice_id")
	private List<NoticeFileEntity> files = new ArrayList<>();

	@Builder.Default
	@ElementCollection
	@CollectionTable(
		name = "tb_notice_view",
		joinColumns = @JoinColumn(name = "notice_id")
	)
	private List<Long> viewUsers = new ArrayList<>();

	public void modify(NoticeEntity.NoticeEntityBuilder builder) {
		this.title = builder.title;
		this.content = builder.content;
		this.from = builder.from;
		this.to = builder.to;
		this.linkFiles(builder.files$value);
	}

	public boolean isInvalidDuration() {
		return this.from.isAfter(this.to);
	}

	public boolean isNotAuthor(Long accountId) {
		return !this.getCreatedBy().equals(accountId);
	}

	public boolean isNotViewed(Long accountId) {
		return !this.viewUsers.contains(accountId);
	}

	public void view(Long accountId) {
		if (ObjectUtils.isEmpty(viewUsers)) {
			this.viewUsers = new ArrayList<>();
		}
		this.viewUsers.add(accountId);
	}

	public Integer getViewCount() {
		return ObjectUtils.isEmpty(viewUsers) ? 0 : this.viewUsers.size();
	}

	public void linkFiles(List<NoticeFileEntity> newFiles) {
		if (ObjectUtils.isEmpty(this.files)) {
			this.files.addAll(newFiles);
			this.files.forEach(item -> item.link(this.id));
			return;
		}

		mergeLinkFiles(newFiles);
	}

	private void mergeLinkFiles(List<NoticeFileEntity> newFiles) {
		Set<Long> newFileIds = newFiles.stream()
			.map(NoticeFileEntity::getFileId)
			.collect(Collectors.toSet());

		/* 삭제 정보 제외 */
		this.files.removeIf(file -> !newFileIds.contains(file.getFileId()));

		/* 신규 정보 추가 */
		newFiles.forEach(newFile -> {
			if (this.files.stream().noneMatch(file -> file.getFileId().equals(newFile.getFileId()))) {
				newFile.link(this.id);
				this.files.add(newFile);
			}
		});
	}

	public List<Long> getRemoveFileIds(List<Long> newFileIds) {
		return this.files.stream()
			.map(NoticeFileEntity::getFileId)
			.filter(fileId -> !newFileIds.contains(fileId))
			.toList();
	}

	public List<Long> getFileIds() {
		return this.files.stream()
			.map(NoticeFileEntity::getFileId)
			.toList();
	}
}
