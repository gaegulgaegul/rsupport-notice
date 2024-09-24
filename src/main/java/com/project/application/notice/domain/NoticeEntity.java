package com.project.application.notice.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.project.core.support.OperatorEntity;

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
	private List<String> viewUsers = new ArrayList<>();

	public void link() {
		if (ObjectUtils.isEmpty(this.files)) {
			return;
		}

		this.files.forEach(item -> item.link(this.id));
	}
}
