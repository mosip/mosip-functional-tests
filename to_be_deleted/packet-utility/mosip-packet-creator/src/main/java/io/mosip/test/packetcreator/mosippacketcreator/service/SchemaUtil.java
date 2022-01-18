package io.mosip.test.packetcreator.mosippacketcreator.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class SchemaUtil {

    public static final String PROPERTIES = "properties";
    public static final String IDENTITY = "identity";
    public static final String SCHEMA_CATEGORY = "fieldCategory";
    public static final String SCHEMA_VERSION_QUERY_PARAM = "schemaVersion";
    public static final String ID_VERSION = "IDSchemaVersion";

    @Value("${mosip.test.masterdata.idschemaapi}")
    private String schemaUrl;

    @Autowired
    private APIRequestUtil apiRequestUtil;

    @Value("#{${mosip.test.packet.mapping.id}}")
    private List<String> id_categories;

    @Value("#{${mosip.test.packet.mapping.evidence}}")
    private List<String> evidence_categories;

    @Value("#{${mosip.test.packet.mapping.optional}}")
    private List<String> optional_categories;

    public String getAndSaveSchema(String version, String workFolder) throws Exception{
        
        Path schemaFileLocation = Path.of(workFolder, "v"+version+".json");
        if (schemaFileLocation.toFile().exists()){
            return readSchemaAsString(schemaFileLocation.toFile().getAbsolutePath());
        }
        
        JSONObject queryParams = new JSONObject();
        queryParams.put(SCHEMA_VERSION_QUERY_PARAM, version);
        JSONObject response = apiRequestUtil.get(schemaUrl, queryParams, new JSONObject());
        
        try(FileOutputStream fos = new FileOutputStream(schemaFileLocation.toString())){
                String schemaData = response.getString("schemaJson");
                fos.write(schemaData.getBytes());
                fos.flush();
                return readSchemaAsString(schemaFileLocation.toFile().getAbsolutePath());
            }
    }

    public String readSchemaAsString(String file) throws IOException{
        return Files.readString(Path.of(file));
    }

    public String getIdSchemaVersion(String dataFile) {
        JSONObject data = new JSONObject(dataFile);
        data = data.getJSONObject(IDENTITY);
        return data.getString(ID_VERSION);
    }


    public JSONObject getPacketIDData(String schemaJson, String data, String category) {
        switch (category.toLowerCase()) {
            case "id":
                return getPacketIDData(schemaJson, id_categories, data);
            case "evidence":
                return getPacketIDData(schemaJson, evidence_categories, data);
            case "optional":
                return getPacketIDData(schemaJson, optional_categories, data);
            default:
                return getPacketIDData(schemaJson, id_categories, data);
        }
    }

    private JSONObject getPacketIDData(String schemaJson, List<String> categories, String dataFile) {
        JSONObject identity = new JSONObject();

        JSONObject data = new JSONObject(dataFile);
        data = data.getJSONObject(IDENTITY);

        JSONObject schema = new JSONObject(schemaJson);
        schema = schema.getJSONObject(PROPERTIES);
        schema = schema.getJSONObject(IDENTITY);
        schema = schema.getJSONObject(PROPERTIES);

        JSONArray fieldNames = data.names();
        for (int i = 0; i < fieldNames.length(); i++) {
            String fieldName = fieldNames.getString(i);
            JSONObject fieldDetail = schema.has(fieldName) ? schema.getJSONObject(fieldName) : new JSONObject();
            String fieldCategory = fieldDetail.has(SCHEMA_CATEGORY) ? fieldDetail.getString(SCHEMA_CATEGORY) : "none";
            if(categories.contains(fieldCategory.toLowerCase())){
                identity.put(fieldName, data.get(fieldName));
            }
        }
        JSONObject identityWrapper = new JSONObject();
        identityWrapper.put("identity", identity);
        return identityWrapper;
    }
}
