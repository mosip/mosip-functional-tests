package io.mosip.test.packetcreator.mosippacketcreator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class SimpleService {
    Logger logger = LoggerFactory.getLogger(SimpleService.class);

    @Value("${mosip.test.regclient.store:/home/sasikumar/Documents/MOSIP/packetcreator}")
    private String finalDestination;

    //@Value("${mosip.test.packet.template.location:/home/sasikumar/Documents/MOSIP/template}")
    @Value("${mosip.test.welcome}")
    private String templateFolder;

    @Value("${mosip.test.packet.template.source:REGISTRATION_CLIENT}")
    private String src;

    @Value("${mosip.test.packet.template.process:NEW}")
    private String process;

    public String getWelcomeMessage(){

        return templateFolder;
    }
}
