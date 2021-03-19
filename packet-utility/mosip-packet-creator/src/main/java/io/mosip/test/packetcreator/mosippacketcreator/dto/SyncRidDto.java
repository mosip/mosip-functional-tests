package io.mosip.test.packetcreator.mosippacketcreator.dto;

import lombok.Data;

@Data
public class SyncRidDto {

    private String name;
    private String phone;
    private String email;
    private String containerPath;
    private String supervisorStatus;
    private String supervisorComment;

}
