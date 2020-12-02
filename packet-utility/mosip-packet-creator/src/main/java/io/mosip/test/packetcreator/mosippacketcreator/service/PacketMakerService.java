package io.mosip.test.packetcreator.mosippacketcreator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

@Service
public class PacketMakerService {
    
    Logger logger = LoggerFactory.getLogger(PacketMakerService.class);

    private static final String UNDERSCORE = "_";
    private static final String PACKET_META_FILENAME = "packet_meta_info.json";
    private static final String PACKET_DATA_HASH_FILENAME = "packet_data_hash.txt";

    @Value("${mosip.test.regclient.store:/home/sasikumar/Documents/MOSIP/packetcreator}")
    private String finalDestination;

    @Value("${mosip.test.packet.template.location:/home/sasikumar/Documents/MOSIP/packetcreator/template}")
    private String templateFolder;

    @Value("${mosip.test.packet.template.source:REGISTRATION_CLIENT}")
    private String src;

    @Value("${mosip.test.packet.template.process:NEW}")
    private String process;

    @Value("${mosip.test.regclient.centerid}")
    private String centerId;

    @Value("${mosip.test.regclient.machineid}")
    private String machineId;

    @Autowired
    private APIRequestUtil apiUtil;

    @Autowired
    private CryptoUtil cryptoUtil;

    @Autowired
    private ZipUtils zipper;

    @Autowired
    private SchemaUtil schemaUtil;

    private String workDirectory;
    private String defaultTemplateLocation;

    @Value("${mosip.test.rid.seq.initialvalue}")
    private int counter;

    @PostConstruct
    public void initService(){
        if (workDirectory != null) return;
        try{
            workDirectory = Files.createTempDirectory("pktcreator").toFile().getAbsolutePath();
            logger.info("CURRENT WORK DIRECTORY --> {}", workDirectory);
            File folder = new File(templateFolder);
            File templateName = folder.listFiles()[0];
            defaultTemplateLocation = templateName.getAbsolutePath();
        } catch(Exception ex) {
            logger.error("", ex);
        }
       
    } 

    public String createContainer(String dataFile, String templatePacketLocation) throws Exception{
        String templateLocation = (null == templatePacketLocation)?defaultTemplateLocation: templatePacketLocation;
        String regId = generateRegId();
        String tempPacketRootFolder = createTempTemplate(templateLocation, regId);
        createPacket(tempPacketRootFolder, regId, dataFile, "id");
        packPacket(getPacketRoot(getProcessRoot(tempPacketRootFolder), regId, "id"), regId, "id");
        createPacket(tempPacketRootFolder, regId, dataFile, "evidence");
        packPacket(getPacketRoot(getProcessRoot(tempPacketRootFolder), regId, "evidence"), regId, "evidence");
        createPacket(tempPacketRootFolder, regId, dataFile, "optional");
        packPacket(getPacketRoot(getProcessRoot(tempPacketRootFolder), regId, "optional"), regId, "optional");
        packContainer(tempPacketRootFolder);
        return Path.of(tempPacketRootFolder + ".zip").toString();
        
    }

    /**
     * 
     * @param templateFile - template folder location.
     * @param dataFile - JSON file name whose content has to be merged
     * @return - the merged JSON as a generic map Map<?,?>
     */
    public Map<?,?> mergeJSON(String templateFile, String dataFile) throws Exception{
        try (InputStream inputStream = new FileInputStream(dataFile) ) {
            String dataToMerge = new String(inputStream.readAllBytes(),StandardCharsets.UTF_8);
            JSONObject data = new JSONObject(dataToMerge);
            return mergeJSON(templateFile, data);
        }
    }

