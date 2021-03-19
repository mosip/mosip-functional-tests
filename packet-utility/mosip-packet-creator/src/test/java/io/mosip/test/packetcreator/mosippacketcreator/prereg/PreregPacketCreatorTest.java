package io.mosip.test.packetcreator.mosippacketcreator.prereg;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import io.mosip.test.packetcreator.mosippacketcreator.MosipPacketCreatorApplicationTest;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.test.packetcreator.mosippacketcreator.service.PacketMakerService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = MosipPacketCreatorApplicationTest.class)
public class PreregPacketCreatorTest {

    Logger logger = LoggerFactory.getLogger(PreregPacketCreatorTest.class);
    
    @Autowired
    private PacketMakerService packetMaker;

    @Test
    void when_prereg_data_is_given() {
        // 9513700216
        try {
            Path path = Files.createTempFile("ID", "json");
            Files.write(path, getClass().getResourceAsStream("/ID.json").readAllBytes());
            assertNotNull(packetMaker.createContainer(path.toString(), null));
        } catch (Exception ex) {
            // do nothing
            logger.error("", ex);
        }
        
    }
}
