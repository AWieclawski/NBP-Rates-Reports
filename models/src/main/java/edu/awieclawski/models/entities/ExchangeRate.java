package edu.awieclawski.models.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import edu.awieclawski.commons.dtos.enums.NbpType;
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
@Table(name = ExchangeRate.TABLE_NAME)
@EqualsAndHashCode(callSuper = false)
@Entity // could be called in Repository
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = ExchangeRate.DISCRIMINATOR, discriminatorType = DiscriminatorType.STRING)
public abstract class ExchangeRate extends BaseEntity {

	public static final String TABLE_NAME = "rates";
	public static final String DISCRIMINATOR = "disc";
	public static final String SEQ_NAME = "rates_id_seq";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
	@SequenceGenerator(name = SEQ_NAME, allocationSize = 1)
	@Setter(value = AccessLevel.PROTECTED)
	protected Long id;

	@Column(name = "nbp_table", length = 32, updatable = false)
	protected String nbpTable;

	@Column(name = "published", updatable = false, nullable = false)
	protected LocalDate published;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "currency_id", nullable = false)
	protected Currency currency;

	@Override
	public String toString() {
		String currToString = currency != null ? currency.toString() + "]" : "]";
		return "ExchangeRate [id=" + id + ", nbpTable=" + nbpTable + ", currency=" + currToString;
	}

	public String getInfo() {
		String currInfo = currency != null ? currency.getInfo() + "]" : "]";
		return "ExchangeRate [id=" + id + ", nbpTable=" + nbpTable + ", currency=" + currInfo;
	}

	@Transient
	@Setter(value = AccessLevel.NONE)
	protected NbpType nbpType;

}