    /**
     * 
     * @param templateFile - template folder location.
     * @param data - JSONObject whose content has to be merged
     * @return - the merged JSON as a generic map Map<?,?>
     */
    public Map<?,?> mergeJSON(String templateFile, JSONObject data) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDefaultMergeable(false).configOverride(ArrayList.class).setMergeable(false);
        Map<?,?> genericJSONObject = objectMapper.readValue(Paths.get(templateFile).toFile(), Map.class);
        String dataToMerge = data.toString();
        ObjectReader objectReader = objectMapper.readerForUpdating(genericJSONObject);
        return objectReader.readValue(dataToMerge); 
    }

    public boolean createPacketRandom(String containerRootFolder, String regId, String templateFilePath, String type){
        //TODO: Create a file from the templateFilePath then call the normal createPacket
        return true;
    }

    public boolean createPacket(String containerRootFolder, String regId, String dataFilePath, String type) throws Exception{
        String packetRootFolder = getPacketRoot(getProcessRoot(containerRootFolder), regId, type);
        String templateFile = getIdJSONFileLocation(packetRootFolder);

        String dataToMerge = Files.readString(Path.of(dataFilePath));
        JSONObject jb = new JSONObject(dataToMerge).getJSONObject("identity");
        String schemaVersion = jb.optString("IDSchemaVersion", "0");
        String schemaJson = schemaUtil.getAndSaveSchema(schemaVersion, workDirectory);
        JSONObject jbToMerge = schemaUtil.getPacketIDData(schemaJson, dataToMerge, type);
        Map<?,?> mergedJsonMap = mergeJSON(templateFile, jbToMerge);
        if(!writeJSONFile(mergedJsonMap, templateFile)) {
            logger.error("Error creating packet {} ", regId);
            return false;
        }
        updateHashSequence(packetRootFolder);
        return true;
        //return packPacket(containerRootFolder, regId, type);
    }

    public boolean packPacket(String containerRootFolder, String regId, String type) throws Exception{
        //String packetRootFolder = getPacketRoot(getProcessRoot(containerRootFolder), regId, type);
        boolean result = zipAndEncrypt(Path.of(containerRootFolder));
        if (!result){
            logger.error("Encryption failed!!! ");
            return false;
        }

        String encryptedHash = cryptoUtil.getHash(Files.readAllBytes(Path.of(Path.of(containerRootFolder) + ".zip")));
        String signature = Base64.getEncoder().encodeToString(cryptoUtil.sign(Files.readAllBytes(Path.of(Path.of(containerRootFolder) + "_unenc.zip"))));

        Files.delete(Path.of(containerRootFolder + "_unenc.zip"));

        String containerMetaDataFileLocation = containerRootFolder + ".json";
        return fixContainerMetaData(containerMetaDataFileLocation, regId, type, encryptedHash, signature);
    }

    public boolean packContainer(String containerRootFolder) throws Exception{
        boolean result = zipAndEncrypt(Path.of(containerRootFolder));
        Files.delete(Path.of(containerRootFolder + "_unenc.zip"));
        return result;
    }

    private boolean zipAndEncrypt(Path zipSrcFolder) throws Exception{
        Path finalZipFile = Path.of(zipSrcFolder + "_unenc.zip");
        zipper.zipFolder(zipSrcFolder, finalZipFile);
        try(FileInputStream zipFile = new FileInputStream(finalZipFile.toFile().getAbsolutePath())){
            boolean result = cryptoUtil.encryptPacket(zipFile.readAllBytes(), centerId + UNDERSCORE + machineId, Path.of(zipSrcFolder+".zip").toString() );
            //Files.delete(finalZipFile);
            if (!result){
                logger.error("Encryption failed!!! ");
                return false;
            }
        }
        return true;
    }

    private boolean writeJSONFile(Map<?,?> jsonValue, String fileToWrite){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter jsonWriter = objectMapper.writer();
        try (FileOutputStream fos = new FileOutputStream(fileToWrite)) {
            OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
            jsonWriter.writeValue(writer,jsonValue); 
            return true;
        } catch(Exception ex){
            logger.error("",ex);
            return false;
        }
    }

    private String getIdJSONFileLocation(String packetRootFolder){
        return new File(packetRootFolder+File.separator+ "ID".toUpperCase()+".json").toString();
    }

    private String getProcessRoot(String containerRootFolder){
        return Path.of(containerRootFolder, src, process).toString();
    }

    private String getPacketRoot(String processRootFolder, String rid, String type){
        return Path.of(processRootFolder, rid+ UNDERSCORE + type.toLowerCase()).toString();
    }

    private String getContainerMetadataJSONFileLocation(String processRootFolder, String rid, String type){
        return Path.of(processRootFolder, rid+ UNDERSCORE + type.toLowerCase()+".json").toString();
    }

    private String createTempTemplate(String templatePacket, String rid) throws IOException, SecurityException{
        Path sourceDirectory = Paths.get(templatePacket);
        String tempDir = workDirectory + File.separator + rid;        
        Path targetDirectory = Paths.get(tempDir);
        FileSystemUtils.copyRecursively(sourceDirectory, targetDirectory);
        setupTemplateName(tempDir, rid);
        return targetDirectory.toString();    
    }
    
    private void setupTemplateName(String templateRootPath, String regId) throws SecurityException{
        String finalPath = templateRootPath + File.separator+ src + File.separator + process;
        File rootFolder = new File(finalPath);
        File[] listFiles= rootFolder.listFiles();
        for(File f: listFiles ){
            String name = f.getName();
            String finalName = name.replace("rid",regId);
            f.renameTo(new File(finalPath + File.separator + finalName));
        }
    }

    private boolean fixContainerMetaData(String fileToFix,String rid, String type, String encryptedHash, String signature ) throws IOException, Exception{
        JSONObject metadata = new JSONObject();
        Map<String, String> metaData = new HashMap();
        metaData.put("process", process);
        metaData.put("creationdate",apiUtil.getUTCDateTime(null));
        //TODO: Encrypted file SHA256 hash
        metaData.put("encryptedhash",encryptedHash);
        metaData.put("signature", signature);
        metaData.put("id",rid);
        metaData.put("source",src);
        //TODO: How to alter this? for now we leave it as is
        //metaData.put("providerversion",);
        //metaData.put("schemaversion",);
        metaData.put("packetname", rid+ UNDERSCORE +type);
        //metaData.put("providername", );
        
        File containerMetaDataTemp = File.createTempFile("pkm", ".cm");
        writeJSONFile(metaData, containerMetaDataTemp.getAbsolutePath());
        Map<?,?> mergedJsonMap = mergeJSON(fileToFix, containerMetaDataTemp.getAbsolutePath());
        if(!writeJSONFile(mergedJsonMap, fileToFix)) {
            logger.error("Error creating containerMetaData packet {} ", rid);
            return false;
        }
        return true;
        
    }

    private String generateRegId() {
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String currUTCTime = f.format(new Date());
		++counter;
		return centerId + machineId + counter + currUTCTime;
    }

    private void updateHashSequence(String packetRootFolder) throws Exception {
        File packetFolder = Path.of(packetRootFolder).toFile();
        String metaInfo_json = Files.readString(Path.of(packetRootFolder, PACKET_META_FILENAME));
        JSONObject jsonObject = new JSONObject(metaInfo_json);
        jsonObject.getJSONObject("identity").put("hashSequence1", new JSONArray());    
        File[] biometricFiles = packetFolder.listFiles((d, name) -> name.endsWith(".xml"));
        File[] demographicFiles = packetFolder.listFiles((d, name) -> name.endsWith(".pdf") ||
                name.endsWith(".jpg") || name.equals("ID.json"));    
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");    
        JSONObject bio_seq = new JSONObject();
        if(biometricFiles != null && biometricFiles.length > 0) {
            bio_seq.put("label", "biometricSequence");
            bio_seq.put("value", new JSONArray());
            for(File file : biometricFiles) {
                String fileName = file.getName();
                bio_seq.getJSONArray("value").put(fileName.substring(0, fileName.lastIndexOf(".")));
                messageDigest.update(Files.readAllBytes(file.toPath()));
            }
            jsonObject.getJSONObject("identity").getJSONArray("hashSequence1").put(bio_seq);
        }    
        JSONObject demo_seq = new JSONObject();
        if(demographicFiles != null && demographicFiles.length > 0) {
            demo_seq.put("label", "demographicSequence");
            demo_seq.put("value", new JSONArray());
            for(File file : demographicFiles) {
                String fileName = file.getName();
                demo_seq.getJSONArray("value").put(fileName.substring(0, fileName.lastIndexOf(".")));
                messageDigest.update(Files.readAllBytes(file.toPath()));
            }
            jsonObject.getJSONObject("identity").getJSONArray("hashSequence1").put(demo_seq);
        }    
        String packetDataHash = new String(Hex.encode(messageDigest.digest())).toUpperCase();
        Files.write(Path.of(packetRootFolder, PACKET_DATA_HASH_FILENAME), packetDataHash.getBytes("UTF-8"));    
        Files.write(Path.of(packetRootFolder, PACKET_META_FILENAME), jsonObject.toString().getBytes("UTF-8"));
    }
}
