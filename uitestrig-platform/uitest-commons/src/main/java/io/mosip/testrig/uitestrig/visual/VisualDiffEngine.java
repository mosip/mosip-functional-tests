package io.mosip.testrig.uitestrig.visual;

import com.microsoft.playwright.Page;
import io.mosip.testrig.uitestrig.core.UiConfigManager;
import io.mosip.testrig.uitestrig.core.UiGlobalConstants;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Enhanced visual regression engine with region masking and diff image output.
 *
 * Builds on {@link SnapshotManager}'s pixel-diff algorithm and adds:
 * <ul>
 *   <li><b>Region masking</b> — paint dynamic areas (timestamps, counters, avatars)
 *       with a solid grey block before comparison, eliminating false positives.</li>
 *   <li><b>Element-level snapshot</b> — compare just a single component rather than
 *       the full page, making tests faster and more focused.</li>
 *   <li><b>Diff image generation</b> — on failure, saves a PNG that highlights differing
 *       pixels in red against a greyed-out background, for easy CI review.</li>
 * </ul>
 *
 * <pre>
 *   // Full-page snapshot, masking timestamps and user-specific counters
 *   VisualDiffEngine vd = new VisualDiffEngine(page)
 *       .maskRegion("#requestTimestamp")
 *       .maskRegion(".user-event-count");
 *   vd.assertPageSnapshot("view-history.png");
 *
 *   // Element-level snapshot
 *   vd.assertElementSnapshot("#vidCard", "vid-card.png");
 *
 *   // Custom tolerance for a noisy page
 *   vd.withTolerance(0.05).assertPageSnapshot("dashboard.png");
 * </pre>
 *
 * Baselines are stored in the directory configured by {@code snapshotDir} (default:
 * {@code src/test/resources/snapshots/}).  Diff images are saved to
 * {@code target/visual-diffs/}.  Run with {@code -Dplaywright.update.snapshots=true}
 * to regenerate baselines.
 */
public class VisualDiffEngine {

    private static final Logger logger = LoggerFactory.getLogger(VisualDiffEngine.class);
    private static final int    CHANNEL_TOLERANCE = 10;
    private static final Color  MASK_COLOR        = new Color(128, 128, 128);
    private static final String DIFF_DIR          = "target/visual-diffs/";

    private final Page         page;
    private final List<String> maskedSelectors = new ArrayList<>();
    private double             maxDiffRatio;

    public VisualDiffEngine(Page page) {
        this.page = page;
        this.maxDiffRatio = UiGlobalConstants.SNAPSHOT_MAX_DIFF_RATIO;
    }

    // ── Fluent configuration ──────────────────────────────────────────────────

    /**
     * Adds a CSS selector whose screen region will be painted grey before comparison.
     * Call multiple times to mask several areas.  Masks are cumulative.
     */
    public VisualDiffEngine maskRegion(String selector) {
        maskedSelectors.add(selector);
        return this;
    }

    /** Removes all previously added mask selectors. */
    public VisualDiffEngine clearMasks() {
        maskedSelectors.clear();
        return this;
    }

    /**
     * Overrides the maximum allowed diff ratio for this engine instance.
     *
     * @param ratio fraction of pixels allowed to differ, e.g. {@code 0.05} for 5%
     */
    public VisualDiffEngine withTolerance(double ratio) {
        if (!Double.isFinite(ratio) || ratio < 0 || ratio > 1) {
            throw new IllegalArgumentException("Tolerance must be between 0 and 1, got: " + ratio);
        }
        this.maxDiffRatio = ratio;
        return this;
    }

    // ── Snapshot assertions ───────────────────────────────────────────────────

    /**
     * Asserts the full page matches the stored baseline, after applying any masks.
     * Saves a new baseline if none exists.  On failure saves a diff image to
     * {@code target/visual-diffs/}.
     *
     * @param snapshotName filename, e.g. {@code "manage-vid.png"}
     */
    public void assertPageSnapshot(String snapshotName) {
        byte[] currentBytes = page.screenshot(new Page.ScreenshotOptions()
                .setFullPage(true)
                .setAnimations(com.microsoft.playwright.options.ScreenshotAnimations.DISABLED));
        Path   baselinePath = resolveBaselinePath(snapshotName);

        if (UiConfigManager.isUpdateSnapshots()) {
            writeImage(baselinePath, currentBytes);
            logger.info("Snapshot baseline updated: {}", snapshotName);
            return;
        }

        if (!baselinePath.toFile().exists()) {
            throw new AssertionFailedError("No baseline for '" + snapshotName
                    + "'. Run with -Dplaywright.update.snapshots=true to create it.");
        }

        compareAndAssert(baselinePath, currentBytes, snapshotName, 0, 0);
    }

