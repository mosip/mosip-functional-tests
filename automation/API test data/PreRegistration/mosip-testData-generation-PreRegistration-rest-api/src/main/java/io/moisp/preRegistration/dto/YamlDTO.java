package io.moisp.preRegistration.dto;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YamlDTO {
	private Map<String,Map<String,List<Object>>> preReg;
}
