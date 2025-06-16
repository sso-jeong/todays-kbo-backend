package com.kbo.todayskbo.scheduler;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;

@Component
public class PythonScheduler {

    private static final String SCRIPT_DIR = "/app/build";

    private final List<String> scripts = List.of(
            "python/resultGames.py",
            "python/relatedGames.py"
    );

    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Seoul")  // 매일 23:00에 실행
    public void runPythonScriptsDaily() {
        for (String script : scripts) {
            try {
                ProcessBuilder pb = new ProcessBuilder("python3", script);
                pb.directory(new File(SCRIPT_DIR));
                pb.redirectErrorStream(true);
                Process process = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[Python][" + script + "] " + line);
                    }
                }

                int exitCode = process.waitFor();
                System.out.println("✅ " + script + " 종료 코드: " + exitCode);

            } catch (Exception e) {
                System.out.println("❌ 오류 발생 in " + script);
                e.printStackTrace();
            }
        }
    }
}