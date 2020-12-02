package io.mosip.test.packetcreator.mosippacketcreator.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class PacketCreateDto {

    @NonNull
    private String idJsonPath;

    private String templatePath;
}
