package io.mosip.test.packetcreator.mosippacketcreator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class MosipPacketCreatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MosipPacketCreatorApplication.class, args);
	}

}
