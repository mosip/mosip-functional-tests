package io.mosip.test.packetcreator.mosippacketcreator.service;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.storage.StorageProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;

@Service
public class PacketJobService {

    private Logger logger = LoggerFactory.getLogger(PacketJobService.class);

    @Autowired
    private StorageProvider storageProvider;

    @Autowired
    private PreregSyncService preregSyncService;

    @Autowired
    private PacketMakerService packetMakerService;

    @Autowired
    private PacketSyncService packetSyncService;

    @Autowired
    private ZipUtils zipUtils;

    @Job(name = "Create Packet with pre-reg sync")
    public void execute() {
        try {
            logger.info("started execute job");
            JSONObject jb = preregSyncService.syncPrereg();

            JSONArray keys = jb.names();
            for(int i=0; i< keys.length(); i++) {
                logger.info("Started for PRID", keys.get(i));
                String prid = keys.getString(i);
                try {
                    String location = preregSyncService.downloadPreregPacket(prid);
                    logger.info("Downloaded the prereg packet in {} ", location);

                    File targetDirectory = Path.of(preregSyncService.getWorkDirectory(), prid).toFile();
                    if(!targetDirectory.exists()  && !targetDirectory.mkdir())
                        throw new Exception("Failed to create target directory ! PRID : " + prid);

                    if(!zipUtils.unzip(location, targetDirectory.getAbsolutePath()))
                        throw new Exception("Failed to unzip pre-reg packet >> " + prid);

                    Path idJsonPath = Path.of(targetDirectory.getAbsolutePath(), "ID.json");

                    logger.info("Unzipped the prereg packet {}, ID.json exists : {}", prid, idJsonPath.toFile().exists());

                    String packetPath = packetMakerService.createContainer(idJsonPath.toString(),null);

                    logger.info("Packet created : {}", packetPath);

                    String response = packetSyncService.syncPacketRid(packetPath, "dummy", "APPROVED",
                            "dummy");

                    logger.info("RID Sync response : {}", response);

                    response =  packetSyncService.uploadPacket(packetPath);

                    logger.info("Packet Sync response : {}", response);

                } catch (Exception exception){
                    logger.error("Failed for PRID : {}", prid, exception);
                }
            }
        } catch (Throwable t) {
            logger.error("Job Failed", t);
        }
    }

}
