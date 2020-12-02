package io.mosip.test.packetcreator.mosippacketcreator.controller;

import java.util.Base64;

import io.mosip.test.packetcreator.mosippacketcreator.dto.PacketCreateDto;
import io.mosip.test.packetcreator.mosippacketcreator.dto.SyncRidDto;
import io.mosip.test.packetcreator.mosippacketcreator.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.*;

@RestController
public class TestDataController {

    private static final Logger logger = LoggerFactory.getLogger(TestDataController.class);
    
    @Value("${mosip.test.welcome}")
    private String welcomeMessage;

    @Autowired
    PacketMakerService pkm;

    @Autowired
    PreregSyncService pss;

    @Autowired
    APIRequestUtil apiUtil;

    @Autowired
    CryptoUtil cryptoUtil;

    @Autowired
    PacketSyncService packetSyncService;

    @PostMapping(value = "/packetcreator")
    public @ResponseBody String getTestData(@RequestBody PacketCreateDto packetCreateDto) {
        try{
            return pkm.createContainer(packetCreateDto.getIdJsonPath(), packetCreateDto.getTemplatePath());
        } catch (Exception ex){
             logger.error("", ex);
        }
        return "Failed!";
    }

    @GetMapping(value = "/auth")
    public @ResponseBody String getAPITestData() {
        return String.valueOf(apiUtil.initToken());
    }

    @GetMapping(value = "/sync")
    public @ResponseBody String syncPreregData() {
        try {
            pss.syncAndDownload();
            return "All Done!";
        } catch (Exception exception) {
            logger.error("", exception);
            return exception.getMessage();
        }
    }
        
    @GetMapping(value = "/sync/{preregId}")
    public @ResponseBody String getPreregData(@PathVariable("preregId") String preregId){
        try{
            return pss.downloadPreregPacket(preregId);
        } catch(Exception exception){
            logger.error("", exception);
            return "Failed";
        }
    }

    @GetMapping(value = "/encrypt")
    public @ResponseBody String encryptData() throws Exception {
        return Base64.getUrlEncoder().encodeToString(cryptoUtil.encrypt("test".getBytes(), "referenceId"));
    }

    @PostMapping(value = "/ridsync")
    public @ResponseBody String syncRid(@RequestBody SyncRidDto syncRidDto) throws Exception {
        return packetSyncService.syncPacketRid(syncRidDto.getContainerPath(), syncRidDto.getName(),
                syncRidDto.getSupervisorStatus(), syncRidDto.getSupervisorComment());
    }
        
}
