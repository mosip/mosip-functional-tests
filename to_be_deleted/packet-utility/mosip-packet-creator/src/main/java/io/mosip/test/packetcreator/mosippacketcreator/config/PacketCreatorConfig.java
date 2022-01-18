package io.mosip.test.packetcreator.mosippacketcreator.config;


import org.jobrunr.dashboard.JobRunrDashboardWebServer;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.utils.mapper.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PacketCreatorConfig {

    // The`spring-boot-starter-web` provides Jackson as JobMapper
    @Bean
    public StorageProvider storageProvider(JobMapper jobMapper) {
        InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();
        storageProvider.setJobMapper(jobMapper);
        return storageProvider;
    }

    /*@Bean
    public JobRunrDashboardWebServer dashboardWebServer(StorageProvider storageProvider, JsonMapper jsonMapper) {
        final JobRunrDashboardWebServer jobRunrDashboardWebServer = new JobRunrDashboardWebServer(storageProvider,
                jsonMapper, 8081);
        jobRunrDashboardWebServer.start();
        return jobRunrDashboardWebServer;
    }*/

}