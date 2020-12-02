package io.mosip.test.packetcreator.mosippacketcreator.prereg;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.slf4j.Logger;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.test.packetcreator.mosippacketcreator.service.PacketMakerService;


public class PreregPacketCreatorTest {

    Logger logger = LoggerFactory.getLogger(PreregPacketCreatorTest.class);
    
    @Autowired
    private PacketMakerService packetMaker;

    @Test
    void when_prereg_data_is_given() {
        // 9513700216
        try {
            if(packetMaker == null)
                packetMaker = new PacketMakerService();
            assertNotNull(packetMaker.createContainer("/home/sasikumar/Documents/MOSIP/packetcreator/prereg/60736047859260/ID.json", null));
        } catch (Exception ex) {
            // do nothing
            logger.error("", ex);
        }
        
    }
}
