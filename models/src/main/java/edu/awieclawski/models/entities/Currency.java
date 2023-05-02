package edu.awieclawski.models.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import edu.awieclawski.models.entities.bases.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = Currency.TABLE_NAME)
@EqualsAndHashCode(callSuper = false)
public class Currency extends BaseEntity {
	public static final String TABLE_NAME = "currencies";
	public static final String SEQ_NAME = "currencies_id_seq";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
	@SequenceGenerator(name = SEQ_NAME, allocationSize = 1)
	@Setter(value = AccessLevel.PROTECTED)
	private Long id;

	@Column(name = "code", unique = true, length = 3, updatable = false)
	private String code;

	@Column(name = "description", updatable = false)
	private String description;

	@Override
	public String toString() {
		return "Currency [id=" + id + ", code=" + code + ", description=" + description + "]";
	}

	public String getInfo() {
		return "Currency [id=" + id + ", code=" + code + "]";
	}

}
