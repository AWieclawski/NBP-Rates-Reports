package edu.awieclawski.models.entities.bases;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class BaseEntity {

	@Column(name = "created_at", updatable = false)
	private final LocalDateTime createdAt = LocalDateTime.now();

	@Override
	public String toString() {
		return ", createdAt=" + createdAt + "]";
	}

}
