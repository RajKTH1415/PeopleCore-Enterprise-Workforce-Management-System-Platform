package com.peoplecore.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;

@Component
public class ExportFileCleanupScheduler {

    @Value("${app.file.export-dir}")
    private String exportDir;

//    @Scheduled(fixedRate = 60000)
     @Scheduled(cron = "0 */1 * * * *")
    public void cleanupExpiredExports() {

        try {

            Path exportPath =
                    Paths.get(exportDir);

            if (!Files.exists(exportPath)) {

                return;
            }

            Files.list(exportPath)

                    .filter(Files::isRegularFile)

                    .forEach(file -> {

                        try {

                            FileTime lastModifiedTime =
                                    Files.getLastModifiedTime(file);

                            Instant expiryTime =
                                    Instant.now()
                                            .minus(Duration.ofMinutes(10));

                            if (lastModifiedTime.toInstant()
                                    .isBefore(expiryTime)) {

                                Files.delete(file);

                                System.out.println(
                                        "Deleted expired export: "
                                                + file.getFileName()
                                );
                            }

                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    });

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}