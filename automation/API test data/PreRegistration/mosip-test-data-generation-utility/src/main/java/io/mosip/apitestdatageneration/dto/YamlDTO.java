package io.mosip.apitestdatageneration.dto;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YamlDTO {
	private Map<String,Map<String,List<Object>>> yamlData;
	private Map<String,List<Object>> yamlData2;
}
