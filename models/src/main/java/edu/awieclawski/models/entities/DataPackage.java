package edu.awieclawski.models.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
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
@Table(name = DataPackage.TABLE_NAME)
@EqualsAndHashCode(callSuper = false)
public class DataPackage extends BaseEntity {

	public static final String TABLE_NAME = "packages";
	public static final String SEQ_NAME = "packages_id_seq";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
	@SequenceGenerator(name = SEQ_NAME, allocationSize = 1)
	@Setter(value = AccessLevel.PROTECTED)
	private Long id;

	@Column(name = "json_data", columnDefinition = "TEXT", updatable = false)
	private String jsonData;

	@Column(name = "url", length = 512, updatable = false)
	private String url;

	@Column(name = "end_point", length = 128, updatable = false)
	private String endPoint;

	@Column(name = "processed", columnDefinition = "boolean default false")
	private Boolean processed;

	@Column(name = "converted", columnDefinition = "boolean default false")
	private Boolean converted;

	@PrePersist
	private void initIfNull() {
		if (getProcessed() == null) {
			setProcessed(Boolean.FALSE);
		}
		if (getConverted() == null) {
			setConverted(Boolean.FALSE);
		}
	}

	@Override
	public String toString() {
		return "DataPackage [id=" + id + ", jsonData=" + jsonData + ", url=" + url + ", endPoint=" + endPoint
			+ ", processed=" + processed + ", converted=" + converted + "]";
	}

	public String getInfo() {
		return "DataPackage [id=" + id + ", url=" + url + "]";
	}

}
