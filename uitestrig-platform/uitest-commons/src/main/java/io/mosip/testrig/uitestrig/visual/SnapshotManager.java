package io.mosip.testrig.uitestrig.visual;

import com.microsoft.playwright.Page;
import io.mosip.testrig.uitestrig.core.UiConfigManager;
import io.mosip.testrig.uitestrig.core.UiGlobalConstants;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Screenshot-based visual regression.
 *
 * Baseline PNGs live in the directory configured by {@code snapshotDir} property
 * (default: src/test/resources/snapshots/).
 *
 * Run with -Dplaywright.update.snapshots=true to regenerate baselines.
 * Pixel diff tolerance is {@link UiGlobalConstants#SNAPSHOT_MAX_DIFF_RATIO} (default 2%).
 * Per-channel tolerance of 10/255 reduces false positives from anti-aliasing.
 */
public class SnapshotManager {

    private static final Logger logger = LoggerFactory.getLogger(SnapshotManager.class);
    private static final int CHANNEL_TOLERANCE = 10;

    private final Page page;

    public SnapshotManager(Page page) {
        this.page = page;
    }

    /**
     * Asserts that the current page matches the stored baseline screenshot.
     * If no baseline exists, the current screenshot is saved as the new baseline.
     *
     * @param snapshotName file name, e.g. "manage-vid.png"
     */
    public void assertPageSnapshot(String snapshotName) {
        byte[] currentBytes = page.screenshot(
                new Page.ScreenshotOptions().setFullPage(true));

        String snapshotDir = UiConfigManager.getproperty("snapshotDir");
        if (snapshotDir == null || snapshotDir.isBlank()) {
            snapshotDir = "src/test/resources/snapshots";
        }
        Path baselinePath = Paths.get(snapshotDir, snapshotName);

        if (UiConfigManager.isUpdateSnapshots()) {
            saveBaseline(baselinePath, currentBytes);
            logger.info("Snapshot baseline updated: {}", snapshotName);
            return;
        }

        if (!baselinePath.toFile().exists()) {
            throw new AssertionFailedError("No baseline for '" + snapshotName
                    + "'. Run with -Dplaywright.update.snapshots=true to create it.");
        }

        double diffRatio = computeDiffRatio(baselinePath, currentBytes);
        double maxAllowed = UiGlobalConstants.SNAPSHOT_MAX_DIFF_RATIO;

        if (diffRatio > maxAllowed) {
            throw new AssertionFailedError(String.format(
                    "Visual snapshot mismatch for '%s': diff %.2f%% exceeds max %.2f%%",
                    snapshotName, diffRatio * 100, maxAllowed * 100));
        }
        logger.debug("Snapshot '{}' matched — diff: {}%", snapshotName, String.format("%.4f", diffRatio * 100));
    }

    /** Captures a raw screenshot for failure evidence. */
    public byte[] captureFailureScreenshot() {
        return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
    }

    private static void saveBaseline(Path path, byte[] imageBytes) {
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, imageBytes);
        } catch (IOException e) {
            throw new java.io.UncheckedIOException("Failed to write baseline screenshot: " + path, e);
        }
    }

    private static double computeDiffRatio(Path baselinePath, byte[] currentBytes) {
        try {
            BufferedImage baseline = ImageIO.read(baselinePath.toFile());
            BufferedImage current  = ImageIO.read(new ByteArrayInputStream(currentBytes));
            if (baseline == null || current == null) {
                throw new AssertionFailedError("Cannot decode snapshot image: " + baselinePath);
            }

            if (baseline.getWidth() != current.getWidth() || baseline.getHeight() != current.getHeight()) {
                logger.warn("Snapshot size changed: baseline {}x{}, current {}x{}",
                        baseline.getWidth(), baseline.getHeight(),
                        current.getWidth(), current.getHeight());
                return 1.0;
            }

            long diffPixels = 0;
            long totalPixels = (long) baseline.getWidth() * baseline.getHeight();

            for (int y = 0; y < baseline.getHeight(); y++) {
                for (int x = 0; x < baseline.getWidth(); x++) {
                    int bRgb = baseline.getRGB(x, y);
                    int cRgb = current.getRGB(x, y);
                    if (pixelsDiffer(bRgb, cRgb)) diffPixels++;
                }
            }
            return (double) diffPixels / totalPixels;
        } catch (IOException e) {
            throw new AssertionFailedError("Failed to compare snapshot images: " + baselinePath, e);
        }
    }

    private static boolean pixelsDiffer(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xFF, g1 = (rgb1 >> 8) & 0xFF, b1 = rgb1 & 0xFF;
        int r2 = (rgb2 >> 16) & 0xFF, g2 = (rgb2 >> 8) & 0xFF, b2 = rgb2 & 0xFF;
        return Math.abs(r1 - r2) > CHANNEL_TOLERANCE
            || Math.abs(g1 - g2) > CHANNEL_TOLERANCE
            || Math.abs(b1 - b2) > CHANNEL_TOLERANCE;
    }
}
