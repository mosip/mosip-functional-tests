package io.mosip.test.packetcreator.mosippacketcreator.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ZipUtils {
    Logger logger = LoggerFactory.getLogger(ZipUtils.class);
    // Uses java.util.zip to create zip file
    public void zipFolder(Path sourceFolderPath, Path zipPath) throws IOException {
        try(ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))){
            Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch(IOException ex){
            logger.error("Error during zip", ex);
        }
        
    }

    public boolean unzip(String sourceFile, String targetDirectory) {
        try(InputStream in = Files.newInputStream(Path.of(sourceFile));
            ZipInputStream zipInputStream = new ZipInputStream(in)){
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {

                String fileName = zipEntry.getName();
                try (OutputStream os = Files.newOutputStream(Path.of(targetDirectory,fileName))) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        os.write(buffer, 0, len);
                        os.flush();
                    }
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            return true;
        } catch (IOException e) {
            logger.error("Error while unzip", e);
        }
        return false;
    }
}
