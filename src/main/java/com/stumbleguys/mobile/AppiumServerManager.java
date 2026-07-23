package com.stumbleguys.mobile;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import static com.stumbleguys.config.ConfigurationManager.configuration;

public final class AppiumServerManager {

    private static final Object LOCK = new Object();
    private static final int STARTUP_TIMEOUT_SEC = 30;

    private AppiumServerManager() {
    }

    public static void ensureServerRunning() {
        URI uri = URI.create(configuration().appiumServerUrl());
        String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        int port = uri.getPort() == -1 ? 4723 : uri.getPort();

        synchronized (LOCK) {
            if (isPortOpen(host, port)) {
                return;
            }
            startServerProcess(host, port);
            waitUntilOpen(host, port, STARTUP_TIMEOUT_SEC);
        }
    }

    private static void startServerProcess(String host, int port) {
        Path logPath = Paths.get("target", "logs", "appium-server.log").toAbsolutePath().normalize();
        createLogParent(logPath);

        boolean windows = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
        ProcessBuilder pb;
        if (windows) {
            pb = new ProcessBuilder("cmd", "/c", "appium",
                    "--address", host, "--port", Integer.toString(port));
        } else {
            pb = new ProcessBuilder("sh", "-lc",
                    "appium --address " + host + " --port " + port);
        }
        pb.redirectErrorStream(true)
          .redirectOutput(ProcessBuilder.Redirect.appendTo(logPath.toFile()));

        try {
            pb.start();
            System.out.println("[Appium] Server starting. Logs: " + logPath);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to start Appium server.", e);
        }
    }

    private static void createLogParent(Path logPath) {
        try {
            Files.createDirectories(logPath.getParent());
            File f = logPath.toFile();
            if (!f.exists()) f.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot prepare Appium log file: " + logPath, e);
        }
    }

    private static void waitUntilOpen(String host, int port, int timeoutSec) {
        Instant deadline = Instant.now().plus(Duration.ofSeconds(timeoutSec));
        while (Instant.now().isBefore(deadline)) {
            if (isPortOpen(host, port)) return;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted waiting for Appium.", e);
            }
        }
        throw new IllegalStateException(
                "Appium did not start on " + host + ":" + port + " within " + timeoutSec + "s");
    }

    private static boolean isPortOpen(String host, int port) {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(host, port), 800);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }
}