    /**
     * Asserts a single element matches its stored baseline.
     * Uses Playwright's element-level screenshot (no full-page capture).
     *
     * @param selector     CSS selector for the element to snapshot
     * @param snapshotName filename, e.g. {@code "vid-card.png"}
     */
    public void assertElementSnapshot(String selector, String snapshotName) {
        byte[] currentBytes = page.locator(selector)
                .screenshot(new com.microsoft.playwright.Locator.ScreenshotOptions()
                        .setAnimations(com.microsoft.playwright.options.ScreenshotAnimations.DISABLED));
        // Read the origin AFTER the screenshot, using the same locator — Locator.screenshot()
        // scrolls the element into view first, which can change its document position relative
        // to when it was queried beforehand.
        BoundingBox elementOrigin = getDocumentBoundingBox(selector);
        Path   baselinePath = resolveBaselinePath(snapshotName);

        if (UiConfigManager.isUpdateSnapshots()) {
            writeImage(baselinePath, currentBytes);
            logger.info("Element snapshot baseline updated: {} → {}", selector, snapshotName);
            return;
        }

        if (!baselinePath.toFile().exists()) {
            throw new AssertionFailedError("No baseline for '" + snapshotName
                    + "'. Run with -Dplaywright.update.snapshots=true to create it.");
        }

        double originX = elementOrigin != null ? elementOrigin.x() : 0;
        double originY = elementOrigin != null ? elementOrigin.y() : 0;
        compareAndAssert(baselinePath, currentBytes, snapshotName, originX, originY);
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private void compareAndAssert(Path baselinePath, byte[] currentBytes, String snapshotName,
                                   double maskOriginX, double maskOriginY) {
        try {
            BufferedImage baseline = ImageIO.read(baselinePath.toFile());
            BufferedImage current  = ImageIO.read(new ByteArrayInputStream(currentBytes));
            if (baseline == null || current == null) {
                throw new AssertionFailedError("Cannot decode snapshot image for '" + snapshotName + "'");
            }

            if (baseline.getWidth() != current.getWidth() || baseline.getHeight() != current.getHeight()) {
                logger.warn("Snapshot size changed: baseline {}x{}, current {}x{}",
                        baseline.getWidth(), baseline.getHeight(),
                        current.getWidth(), current.getHeight());
                saveDiffImages(baseline, current, snapshotName);
                throw new AssertionFailedError(String.format(
                        "Visual snapshot size mismatch for '%s': " +
                        "baseline %dx%d, current %dx%d. Diff saved to %s",
                        snapshotName,
                        baseline.getWidth(), baseline.getHeight(),
                        current.getWidth(), current.getHeight(), DIFF_DIR));
            }

            applyMasks(baseline, maskOriginX, maskOriginY);
            applyMasks(current, maskOriginX, maskOriginY);

            long diffPixels  = 0;
            long totalPixels = (long) baseline.getWidth() * baseline.getHeight();
            for (int y = 0; y < baseline.getHeight(); y++) {
                for (int x = 0; x < baseline.getWidth(); x++) {
                    if (pixelsDiffer(baseline.getRGB(x, y), current.getRGB(x, y))) diffPixels++;
                }
            }
            double diffRatio = (double) diffPixels / totalPixels;

            if (diffRatio > maxDiffRatio) {
                saveDiffImages(baseline, current, snapshotName);
                throw new AssertionFailedError(String.format(
                        "Visual snapshot mismatch for '%s': diff %.2f%% exceeds max %.2f%%. " +
                        "Diff image saved to %s",
                        snapshotName, diffRatio * 100, maxDiffRatio * 100, DIFF_DIR));
            }
            logger.debug("Snapshot '{}' matched — diff: {}%",
                    snapshotName, String.format("%.4f", diffRatio * 100));

        } catch (IOException e) {
            throw new AssertionFailedError("Snapshot comparison failed for '" + snapshotName + "': " + e.getMessage(), e);
        }
    }

    /**
     * Paints masked regions grey on the image in-place.
     * Fetches each selector's document-relative bounding box via JavaScript, then shifts it
     * by the captured image's own document origin — zero for a full-page screenshot, or the
     * target element's own document position for an element screenshot — before scaling to
     * device pixels, since the element screenshot's pixel data starts at the element's corner,
     * not the document origin.
     */
    private void applyMasks(BufferedImage image, double originX, double originY) {
        if (maskedSelectors.isEmpty()) return;
        double scale = getDevicePixelRatio();
        Graphics2D g = image.createGraphics();
        g.setColor(MASK_COLOR);
        for (String selector : maskedSelectors) {
            BoundingBox box = getDocumentBoundingBox(selector);
            if (box != null) {
                int x = (int) Math.round((box.x() - originX) * scale);
                int y = (int) Math.round((box.y() - originY) * scale);
                int w = (int) Math.round(box.width() * scale);
                int h = (int) Math.round(box.height() * scale);
                g.fillRect(x, y, w, h);
                logger.debug("Mask applied: {} → {}x{} at ({},{})", selector, w, h, x, y);
            }
        }
        g.dispose();
    }

    /** CSS pixels (from getBoundingClientRect) must be scaled to device/screenshot pixels. */
    private double getDevicePixelRatio() {
        try {
            Object result = page.evaluate("() => window.devicePixelRatio");
            return result instanceof Number n ? n.doubleValue() : 1.0;
        } catch (Exception e) {
            logger.warn("Could not read devicePixelRatio, defaulting to 1.0: {}", e.getMessage());
            return 1.0;
        }
    }

    /**
     * Saves two diff images on visual failure:
     * <ul>
     *   <li>{@code <name>-diff.png} — matching pixels greyed, differing pixels highlighted red</li>
     *   <li>{@code <name>-actual.png} — the actual (current) screenshot for direct comparison</li>
     * </ul>
     */
    private void saveDiffImages(BufferedImage baseline, BufferedImage current, String snapshotName) {
        try {
            String base = snapshotName.replaceFirst("\\.png$", "");
            Path diffPath   = Paths.get(DIFF_DIR, base + "-diff.png");
            Path actualPath = Paths.get(DIFF_DIR, base + "-actual.png");
            Files.createDirectories(diffPath.getParent());

            // Diff image — red on grey
            int w = Math.min(baseline.getWidth(), current.getWidth());
            int h = Math.min(baseline.getHeight(), current.getHeight());
            BufferedImage diff = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (pixelsDiffer(baseline.getRGB(x, y), current.getRGB(x, y))) {
                        diff.setRGB(x, y, Color.RED.getRGB());
                    } else {
                        int rgb = baseline.getRGB(x, y);
                        int r = ((rgb >> 16) & 0xFF) / 3;
                        int g = ((rgb >>  8) & 0xFF) / 3;
                        int b = ( rgb        & 0xFF) / 3;
                        diff.setRGB(x, y, (r << 16) | (g << 8) | b);
                    }
                }
            }
            ImageIO.write(diff,    "PNG", diffPath.toFile());
            ImageIO.write(current, "PNG", actualPath.toFile());
            logger.info("Diff images saved to {}", DIFF_DIR);
        } catch (IOException e) {
            throw new java.io.UncheckedIOException(
                    "Failed to save diff images for " + snapshotName, e);
        }
    }

    @SuppressWarnings("unchecked")
    private BoundingBox getDocumentBoundingBox(String selector) {
        try {
            Object result = page.evaluate(
                "(selector) => { " +
                "  const el = document.querySelector(selector); " +
                "  if (!el) return null; " +
                "  const r = el.getBoundingClientRect(); " +
                "  return { x: Math.round(r.x + window.scrollX), " +
                "           y: Math.round(r.y + window.scrollY), " +
                "           w: Math.round(r.width), " +
                "           h: Math.round(r.height) }; " +
                "}",
                selector
            );
            if (result == null) return null;
            Map<String, Object> map = (Map<String, Object>) result;
            return new BoundingBox(
                    toInt(map.get("x")), toInt(map.get("y")),
                    toInt(map.get("w")), toInt(map.get("h")));
        } catch (Exception e) {
            logger.warn("Could not get bounding box for '{}': {}", selector, e.getMessage());
            return null;
        }
    }

    private static int toInt(Object v) {
        return v instanceof Number n ? n.intValue() : 0;
    }

    private static Path resolveBaselinePath(String snapshotName) {
        String dir = UiConfigManager.getproperty("snapshotDir");
        if (dir == null || dir.isBlank()) dir = "src/test/resources/snapshots";
        return Paths.get(dir, snapshotName);
    }

    private static void writeImage(Path path, byte[] bytes) {
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new java.io.UncheckedIOException("Failed to write snapshot: " + path, e);
        }
    }

    private static boolean pixelsDiffer(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xFF, g1 = (rgb1 >> 8) & 0xFF, b1 = rgb1 & 0xFF;
        int r2 = (rgb2 >> 16) & 0xFF, g2 = (rgb2 >> 8) & 0xFF, b2 = rgb2 & 0xFF;
        return Math.abs(r1 - r2) > CHANNEL_TOLERANCE
            || Math.abs(g1 - g2) > CHANNEL_TOLERANCE
            || Math.abs(b1 - b2) > CHANNEL_TOLERANCE;
    }

    private record BoundingBox(int x, int y, int width, int height) {}
}
