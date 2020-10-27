package io.mosip.registrationProcessor.perf.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "location")
public class CodeAndLanguageCodeID implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9083898959072007739L;

	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;
}
