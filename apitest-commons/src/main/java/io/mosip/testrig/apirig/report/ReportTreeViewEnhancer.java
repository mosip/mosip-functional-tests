package io.mosip.testrig.apirig.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;


public final class ReportTreeViewEnhancer {

    private static final String REPORT_SHELL_CLOSE = "</div></div>";

    private static final String REPORT_THEME_SCRIPT =
            "<script>(function(){function s(){var t=document.documentElement.getAttribute('data-theme')||'light',"
                    + "b=document.getElementById('report-theme-toggle');if(!b)return;"
                    + "b.textContent=t==='light'?'Dark mode':'Light mode';"
                    + "b.setAttribute('aria-pressed',t==='light'?'true':'false')}"
                    + "s();document.getElementById('report-theme-toggle')?.addEventListener('click',function(){"
                    + "var t=document.documentElement.getAttribute('data-theme')||'light',n=t==='light'?'dark':'light';"
                    + "document.documentElement.setAttribute('data-theme',n);try{localStorage.setItem('report-theme',n)}catch(e){}"
                    + "s()})})();</script>";

    private static final String REPORT_TREE_ACCORDION_SCRIPT =
            "<script>(function(){var nav=document.getElementById('report-scenario-tree');"
                    + "if(!nav)return;"
                    + "nav.querySelectorAll('details.tree-steps-details').forEach(function(d){"
                    + "d.addEventListener('toggle',function(){"
                    + "if(!d.open)return;"
                    + "nav.querySelectorAll('details.tree-steps-details').forEach(function(o){"
                    + "if(o!==d)o.open=false;});});});})();</script>";

    private static final String REPORT_TREE_FILTER_SCRIPT =
            "<script>(function(){var nav=document.getElementById('report-scenario-tree');"
                    + "if(!nav)return;var bar=nav.querySelector('.tree-filter-toolbar');"
                    + "if(!bar)return;var q=bar.querySelector('.tree-search');"
                    + "var empty=nav.querySelector('.tree-search-empty');"
                    + "function active(){var b=bar.querySelector('.tree-filter-chip.is-active');"
                    + "return b?(b.getAttribute('data-tree-filter')||'all'):'all';}"
                    + "function apply(){var f=active();var needle=(q&&q.value||'').trim().toLowerCase();var shown=0;"
                    + "nav.querySelectorAll('li.tree-scenario-node').forEach(function(li){"
                    + "var s=li.getAttribute('data-status')||'';"
                    + "var name=(li.getAttribute('data-name')||'').toLowerCase();"
                    + "var show=(f==='all'||s===f)&&(!needle||name.indexOf(needle)>=0);"
                    + "if(!show){var det=li.querySelector('details.tree-steps-details');if(det)det.open=false;}"
                    + "li.style.display=show?'':'none';if(show)shown++;});"
                    + "if(empty)empty.hidden=shown!==0;}"
                    + "bar.addEventListener('click',function(e){"
                    + "var t=e.target;if(!t||!t.closest)return;var btn=t.closest('.tree-filter-chip');"
                    + "if(!btn)return;"
                    + "bar.querySelectorAll('.tree-filter-chip').forEach(function(b){"
                    + "var on=b===btn;b.classList.toggle('is-active',on);b.setAttribute('aria-pressed',on?'true':'false');});"
                    + "apply();});"
                    + "if(q)q.addEventListener('input',apply);"
                    + "var next=bar.querySelector('.tree-next-fail');var failIdx=-1;"
                    + "if(next)next.addEventListener('click',function(){"
                    + "var nodes=[];nav.querySelectorAll('li.tree-scenario-node').forEach(function(li){"
                    + "if(li.getAttribute('data-status')==='fail'&&li.style.display!=='none')nodes.push(li);});"
                    + "if(!nodes.length)return;failIdx=(failIdx+1)%nodes.length;"
                    + "var a=nodes[failIdx].querySelector('a.tree-link--scenario');if(a)a.click();});"
                    + "apply();})();</script>";

    private static final String REPORT_UX_MARKER = "report-ux-v3";

    private static final String[] REPORT_UX_LEGACY_MARKERS = {"/*report-ux-v1*/", "/*report-ux-v2*/"};

    // Scenario bodies are stored gzip+base64 (or JSON text in older reports) in
    // <script class="scenario-fold-src"> (see deferScenarioBodies) and only turned into DOM
    // when the card is opened. Cards from older reports without that script are folded in
    // place as before.
    private static final String REPORT_UX_SCRIPT =
            "<script>(function(){/*" + REPORT_UX_MARKER + "*/"
                    + "function addCopy(root){root.querySelectorAll('textarea').forEach(function(ta){"
                    + "if(ta.getAttribute('data-copy-ready'))return;ta.setAttribute('data-copy-ready','1');"
                    + "var b=document.createElement('button');b.type='button';b.className='report-copy-btn';b.textContent='Copy';"
                    + "b.addEventListener('click',function(){var v=ta.value||'';"
                    + "function ok(){b.textContent='Copied';setTimeout(function(){b.textContent='Copy';},1200);}"
                    + "if(navigator.clipboard&&navigator.clipboard.writeText){navigator.clipboard.writeText(v).then(ok,function(){ta.select();try{document.execCommand('copy');ok();}catch(e){}});} "
                    + "else {ta.select();try{document.execCommand('copy');ok();}catch(e){}}});"
                    + "if(ta.parentNode)ta.parentNode.insertBefore(b,ta);});}"
                    + "function decode(src){if(src.type==='application/json')return Promise.resolve(JSON.parse(src.textContent));"
                    + "var bin=atob(src.textContent.trim()),u=new Uint8Array(bin.length);"
                    + "for(var i=0;i<bin.length;i++)u[i]=bin.charCodeAt(i);"
                    + "return new Response(new Blob([u]).stream().pipeThrough(new DecompressionStream('gzip'))).text();}"
                    + "function fill(card){if(card._fill)return card._fill;"
                    + "var body=card.querySelector('.scenario-fold-body'),src=card.querySelector('script.scenario-fold-src');"
                    + "if(!body||!src){card._fill=Promise.resolve(body);return card._fill;}"
                    + "body.textContent='Loading\u2026';"
                    + "card._fill=Promise.resolve().then(function(){return decode(src);})"
                    + ".then(function(h){body.innerHTML=h;},function(e){body.textContent='Could not load scenario details: '+e.message;})"
                    + ".then(function(){src.parentNode.removeChild(src);addCopy(body);return body;});"
                    + "return card._fill;}"
                    + "window.reportOpenScenario=function(card,open){var body=card.querySelector('.scenario-fold-body');"
                    + "if(body){body.hidden=!open;card.classList.toggle('is-open',open);}"
                    + "return open?fill(card):Promise.resolve(body);};"
                    + "document.querySelectorAll('.report-main .scenario-detail-card').forEach(function(card){"
                    + "var inner=card.querySelector('.scenario-detail-card__inner');if(!inner)return;"
                    + "var h3=inner.querySelector('h3');if(!h3)return;"
                    + "if(!inner.querySelector('.scenario-fold-body')){"
                    + "var body=document.createElement('div');body.className='scenario-fold-body';body.hidden=true;"
                    + "var n=h3.nextSibling;while(n){var nx=n.nextSibling;body.appendChild(n);n=nx;}"
                    + "inner.appendChild(body);addCopy(body);}"
                    + "h3.classList.add('scenario-fold-head');"
                    + "h3.addEventListener('click',function(e){"
                    + "if(e.target.closest&&e.target.closest('a,button'))return;"
                    + "var b=card.querySelector('.scenario-fold-body');"
                    + "window.reportOpenScenario(card,!!(b&&b.hidden));});});})();</script>";


    // content-visibility:auto skips layout for off-screen scenario cards and substitutes
    // contain-intrinsic-size (480px). scrollIntoView then uses those placeholders, so a
    // click on a far scenario stops around the middle of a long report. Before scrolling,
    // force real layout only for cards up to the target (later cards stay skipped so the
    // first paint stays cheap). Instant scroll; smooth animation starts against the
    // placeholder height and stops short.
    // Step anchors (mN-sK) live inside deferred scenario bodies, so a step link opens its
    // scenario card (by the mN prefix) before looking the anchor up.
    private static final String REPORT_MAIN_SCROLL_MARKER = "cv-scroll-v5";

    private static final String REPORT_MAIN_SCROLL_SCRIPT =
            "<script>(function(){/*" + REPORT_MAIN_SCROLL_MARKER + "*/"
                    + "function reveal(el){var cards=document.querySelectorAll('.report-main .scenario-detail-card');"
                    + "for(var i=0;i<cards.length;i++){var c=cards[i];c.style.contentVisibility='visible';"
                    + "if(c===el||c.contains(el))break;}}"
                    + "function cardFor(id){var m=/^(m\\d+)(-|$)/.exec(id);if(!m)return null;"
                    + "return document.querySelector('.report-main .scenario-detail-card[data-scenario-anchor=\"'+m[1]+'\"]');}"
                    + "function openCard(card){if(window.reportOpenScenario)return Promise.resolve(window.reportOpenScenario(card,true));"
                    + "var body=card.querySelector('.scenario-fold-body');"
                    + "if(body){body.hidden=false;card.classList.add('is-open');}return Promise.resolve();}"
                    + "function openFold(el){var card=el.closest&&el.closest('.scenario-detail-card');if(card)openCard(card);}"
                    + "function s(el){if(!el)return;openFold(el);reveal(el);void el.offsetHeight;"
                    + "el.scrollIntoView({behavior:'auto',block:'start'});"
                    + "requestAnimationFrame(function(){el.scrollIntoView({behavior:'auto',block:'start'})})}"
                    + "function h(hash){if(!hash||hash.charAt(0)!=='#')return;var id=hash.slice(1);"
                    + "var el=document.getElementById(id);if(el){s(el);return;}"
                    + "var c=cardFor(id);if(c)openCard(c).then(function(){s(document.getElementById(id));});}"
                    + "function navClick(e){var a=e.target.closest&&e.target.closest('a[href^=\"#\"]');"
                    + "if(!a||!a.closest('.report-sidebar'))return;"
                    + "var id=a.getAttribute('href');if(!id||id==='#')return;"
                    + "e.preventDefault();h(id);try{history.replaceState(null,'',id)}catch(x){}}"
                    + "function mainClick(e){var a=e.target.closest&&e.target.closest('a[href^=\"#\"]');"
                    + "if(!a||!a.closest('.report-main'))return;"
                    + "var id=a.getAttribute('href');if(!id||id==='#')return;"
                    + "var el=document.getElementById(id.slice(1));"
                    + "if(el?!el.closest('.report-main'):!cardFor(id.slice(1)))return;"
                    + "e.preventDefault();h(id)}"
                    + "function init(){document.querySelector('.report-sidebar')?.addEventListener('click',navClick);"
                    + "document.querySelector('.report-main')?.addEventListener('click',mainClick);"
                    + "h(location.hash||'#report-exec-summary');"
                    + "if(!document.getElementById('report-exec-summary'))h('#summary')}"
                    + "if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',init);"
                    + "else init()})();</script>";

    private static final Pattern SUMMARY_TABLE =
            Pattern.compile("<table\\b[^>]*\\bid\\s*=\\s*['\"]summary['\"][^>]*>(.*?)</table>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);


    private static final Pattern SCENARIO_ROW_TWO_COL =
            Pattern.compile(
                    "<tr class=\"([^\"]+)\"><td><a href=\"#(m\\d+)\">([^<]+)</a></td>"
                            + "<td[^>]*\\bclass=['\"]bug-column['\"][^>]*>(.*?)</td></tr>",
                    Pattern.DOTALL);


    private static final Pattern SCENARIO_ROW_THREE_COL =
            Pattern.compile(
                    "<tr class=\"([^\"]+)\"><td><a href=\"#(m\\d+)\">([^<]+)</a></td>"
                            + "<td[^>]*>(.*?)</td><td[^>]*class=['\"]bug-column['\"]>(.*?)</td></tr>",
                    Pattern.DOTALL);

    private static final Pattern H3_ANCHOR = Pattern.compile("<h3[^>]*\\bid=['\"](m\\d+)['\"][^>]*>", Pattern.CASE_INSENSITIVE);


    private static final Pattern STEP_CAPTURE_LINE =
            Pattern.compile(
                    "<span class=['\"]step-capture-label['\"]>([^<]*)</span>\\s*:\\s*<span class=['\"]step-capture-value[^'\"]*['\"]>([^<]*)</span>",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final Pattern TEXTAREA_BODY =
            Pattern.compile("(<textarea\\b[^>]*>)([\\s\\S]*?)(</textarea>)", Pattern.CASE_INSENSITIVE);

    private static final Pattern STEP_CAPTURE_LEGACY_BLOCK =
            Pattern.compile(
                    "<div style='padding: 0; margin: 0;'><textarea style='border: solid 1px gray; background-color: lightgray; width: 100%; padding: 0; margin: 0;' name='headers' rows='3' readonly='true'>(.*?)</textarea></div>",
                    Pattern.DOTALL);

    private static final Pattern LEGEND_BR = Pattern.compile("<br\\s*/?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern LEGEND_ICON = Pattern.compile("^(&#(?:\\d+|x[0-9a-fA-F]+);)\\s*(.*)$", Pattern.DOTALL);
    private static final Pattern EXEC_LEGEND_TD =
            Pattern.compile(
                    "(<td[^>]*\\bclass=['\"]exec-legend['\"][^>]*>)(.*?)(</td>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final Pattern STATS_HEADER_ROW =
            Pattern.compile("</td></tr>\\s*(<th\\s+style=['\"]text-align:center;['\"]>)", Pattern.CASE_INSENSITIVE);

    private static final Pattern TC_MARKING_BANNER =
            Pattern.compile(
                    "<b\\s+style=\"background-color:\\s*(#[0-9a-fA-F]{3,8});?\">\\s*(Marking\\s+test\\s+case[^<]*?)</b>",
                    Pattern.CASE_INSENSITIVE);


    private static final Pattern HTTP_REQ =
            Pattern.compile(
                    "(?i)<b><u>Request:\\s*</u></b>(\\(End Point URL:[^<]*?\\)\\s*)<pre><div style='padding: 0; margin: 0;'><textarea style='border: solid 1px gray");

    private static final Pattern HTTP_RESP =
            Pattern.compile(
                    "(?i)<b><u>Response:\\s*</u></b>\\s*<pre><div style='padding: 0; margin: 0;'><textarea style='border: solid 1px gray",
                    Pattern.DOTALL);

    private static final Pattern VALIDATION_PANEL =
            Pattern.compile(
                    "(?:<br/>\\s*){1,2}<b>\\s*Output validation:\\s*</b>\\s*EXPECTED vs ACTUAL\\s*"
                            + "<table width='100%' charset='UTF8'>(.*?)</table>",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final Pattern HEAD = Pattern.compile("<head>.*?</head>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /** EmailableReport writes {@code <body style='...'>}, not a bare {@code <body>}. */
    private static final Pattern BODY_OPEN = Pattern.compile("<body\\b[^>]*>", Pattern.CASE_INSENSITIVE);

    /** {@code <h3 id="m12">} from EmailableReport, before the tree-view card wrapper. */
    private static final Pattern PLAIN_SCENARIO_H3 =
            Pattern.compile("<h3\\s+id=(['\"])(m\\d+)\\1>", Pattern.CASE_INSENSITIVE);
    private static final Pattern TITLE_H2 = Pattern.compile("<h2[^>]*>([^<]+)</h2>", Pattern.CASE_INSENSITIVE);
    private static final Pattern TITLE_TAG = Pattern.compile("<title>([^<]*)</title>", Pattern.CASE_INSENSITIVE);

    private static final Pattern NAV_BY_ID =
            Pattern.compile(
                    "<nav\\b[^>]*\\bid\\s*=\\s*['\"]report-scenario-tree['\"][^>]*>.*?</nav>",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final Pattern NAV_LEGACY =
            Pattern.compile(
                    "<nav\\b[^>]*class\\s*=\\s*['\"]tree-nav['\"][^>]*\\baria-label\\s*=\\s*['\"]Scenario outline['\"][^>]*>.*?</nav>",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    // Matches any <div> whose class attribute contains "step-capture-card", regardless of
    // additional classes or other attributes on the tag. The original strict pattern
    // "<div class=['"]step-capture-card['"]>" was invisible to any card logged with extra
    // attributes (e.g. data-step="...", style="...") or extra CSS classes. Those scenarios
    // would then produce no step IDs and appear to have zero steps in the sidebar.
    private static final Pattern STEP_CAPTURE_DIV =
            Pattern.compile(
                    "<div\\b[^>]*\\bclass\\s*=\\s*['\"][^'\"]*\\bstep-capture-card\\b[^'\"]*['\"][^>]*>",
                    Pattern.CASE_INSENSITIVE);


    private static final Pattern SCENARIO_DETAIL_SECTION_OPEN =
            Pattern.compile(
                    "<section\\s+[^>]*\\bclass\\s*=\\s*['\"][^'\"]*\\bscenario-detail-card\\b",
                    Pattern.CASE_INSENSITIVE);


    private static final String SCENARIO_SECTION_OPEN = "<section class=\"scenario-detail-card";

    private static final String SCENARIO_SECTION_CLOSE = "</div></section>";

    private static final String SCENARIO_FOLD_BODY_OPEN = "<div class=\"scenario-fold-body\"";

    private static final String SCENARIO_FOLD_TITLE_STYLE =
            "<style>.report-main .scenario-fold-title{font-size:var(--fs-body);font-weight:600}</style>";

    private static final Pattern SCENARIO_BODY_TITLE =
            Pattern.compile("class=['\"]box black-bg[^>]*>\\s*<b><u>([^<]{1,600})</u></b>", Pattern.CASE_INSENSITIVE);

    private static final Pattern SCENARIO_DETAIL_CARD_CHROME =
            Pattern.compile(
                    "<div\\s+class\\s*=\\s*['\"]scenario-detail-card__chrome['\"][^>]*>[\\s\\S]*?</div>\\s*</div>\\s*",
                    Pattern.CASE_INSENSITIVE);

    private static volatile String headTemplate;

    private static final String TEXTAREA_TRUNCATE_NOTE =
            "\n\n… [truncated for report load time — see test run logs for full payload]";

    /** Max chars per textarea when preparing / enhancing the tree-view report. */
    private static final int REPORT_TEXTAREA_MAX_CHARS = 8192;

    /** Stream-shrink pass runs when the on-disk report is at least this large. */
    private static final int REPORT_STREAM_TRUNCATE_MIN_BYTES = 8 * 1024 * 1024;

    /** Skip tree-view enhancement above this in-memory size (plain HTML kept). */
    private static final int REPORT_TREE_VIEW_MAX_ENHANCE_INPUT_CHARS = 80_000_000;

    private enum TextareaScanState {
        NORMAL,
        TEXTAREA_TAG,
        TEXTAREA_BODY,
        TEXTAREA_SKIP
    }

    private ReportTreeViewEnhancer() {}


    public static boolean enhanceReportFileInPlace(File reportFile, Logger log) {
        Objects.requireNonNull(log, "log");
        if (reportFile == null) {
            log.error("ReportTreeViewEnhancer: report file is null; skip.");
            return false;
        }
        if (!reportFile.isFile()) {
            log.warn("ReportTreeViewEnhancer: not a regular file, skip: " + reportFile.getAbsolutePath());
            return false;
        }
        Path target = reportFile.toPath().toAbsolutePath().normalize();
        log.info("ReportTreeViewEnhancer: detected report file for enhancement at " + target);
        try {
            streamPrepareReportIfLarge(target, log);
            String html = Files.readString(target, StandardCharsets.UTF_8);
            html = applyReportPayloadLimits(html);
            if (html.length() > REPORT_TREE_VIEW_MAX_ENHANCE_INPUT_CHARS) {
                log.warn(
                        "ReportTreeViewEnhancer: report still too large for tree-view enhancement ("
                                + html.length()
                                + " chars > limit "
                                + REPORT_TREE_VIEW_MAX_ENHANCE_INPUT_CHARS
                                + "); leaving plain HTML.");
                return true;
            }
            if (hasReportShellLayout(html)) {
                String patched = deferScenarioBodies(applyReportPayloadLimits(patchExistingTreeViewReport(html)));
                if (!patched.equals(html)) {
                    Path parent = target.getParent();
                    if (parent == null) {
                        log.error("ReportTreeViewEnhancer: cannot resolve parent directory for " + target);
                        return false;
                    }
                    Path tmp = Files.createTempFile(parent, "treeview-patch-", ".html");
                    try {
                        Files.writeString(tmp, patched, StandardCharsets.UTF_8);
                        Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
                        log.info("ReportTreeViewEnhancer: patched existing tree-view report at " + target);
                    } catch (IOException e) {
                        try {
                            Files.deleteIfExists(tmp);
                        } catch (IOException ignored) {

                        }
                        throw e;
                    }
                } else {
                    log.info("ReportTreeViewEnhancer: file already has tree-view layout; skip transformation.");
                }
                return true;
            }
            log.info("ReportTreeViewEnhancer: enhancement starting (" + html.length() + " characters).");
            String expanded = enhanceExpanded(html);
            String enhanced = deferScenarioBodies(expanded);
            Path parent = target.getParent();
            if (parent == null) {
                log.error("ReportTreeViewEnhancer: cannot resolve parent directory for " + target);
                return false;
            }
            Path tmp = Files.createTempFile(parent, "treeview-enh-", ".html");
            try {
                Files.writeString(tmp, enhanced, StandardCharsets.UTF_8);
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                try {
                    Files.deleteIfExists(tmp);
                } catch (IOException ignored) {

                }
                throw e;
            }
            log.info(
                    "ReportTreeViewEnhancer: enhanced report written in place; previous plain HTML removed/replaced: "
                            + target);
            boolean integrityOk = ReportIntegrityValidator.validate(expanded);
            if (!integrityOk) {
                log.error("ReportTreeViewEnhancer: INTEGRITY CHECK FAILED for " + target
                        + " — sidebar/panel mismatch detected. See [REPORT-INTEGRITY] log lines above.");
            } else {
                log.info("ReportTreeViewEnhancer: integrity check passed for " + target);
            }
            return true;
        } catch (OutOfMemoryError oom) {
            log.error(
                    "ReportTreeViewEnhancer: out of memory during enhancement; leaving existing report unchanged: "
                            + target,
                    oom);
            return false;
        } catch (Exception e) {
            log.error("ReportTreeViewEnhancer: enhancement failed; leaving existing report unchanged: " + target, e);
            return false;
        }
    }


    private static void streamPrepareReportIfLarge(Path target, Logger log) throws IOException {
        long size = Files.size(target);
        if (size < REPORT_STREAM_TRUNCATE_MIN_BYTES) {
            return;
        }
        int maxChars = REPORT_TEXTAREA_MAX_CHARS;
        Path parent = target.getParent();
        if (parent == null) {
            log.warn("ReportTreeViewEnhancer: cannot stream-shrink report without parent directory: " + target);
            return;
        }
        log.info(
                "ReportTreeViewEnhancer: streaming textarea shrink for large report ("
                        + size
                        + " bytes, max "
                        + maxChars
                        + " chars per textarea).");
        Path tmp = Files.createTempFile(parent, "report-slim-", ".html");
        try {
            streamTruncateTextareas(target, tmp, maxChars);
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("ReportTreeViewEnhancer: shrunk report to " + Files.size(target) + " bytes.");
        } catch (IOException e) {
            try {
                Files.deleteIfExists(tmp);
            } catch (IOException ignored) {

            }
            throw e;
        }
    }


    static void streamTruncateTextareas(Path in, Path out, int maxChars) throws IOException {
        if (maxChars <= 0) {
            Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
            return;
        }
        try (Reader reader = Files.newBufferedReader(in, StandardCharsets.UTF_8);
                Writer writer = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            TextareaScanState state = TextareaScanState.NORMAL;
            StringBuilder tag = new StringBuilder(128);
            StringBuilder closeProbe = new StringBuilder(12);
            int bodyWritten = 0;
            boolean truncateNoteWritten = false;
            int ch;
            while ((ch = reader.read()) != -1) {
                char c = (char) ch;
                switch (state) {
                    case NORMAL -> {
                        if (c == '<') {
                            tag.setLength(0);
                            tag.append(c);
                            state = TextareaScanState.TEXTAREA_TAG;
                        } else {
                            writer.write(c);
                        }
                    }
                    case TEXTAREA_TAG -> {
                        tag.append(c);
                        if (c == '>') {
                            String tagText = tag.toString();
                            String tagLower = tagText.toLowerCase();
                            if (tagLower.startsWith("<textarea") && tagLower.contains("step-capture-raw")) {
                                state = TextareaScanState.TEXTAREA_SKIP;
                                closeProbe.setLength(0);
                            } else if (tagLower.startsWith("<textarea")) {
                                writer.write(tagText);
                                state = TextareaScanState.TEXTAREA_BODY;
                                bodyWritten = 0;
                                truncateNoteWritten = false;
                                closeProbe.setLength(0);
                            } else {
                                writer.write(tagText);
                                state = TextareaScanState.NORMAL;
                            }
                            tag.setLength(0);
                        } else if (tag.length() > 512) {
                            writer.write(tag.toString());
                            tag.setLength(0);
                            state = TextareaScanState.NORMAL;
                        }
                    }
                    case TEXTAREA_BODY -> {
                        if (bodyWritten < maxChars) {
                            writer.write(c);
                            bodyWritten++;
                            // Track </textarea> close so we exit TEXTAREA_BODY when the
                            // textarea ends normally (body smaller than maxChars). Without
                            // this the state machine stays stuck in TEXTAREA_BODY and counts
                            // subsequent scenario HTML as body bytes — eventually hitting
                            // maxChars mid-scenario and silently dropping H3 anchors.
                            closeProbe.append(Character.toLowerCase(c));
                            if (closeProbe.length() > 12) {
                                closeProbe.delete(0, closeProbe.length() - 12);
                            }
                            if (endsWithTextareaClose(closeProbe)) {
                                // The </textarea> chars were already written above one by one.
                                state = TextareaScanState.NORMAL;
                                closeProbe.setLength(0);
                            }
                        } else {
                            if (!truncateNoteWritten) {
                                writer.write(TEXTAREA_TRUNCATE_NOTE);
                                truncateNoteWritten = true;
                            }
                            state = TextareaScanState.TEXTAREA_SKIP;
                            closeProbe.setLength(0);
                            closeProbe.append(Character.toLowerCase(c));
                            if (endsWithTextareaClose(closeProbe)) {
                                writer.write("</textarea>");
                                state = TextareaScanState.NORMAL;
                                closeProbe.setLength(0);
                            }
                        }
                    }
                    case TEXTAREA_SKIP -> {
                        closeProbe.append(Character.toLowerCase(c));
                        if (closeProbe.length() > 12) {
                            closeProbe.delete(0, closeProbe.length() - 12);
                        }
                        if (endsWithTextareaClose(closeProbe)) {
                            writer.write("</textarea>");
                            state = TextareaScanState.NORMAL;
                            closeProbe.setLength(0);
                        }
                    }
                    default -> {
                        writer.write(c);
                        state = TextareaScanState.NORMAL;
                    }
                }
            }
            if (state == TextareaScanState.TEXTAREA_TAG && tag.length() > 0) {
                writer.write(tag.toString());
            }
        }
    }


    private static boolean endsWithTextareaClose(CharSequence probe) {
        return probe.length() >= 11 && probe.toString().endsWith("</textarea>");
    }


    public static String enhance(String html) throws IOException {
        return deferScenarioBodies(enhanceExpanded(html));
    }

    /** Tree-view HTML with scenario bodies still inline (before {@link #deferScenarioBodies}). */
    static String enhanceExpanded(String html) throws IOException {
        if (html != null) {
            html = applyReportPayloadLimits(html);
        }
        if (hasReportShellLayout(html)) {
            return patchExistingTreeViewReport(html);
        }
        String title = resolveTitle(html);
        if (!title.toLowerCase().contains("tree view")) {
            title = title + " (tree view)";
        }

        String summaryBlock = extractSummaryTable(html);
        List<ScenarioRow> rows = summaryBlock != null ? parseScenarioRows(summaryBlock) : List.of();

        String treeHtml = buildTreeNav(rows);
        String themeBar =
                "<div class=\"theme-toolbar\" role=\"region\" aria-label=\"Theme\">"
                        + "<button type=\"button\" id=\"report-theme-toggle\" class=\"theme-toggle\" "
                        + "aria-pressed=\"false\" title=\"Switch color theme\">Dark mode</button>"
                        + "</div>";

        String out = HEAD.matcher(html).replaceFirst(Matcher.quoteReplacement(headBlock(title)));

        Matcher bodyOpen = BODY_OPEN.matcher(out);
        if (bodyOpen.find()) {
            out =
                    bodyOpen.replaceFirst(
                            Matcher.quoteReplacement(
                                    "<body class=\"report-document report-document--readonly\"><div class=\"report-shell\"><div class=\"report-sidebar sidebar\">"
                                            + themeBar
                                            + treeHtml
                                            + "</div><div class=\"report-main\">"));
        }
        out =
                replaceLastLiteral(
                        out,
                        "</body>",
                        REPORT_SHELL_CLOSE
                                + REPORT_THEME_SCRIPT
                                + REPORT_TREE_ACCORDION_SCRIPT
                                + REPORT_TREE_FILTER_SCRIPT
                                + REPORT_UX_SCRIPT
                                + REPORT_MAIN_SCROLL_SCRIPT
                                + "\n</body>");

        out = annotateReportBody(out);

        if (summaryBlock != null && !rows.isEmpty()) {
            rows = attachStepsToRows(out, rows);
            String newNav = buildTreeNav(rows);
            Matcher m = NAV_BY_ID.matcher(out);
            if (m.find()) {
                out = NAV_BY_ID.matcher(out).replaceFirst(Matcher.quoteReplacement(newNav));
            } else {
                out = NAV_LEGACY.matcher(out).replaceFirst(Matcher.quoteReplacement(newNav));
            }
        }
        out = ensureScenarioAnchorClass(out);
        out = wrapScenarioDetailCards(out);
        out = stripScenarioDetailCardChrome(out);
        return out;
    }

    /**
     * The head template's CSS contains the selector {@code .report-shell}. Matching that
     * string treats a plain emailable report as already enhanced and skips the sidebar.
     */
    private static boolean hasReportShellLayout(String html) {
        return html != null && html.contains("<div class=\"report-shell\"");
    }

    /** Cards are built only for H3s that carry {@code scenario-anchor}. EmailableReport omits it. */
    private static String ensureScenarioAnchorClass(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        Matcher m = PLAIN_SCENARIO_H3.matcher(html);
        StringBuffer sb = new StringBuffer(html.length() + 256);
        while (m.find()) {
            m.appendReplacement(
                    sb,
                    Matcher.quoteReplacement(
                            "<h3 id=\"" + m.group(2) + "\" class=\"scenario-anchor\">"));
        }
        m.appendTail(sb);
        return sb.toString();
    }


    private static String applyReportPayloadLimits(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        html = html.replaceAll("<textarea[^>]*\\bstep-capture-raw\\b[^>]*>[\\s\\S]*?</textarea>\\s*", "");
        return truncateLargeTextareaContent(html, REPORT_TEXTAREA_MAX_CHARS);
    }


    static String truncateLargeTextareaContent(String html, int maxChars) {
        Matcher m = TEXTAREA_BODY.matcher(html);
        StringBuffer sb = new StringBuffer(html.length());
        while (m.find()) {
            String body = m.group(2);
            if (body.length() <= maxChars) {
                m.appendReplacement(sb, Matcher.quoteReplacement(m.group(0)));
                continue;
            }
            String trimmed = body.substring(0, maxChars) + TEXTAREA_TRUNCATE_NOTE;
            m.appendReplacement(sb, Matcher.quoteReplacement(m.group(1) + trimmed + m.group(3)));
        }
        m.appendTail(sb);
        return sb.toString();
    }


    private static String patchExistingTreeViewReport(String html) {
        String out = relocateShellCloseAndScripts(html);
        if (!out.contains(".scenario-fold-title")) {
            int headClose = out.indexOf("</head>");
            if (headClose >= 0) {
                out = out.substring(0, headClose) + SCENARIO_FOLD_TITLE_STYLE + out.substring(headClose);
            }
        }
        out = ensureReportExecSummaryAnchor(out);
        out =
                out.replace(
                        "href=\"#summary\" role=\"treeitem\"><span class=\"tree-icon\"",
                        "href=\"#report-exec-summary\" role=\"treeitem\"><span class=\"tree-icon\"");
        out = upgradeReportUx(out);
        out = upgradeScrollScript(out);
        if (!out.contains(REPORT_MAIN_SCROLL_MARKER)) {
            String inject = REPORT_MAIN_SCROLL_SCRIPT + "\n";
            if (out.contains("</body>")) {
                out = replaceLastLiteral(out, "</body>", inject + "</body>");
            }
        }
        // Repair scenario-detail-card wrappers that were missed in a previous enhancement
        // pass (e.g. because findClosingTableStartIndex hit a </table> inside a textarea
        // payload and wrapScenarioDetailCards silently skipped those scenarios). Since
        // wrapScenarioDetailCards now detects already-wrapped H3s and skips them, calling
        // it here is safe: it only wraps the orphaned H3s that still lack a section.
        out = ensureScenarioAnchorClass(out);
        out = wrapScenarioDetailCards(out);
        return stripScenarioDetailCardChrome(stripSlowestStrip(out));
    }


    /**
     * Older enhancement runs inserted the shell close and all report scripts before the first
     * {@code </body>}, which can be inside a payload textarea (e.g. an nginx 504 page). The
     * scripts then render as text and never run. Move that block back before the document's
     * last {@code </body>}.
     */
    private static String relocateShellCloseAndScripts(String html) {
        int start = html.indexOf(REPORT_SHELL_CLOSE + "<script>(function(){function s(){var t=document.documentElement");
        if (start < 0) {
            return html;
        }
        int bodyClose = html.indexOf("</body>", start);
        if (bodyClose < 0 || bodyClose == html.lastIndexOf("</body>")) {
            return html;
        }
        String block = html.substring(start, bodyClose);
        String without = html.substring(0, start) + html.substring(bodyClose);
        return replaceLastLiteral(without, "</body>", block + "</body>");
    }

    private static String upgradeReportUx(String html) {
        if (html.contains(REPORT_UX_MARKER)) {
            return html;
        }
        for (String legacyMarker : REPORT_UX_LEGACY_MARKERS) {
            int legacyIdx = html.indexOf(legacyMarker);
            if (legacyIdx >= 0) {
                int legacyStart = html.lastIndexOf("<script>", legacyIdx);
                int legacyEnd = html.indexOf("</script>", legacyIdx);
                if (legacyStart >= 0 && legacyEnd >= 0) {
                    html = html.substring(0, legacyStart) + html.substring(legacyEnd + 9);
                }
            }
        }
        String sig = "nav.querySelectorAll('li.tree-scenario-node')";
        int sigIdx = html.indexOf(sig);
        String inject = REPORT_TREE_FILTER_SCRIPT + REPORT_UX_SCRIPT;
        if (sigIdx >= 0) {
            int scriptStart = html.lastIndexOf("<script>", sigIdx);
            int scriptEnd = html.indexOf("</script>", sigIdx);
            if (scriptStart >= 0 && scriptEnd >= 0) {
                return html.substring(0, scriptStart) + inject + html.substring(scriptEnd + 9);
            }
        }
        if (html.contains("</body>")) {
            return replaceLastLiteral(html, "</body>", inject + "\n</body>");
        }
        return html;
    }


    private static String upgradeScrollScript(String html) {
        if (html.contains(REPORT_MAIN_SCROLL_MARKER)) {
            return html;
        }
        String oldSignature = "h(location.hash||'#report-exec-summary'";
        int sigIdx = html.indexOf(oldSignature);
        if (sigIdx < 0) {
            oldSignature = "getBoundingClientRect().top-p.getBoundingClientRect().top+p.scrollTop";
            sigIdx = html.indexOf(oldSignature);
        }
        if (sigIdx < 0) {
            return html;
        }
        int scriptStart = html.lastIndexOf("<script>", sigIdx);
        if (scriptStart < 0) {
            return html;
        }
        int scriptEnd = html.indexOf("</script>", sigIdx);
        if (scriptEnd < 0) {
            return html;
        }
        return html.substring(0, scriptStart) + REPORT_MAIN_SCROLL_SCRIPT + html.substring(scriptEnd + 9);
    }


    private static String stripScenarioDetailCardChrome(String html) {
        if (html == null || html.isEmpty() || !html.contains("scenario-detail-card__chrome")) {
            return html;
        }
        String out = SCENARIO_DETAIL_CARD_CHROME.matcher(html).replaceAll("");
        return out.replace("<motion class=\"scenario-detail-card__inner\">", "<div class=\"scenario-detail-card__inner\">");
    }


    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println(
                    "Usage: java report.treeview.ReportTreeViewEnhancer <input.html> [output.html]\n"
                            + "  Default output: <stem>-tree-view-java.html");
            System.exit(2);
        }
        Path in = Paths.get(args[0]);
        if (!Files.isRegularFile(in)) {
            System.err.println("Not found: " + in);
            System.exit(1);
        }
        Path out;
        if (args.length >= 2) {
            out = Paths.get(args[1]);
        } else {
            String name = in.getFileName().toString();
            String stem = name.endsWith(".html") ? name.substring(0, name.length() - 5) : name;
            out = in.resolveSibling(stem + "-tree-view-java.html");
        }
        String html = Files.readString(in, StandardCharsets.UTF_8);
        Files.writeString(out, enhance(html), StandardCharsets.UTF_8);
        System.out.println("Wrote " + out);
    }

    private static String resolveTitle(String html) {
        Matcher h2 = TITLE_H2.matcher(html);
        if (h2.find()) {
            return h2.group(1).trim();
        }
        Matcher t = TITLE_TAG.matcher(html);
        if (t.find() && !t.group(1).trim().isEmpty()) {
            return t.group(1).trim();
        }
        return "Test report";
    }

    private static String headBlock(String title) throws IOException {
        return getHeadTemplate().replace("__REPORT_TITLE__", escapeHtml(title));
    }

    private static String getHeadTemplate() throws IOException {
        String cached = headTemplate;
        if (cached != null) {
            return cached;
        }
        synchronized (ReportTreeViewEnhancer.class) {
            if (headTemplate != null) {
                return headTemplate;
            }
            try (InputStream in =
                    ReportTreeViewEnhancer.class.getResourceAsStream("/config/head-template.html")) {
                if (in == null) {
                    throw new IOException(
                            "Missing classpath resource /config/head-template.html (expected next to dsl.properties).");
                }
                headTemplate = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                return headTemplate;
            }
        }
    }

    static String escapeHtml(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }


    private static String escapeForTextareaText(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;");
    }


    private static String decodeMinimalHtmlEntities(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        return s.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
    }


    private static String replaceFirstLiteral(String s, String from, String to) {
        int i = s.indexOf(from);
        if (i < 0) {
            return s;
        }
        int fromLen = from.length();
        StringBuilder sb = new StringBuilder(s.length() - fromLen + to.length());
        sb.append(s, 0, i);
        sb.append(to);
        sb.append(s, i + fromLen, s.length());
        return sb.toString();
    }

    private static String replaceLastLiteral(String s, String from, String to) {
        int i = s.lastIndexOf(from);
        if (i < 0) {
            return s;
        }
        int fromLen = from.length();
        StringBuilder sb = new StringBuilder(s.length() - fromLen + to.length());
        sb.append(s, 0, i);
        sb.append(to);
        sb.append(s, i + fromLen, s.length());
        return sb.toString();
    }

    private static String extractSummaryTable(String html) {
        Matcher m = SUMMARY_TABLE.matcher(html);
        return m.find() ? m.group(0) : null;
    }

    private static List<ScenarioRow> parseScenarioRows(String summaryHtml) {
        List<ScenarioRow> rows = new ArrayList<>();
        Matcher m3 = SCENARIO_ROW_THREE_COL.matcher(summaryHtml);
        while (m3.find()) {
            String cls = m3.group(1);
            String anchor = m3.group(2);
            String name = m3.group(3).trim();
            String descHtml = m3.group(4);
            String timingHtml = m3.group(5);
            String timing = timingHtml.replaceAll("<[^>]+>", "").trim();
            String desc = descHtml.replaceAll("<[^>]+>", "").trim();
            rows.add(new ScenarioRow(cls, anchor, name, desc, timing, statusFromRowClass(cls)));
        }
        if (!rows.isEmpty()) {
            return rows;
        }
        Matcher m2 = SCENARIO_ROW_TWO_COL.matcher(summaryHtml);
        while (m2.find()) {
            String cls = m2.group(1);
            String anchor = m2.group(2);
            String name = m2.group(3).trim();
            String timingHtml = m2.group(4);
            String timing = timingHtml.replaceAll("<[^>]+>", "").trim();
            rows.add(new ScenarioRow(cls, anchor, name, "", timing, statusFromRowClass(cls)));
        }
        return rows;
    }

    private static String statusFromRowClass(String cls) {
        String c = cls.toLowerCase();
        if (c.contains("passed")) return "pass";
        if (c.contains("failed")) return "fail";
        if (c.contains("skipped")) return "skip";
        if (c.contains("ignored")) return "ignore";
        if (c.contains("knownissue")) return "known";
        return "unknown";
    }


    private static String scenarioStatusLabel(String status) {
        return switch (status) {
            case "pass" -> "Passed";
            case "ignore" -> "Ignored";
            case "known" -> "Known issues";
            case "skip" -> "Skipped";
            case "fail" -> "Failed";
            default -> "Unknown";
        };
    }

    private static String buildTreeFilterToolbar(List<ScenarioRow> rows) {
        if (rows.isEmpty()) {
            return "";
        }
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String k : List.of("pass", "ignore", "known", "skip", "fail", "unknown")) {
            counts.put(k, 0);
        }
        for (ScenarioRow r : rows) {
            counts.merge(r.status, 1, Integer::sum);
        }
        int nAll = rows.size();
        int nFail = counts.getOrDefault("fail", 0);
        String defaultFilter = nFail > 0 ? "fail" : "all";
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"tree-filter-toolbar\" role=\"toolbar\" aria-label=\"Filter scenarios by outcome\">");
        sb.append("<input class=\"tree-search\" type=\"search\" placeholder=\"Search scenarios\" aria-label=\"Search scenarios\">");
        sb.append("<div class=\"tree-filter-chips\">");
        appendFilterChip(sb, "all", "All", nAll, "all".equals(defaultFilter));

        List<String> filterOrder = List.of("pass", "ignore", "known", "skip", "fail", "unknown");
        for (String key : filterOrder) {
            int cnt = counts.getOrDefault(key, 0);
            if (cnt == 0) continue;
            appendFilterChip(sb, key, scenarioStatusLabel(key), cnt, key.equals(defaultFilter));
        }
        sb.append("</div>");
        if (nFail > 0) {
            sb.append("<button type=\"button\" class=\"tree-next-fail\">Next failure</button>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private static void appendFilterChip(StringBuilder sb, String key, String label, int count, boolean active) {
        sb.append("<button type=\"button\" class=\"tree-filter-chip");
        if (!"all".equals(key)) {
            sb.append(" tree-filter-chip--").append(key);
        }
        if (active) {
            sb.append(" is-active");
        }
        sb.append("\" data-tree-filter=\"").append(key).append("\" aria-pressed=\"")
                .append(active ? "true" : "false")
                .append("\">")
                .append(label)
                .append("<span class=\"tree-filter-chip-n\" aria-hidden=\"true\">(")
                .append(count)
                .append(")</span></button>");
    }

    private static String stripSlowestStrip(String html) {
        String marker = "<div class=\"tree-slow\"";
        int start = html.indexOf(marker);
        if (start < 0) {
            return html;
        }
        int depth = 0;
        int i = start;
        while (i < html.length()) {
            int open = html.indexOf("<div", i);
            int close = html.indexOf("</div>", i);
            if (close < 0) {
                return html;
            }
            if (open >= 0 && open < close) {
                depth++;
                i = open + 4;
                continue;
            }
            depth--;
            i = close + 6;
            if (depth == 0) {
                return html.substring(0, start) + html.substring(i);
            }
        }
        return html;
    }

    private static String suiteLifecycleTreeClass(String scenarioName) {
        if (scenarioName == null) {
            return "";
        }
        String n = scenarioName.trim();
        if ("Scenario_0".equals(n) || "Scenario_AFTER_SUITE".equals(n)) {
            return " tree-scenario-node--suite-lifecycle";
        }
        return "";
    }

    private static String buildTreeNav(List<ScenarioRow> rows) {
        StringBuilder parts = new StringBuilder();
        parts.append("<nav id=\"report-scenario-tree\" class=\"tree-nav\" aria-label=\"Scenario outline\">");
        parts.append("<div class=\"tree-nav-title\">Scenarios</div>");
        parts.append(buildTreeFilterToolbar(rows));
        parts.append("<ul class=\"tree tree-root\" role=\"tree\">");
        parts.append("<li class=\"tree-item depth-0\" role=\"none\">");
        parts.append("<a class=\"tree-link root-link\" href=\"#report-exec-summary\" role=\"treeitem\">");
        parts.append("<span class=\"tree-icon\" aria-hidden=\"true\">◆</span>");
        parts.append("<span class=\"tree-label\">Summary &amp; metrics</span>");
        parts.append("</a></li>");
        for (ScenarioRow r : rows) {
            String st = r.status;
            String stLbl = scenarioStatusLabel(st);
            String tip = escapeHtml(r.name + " — " + stLbl);
            parts.append("<li class=\"tree-item depth-1 tree-scenario-node scenario-card status-")
                    .append(st)
                    .append(suiteLifecycleTreeClass(r.name))
                    .append("\" data-status=\"")
                    .append(st)
                    .append("\" data-name=\"")
                    .append(escapeHtml(r.name))
                    .append("\" role=\"none\">");
            parts.append("<a class=\"tree-link tree-link--scenario\" href=\"#")
                    .append(r.anchor)
                    .append("\" role=\"treeitem\" title=\"")
                    .append(tip)
                    .append("\">");
            parts.append("<span class=\"tree-scenario-row\">");
            parts.append("<span class=\"tree-scenario-left\">");
            parts.append("<span class=\"status-dot ").append(st).append("\" aria-hidden=\"true\"></span>");
            parts.append("<span class=\"tree-name scenario-title\">").append(escapeHtml(r.name)).append("</span>");
            parts.append("</span>");
            parts.append("<span class=\"tree-scenario-right\">");
            parts.append("<span class=\"tree-scenario-status status-badge tree-scenario-status--")
                    .append(st)
                    .append("\">")
                    .append(escapeHtml(stLbl))
                    .append("</span>");
            if (!r.timing.isEmpty()) {
                parts.append("<span class=\"tree-time scenario-duration\">").append(escapeHtml(r.timing)).append("</span>");
            }
            parts.append("</span></span>");
            parts.append("</a>");
            String descTrim = r.desc == null ? "" : r.desc.trim();
            if (!descTrim.isEmpty()) {
                parts.append("<div class=\"tree-scenario-desc scenario-description\">").append(escapeHtml(descTrim)).append("</div>");
            }
            List<StepEntry> steps = r.steps != null ? r.steps : List.of();
            if (!steps.isEmpty()) {
                int n = steps.size();
                parts.append("<details class=\"tree-steps-details\" aria-label=\"Steps for this scenario\">");
                parts.append("<summary class=\"tree-steps-summary\">");
                parts.append("<span class=\"tree-steps-chevron\" aria-hidden=\"true\"></span>");
                parts.append("<span class=\"tree-steps-summary-label\">");
                parts.append("<span class=\"tree-steps-lbl-closed\">Show steps</span>");
                parts.append("<span class=\"tree-steps-lbl-open\">Hide steps</span>");
                parts.append("</span>");
                parts.append("<span class=\"tree-steps-count\">(").append(n).append(")</span>");
                parts.append("</summary>");
                parts.append("<ul class=\"tree tree-steps\" role=\"group\">");
                for (int j = 0; j < steps.size(); j++) {
                    StepEntry stp = steps.get(j);
                    String label = stp.label;
                    String desc = stp.desc == null ? "" : stp.desc.trim();
                    String tipS = desc.isEmpty() ? label : escapeHtml(label + " — " + desc);
                    parts.append("<li class=\"tree-item depth-2\" role=\"none\">");
                    parts.append("<a class=\"tree-link tree-step-link\" href=\"#")
                            .append(stp.id)
                            .append("\" role=\"treeitem\" title=\"")
                            .append(tipS)
                            .append("\">");
                    parts.append("<span class=\"tree-step-head\">");
                    parts.append("<span class=\"tree-step-idx\" aria-hidden=\"true\">").append(j + 1).append(".</span>");
                    parts.append("<span class=\"tree-step-name\">").append(escapeHtml(label)).append("</span>");
                    parts.append("</span>");
                    if (!desc.isEmpty()) {
                        parts.append("<span class=\"tree-step-desc\">").append(escapeHtml(desc)).append("</span>");
                    }
                    parts.append("</a></li>");
                }
                parts.append("</ul></details>");
            }
            parts.append("</li>");
        }
        parts.append("</ul>");
        parts.append("<p class=\"tree-search-empty\" hidden>No scenarios match.</p>");
        parts.append("</nav>");
        return parts.toString();
    }

    private static List<ScenarioRow> attachStepsToRows(String fullHtml, List<ScenarioRow> rows) {
        if (rows.isEmpty()) {
            return rows;
        }
        List<Integer> starts = new ArrayList<>();
        List<Integer> ends = new ArrayList<>();
        List<String> aids = new ArrayList<>();
        Matcher hm = H3_ANCHOR.matcher(fullHtml);
        while (hm.find()) {
            aids.add(hm.group(1));
            starts.add(hm.start());
            ends.add(hm.end());
        }
        if (aids.isEmpty()) {
            for (ScenarioRow r : rows) {
                r.steps = List.of();
            }
            return rows;
        }
        Map<String, String> segs = new LinkedHashMap<>();
        for (int i = 0; i < aids.size(); i++) {
            String aid = aids.get(i);
            int segEnd = i + 1 < aids.size() ? starts.get(i + 1) : fullHtml.length();
            segs.put(aid, fullHtml.substring(ends.get(i), segEnd));
        }
        for (ScenarioRow r : rows) {
            String chunk = segs.getOrDefault(r.anchor, "");
            // RC#4 FIX: Replace STEP_CARD regex (DOTALL + nested .*? caused catastrophic
            // backtracking on large scenario chunks with many HTTP payloads) with a linear
            // character-level scanner that tracks div depth. O(n), no backtracking.
            r.steps = extractStepEntriesLinear(chunk, r.anchor);
        }
        return rows;
    }

    /**
     * Linear O(n) extractor: locates step-capture-card divs whose id starts with
     * {@code anchorPrefix + "-"}, then extracts label/desc using depth-tracked div
     * traversal. No regex backtracking regardless of chunk size.
     */
    private static List<StepEntry> extractStepEntriesLinear(String chunk, String anchorPrefix) {
        List<StepEntry> out = new ArrayList<>();
        if (chunk == null || chunk.isEmpty()) {
            return out;
        }
        String prefix = anchorPrefix + "-";
        int pos = 0;
        while (pos < chunk.length()) {
            int divOpen = indexOfIgnoreCase(chunk, "<div", pos);
            if (divOpen < 0) {
                break;
            }
            int divGt = chunk.indexOf('>', divOpen);
            if (divGt < 0) {
                break;
            }
            String openTag = chunk.substring(divOpen, divGt + 1);
            pos = divGt + 1;
            if (!containsIgnoreCase(openTag, "step-capture-card")) {
                continue;
            }
            String sid = extractAttrValue(openTag, "id");
            if (sid == null || !sid.startsWith(prefix)) {
                continue;
            }
            int cardBodyEnd = findBalancedDivCloseIndex(chunk, divGt + 1);
            if (cardBodyEnd < 0) {
                continue;
            }
            String cardBody = chunk.substring(divGt + 1, cardBodyEnd);
            String raw = extractTextareaBody(cardBody, "step-capture-raw");
            if (raw == null || raw.isBlank()) {
                String panelHtml = extractDivBody(cardBody, "step-capture-panel");
                raw = parseStepCapturePanelHtml(panelHtml == null ? "" : panelHtml);
            } else {
                raw = decodeMinimalHtmlEntities(raw);
            }
            String[] parsed = parseStepCaptureText(raw);
            out.add(new StepEntry(sid, parsed[0], parsed[1]));
            pos = cardBodyEnd + 6; // skip past </div>
        }
        return out;
    }

    /**
     * Depth-tracking search for the balanced {@code </div>} that closes the div
     * whose opening tag ends just before {@code from}.
     *
     * <p><b>Textarea-safe</b>: skips {@code <textarea>} bodies so that literal
     * {@code </div>} strings inside HTTP response payloads do not corrupt the
     * depth counter and cause step-card body extraction to terminate early.
     */
    private static int findBalancedDivCloseIndex(String html, int from) {
        int depth = 1;
        int pos = from;
        while (depth > 0 && pos < html.length()) {
            int nextOpen    = indexOfIgnoreCase(html, "<div",      pos);
            int nextClose   = indexOfIgnoreCase(html, "</div>",    pos);
            int nextTextarea = indexOfIgnoreCase(html, "<textarea", pos);

            if (nextClose < 0) {
                return -1;
            }
            int divMin = nextOpen >= 0 ? Math.min(nextOpen, nextClose) : nextClose;
            if (nextTextarea >= 0 && nextTextarea < divMin) {
                int taGt    = html.indexOf('>', nextTextarea);
                int taClose = taGt >= 0 ? indexOfIgnoreCase(html, "</textarea>", taGt + 1) : -1;
                pos = taClose >= 0 ? taClose + 11 : html.length();
                continue;
            }
            if (nextOpen >= 0 && nextOpen < nextClose) {
                depth++;
                pos = nextOpen + 4;
            } else {
                depth--;
                if (depth == 0) {
                    return nextClose;
                }
                pos = nextClose + 6;
            }
        }
        return -1;
    }

    /** Extracts {@code attrName="value"} or {@code attrName='value'} from an HTML open tag string. */
    private static String extractAttrValue(String tag, String attrName) {
        int i = indexOfIgnoreCase(tag, attrName, 0);
        if (i < 0) {
            return null;
        }
        int eq = tag.indexOf('=', i + attrName.length());
        if (eq < 0 || eq >= tag.length() - 1) {
            return null;
        }
        int start = eq + 1;
        while (start < tag.length() && Character.isWhitespace(tag.charAt(start))) {
            start++;
        }
        if (start >= tag.length()) {
            return null;
        }
        char quote = tag.charAt(start);
        if (quote != '"' && quote != '\'') {
            return null;
        }
        int end = tag.indexOf(quote, start + 1);
        if (end < 0) {
            return null;
        }
        return tag.substring(start + 1, end);
    }

    /** Extracts the inner HTML of the first {@code <div>} whose opening tag contains {@code cssClass}. */
    private static String extractDivBody(String html, String cssClass) {
        int pos = 0;
        while (pos < html.length()) {
            int divOpen = indexOfIgnoreCase(html, "<div", pos);
            if (divOpen < 0) {
                return null;
            }
            int divGt = html.indexOf('>', divOpen);
            if (divGt < 0) {
                return null;
            }
            String openTag = html.substring(divOpen, divGt + 1);
            pos = divGt + 1;
            if (!containsIgnoreCase(openTag, cssClass)) {
                continue;
            }
            int bodyEnd = findBalancedDivCloseIndex(html, divGt + 1);
            if (bodyEnd < 0) {
                return null;
            }
            return html.substring(divGt + 1, bodyEnd);
        }
        return null;
    }

    /** Extracts content of first {@code <textarea>} whose opening tag contains {@code attrHint}. */
    private static String extractTextareaBody(String html, String attrHint) {
        int pos = 0;
        while (pos < html.length()) {
            int tOpen = indexOfIgnoreCase(html, "<textarea", pos);
            if (tOpen < 0) {
                return null;
            }
            int tGt = html.indexOf('>', tOpen);
            if (tGt < 0) {
                return null;
            }
            String openTag = html.substring(tOpen, tGt + 1);
            pos = tGt + 1;
            if (!containsIgnoreCase(openTag, attrHint)) {
                continue;
            }
            int closeIdx = indexOfIgnoreCase(html, "</textarea>", tGt + 1);
            if (closeIdx < 0) {
                return null;
            }
            return html.substring(tGt + 1, closeIdx);
        }
        return null;
    }


    private static String[] parseStepCaptureFields(String raw) {
        String name = "Step";
        List<String> descParts = new ArrayList<>();
        String params = "";
        boolean inDesc = false;
        String pendingLabel = null;
        for (String rawLine : raw.split("\\R")) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            String low = line.toLowerCase();
            if (low.startsWith("step name:")) {
                inDesc = false;
                pendingLabel = null;
                int idx = line.indexOf(':');
                name = idx >= 0 ? line.substring(idx + 1).trim() : name;
            } else if (low.equals("step name")) {
                inDesc = false;
                pendingLabel = "name";
            } else if (low.startsWith("step description:")) {
                inDesc = true;
                pendingLabel = null;
                int idx = line.indexOf(':');
                String rest = idx >= 0 ? line.substring(idx + 1).trim() : "";
                if (!rest.isEmpty()) {
                    descParts.add(rest);
                }
            } else if (low.equals("step description")) {
                inDesc = false;
                pendingLabel = "description";
            } else if (low.startsWith("step parameters:") || low.startsWith("step parameter:")) {
                inDesc = false;
                pendingLabel = null;
                int idx = line.indexOf(':');
                params = idx >= 0 ? line.substring(idx + 1).trim() : "";
            } else if (low.equals("step parameters") || low.equals("step parameter")) {
                inDesc = false;
                pendingLabel = "parameters";
            } else if ("name".equals(pendingLabel)) {
                name = line;
                pendingLabel = null;
            } else if ("description".equals(pendingLabel)) {
                descParts.add(line);
                pendingLabel = null;
            } else if ("parameters".equals(pendingLabel)) {
                params = line;
                pendingLabel = null;
            } else if (inDesc) {
                descParts.add(line);
            }
        }
        String desc = String.join(" ", descParts).trim().replaceAll("\\s+", " ");
        if (name.length() > 56) {
            name = name.substring(0, 53) + "...";
        }
        return new String[] {name, desc, params};
    }


    private static String[] parseStepCaptureText(String raw) {
        String[] f = parseStepCaptureFields(raw);
        return new String[] {f[0], f[1]};
    }


    private static String parseStepCapturePanelHtml(String panelHtml) {
        String name = "Step";
        List<String> descParts = new ArrayList<>();
        Matcher lm = STEP_CAPTURE_LINE.matcher(panelHtml);
        while (lm.find()) {
            String label = lm.group(1).trim().toLowerCase();
            String value = lm.group(2).trim();
            if (label.contains("name")) {
                name = value;
            } else if (label.contains("description")) {
                descParts.add(value);
            }
        }
        StringBuilder raw = new StringBuilder();
        raw.append("Step Name: ").append(name);
        if (!descParts.isEmpty()) {
            raw.append("\nStep Description: ").append(String.join(" ", descParts));
        }
        return raw.toString();
    }

    private static String annotateReportBody(String html) {
        html = annotateTcOutcomeBanners(html);
        html = annotateExecDashboard(html);
        html = annotateHttpBlocks(html);
        html = annotateValidationPanels(html);
        html = annotateStepCaptureCards(html);
        html = injectStepNavIds(html);
        return html;
    }


    /**
     * Returns true if the H3 element starting at {@code h3Start} is already inside a
     * {@code scenario-detail-card__inner} div (i.e., already wrapped by a previous run).
     * Looks back up to 120 characters before the H3 opening tag.
     */
    private static boolean isAlreadyWrappedScenarioH3(String html, int h3Start) {
        int lookFrom = Math.max(0, h3Start - 120);
        String lookback = html.substring(lookFrom, h3Start);
        return containsIgnoreCase(lookback, "scenario-detail-card__inner");
    }

    private static String wrapScenarioDetailCards(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        // Original fast-path: if NO section exists at all, this is a fresh plain report —
        // process every H3. If sections DO exist (already-enhanced or partial report), we
        // still need to process UNWRAPPED H3s (e.g. m19 in a report whose wrapping failed
        // for that scenario due to the old textarea bug). We detect per-H3 whether it is
        // already wrapped instead of bailing out for the entire document.
        final String h3Head = "<h3";
        StringBuilder out = new StringBuilder();
        int cursor = 0;
        while (cursor < html.length()) {
            int h3 = indexOfIgnoreCase(html, h3Head, cursor);
            if (h3 < 0) {
                out.append(html, cursor, html.length());
                break;
            }
            int gt = html.indexOf('>', h3);
            if (gt < 0) {
                out.append(html, cursor, html.length());
                break;
            }
            String h3Tag = html.substring(h3, gt + 1);
            int h3End = gt + 1;
            if (!containsIgnoreCase(h3Tag, "scenario-anchor")) {
                out.append(html, cursor, h3End);
                cursor = h3End;
                continue;
            }
            String anchorId = extractScenarioAnchorId(h3Tag);
            if (anchorId == null) {
                out.append(html, cursor, h3End);
                cursor = h3End;
                continue;
            }
            // Skip H3s already wrapped in a scenario-detail-card__inner div so we don't
            // double-wrap sections in partially-enhanced reports. This also makes
            // wrapScenarioDetailCards() safe to call on already-enhanced reports (via
            // patchExistingTreeViewReport) to repair the scenarios whose wrapping failed.
            if (isAlreadyWrappedScenarioH3(html, h3)) {
                out.append(html, cursor, h3End);
                cursor = h3End;
                continue;
            }
            int h3Close = indexOfIgnoreCase(html, "</h3>", h3End);
            if (h3Close < 0) {
                out.append(html, cursor, h3End);
                cursor = h3End;
                continue;
            }
            // The scenario title lives inside the H3. The result table follows </h3>.
            int pos = h3Close + 5;
            while (pos < html.length() && Character.isWhitespace(html.charAt(pos))) {
                pos++;
            }
            int tableStart = indexOfIgnoreCase(html, "<table", pos);
            if (tableStart < 0 || tableStart > pos + 24) {
                out.append(html, cursor, h3End);
                cursor = h3End;
                continue;
            }
            int tableGt = html.indexOf('>', tableStart);
            if (tableGt < 0) {
                out.append(html, cursor, h3End);
                cursor = h3End;
                continue;
            }
            String openTable = html.substring(tableStart, tableGt + 1);
            if (!tableOpensWithClassResult(openTable)) {
                out.append(html, cursor, h3End);
                cursor = h3End;
                continue;
            }
            int inner = tableGt + 1;
            int closeTableIdx = findClosingTableStartIndex(html, inner);
            if (closeTableIdx < 0) {
                out.append(html, cursor, h3End);
                cursor = h3End;
                continue;
            }
            int afterTable = closeTableIdx + 8;
            int pScan = afterTable;
            while (pScan < html.length() && Character.isWhitespace(html.charAt(pScan))) {
                pScan++;
            }
            // Search for <p class="totop"> after </table>. The original 120-char limit was
            // too tight: annotation passes (tc-outcome-banner, validation-panel, etc.) can
            // insert content between </table> and <p class="totop">, causing wrapping to
            // silently fail for those scenarios. Use a generous bound (4096) so any
            // reasonable annotation output is tolerated, while still not scanning the
            // entire document for a mis-matched <p>.
            int pOpen = indexOfIgnoreCase(html, "<p", pScan);
            if (pOpen < 0 || pOpen > pScan + 4096) {
                out.append(html, cursor, h3End);
                cursor = h3End;
                continue;
            }
            int pGt = html.indexOf('>', pOpen);
            if (pGt < 0) {
                out.append(html, cursor, h3End);
                cursor = h3End;
                continue;
            }
            String pTag = html.substring(pOpen, pGt + 1);
            if (!containsIgnoreCase(pTag, "totop")) {
                // The first <p> wasn't the totop one — scan forward for the real one.
                int searchFrom = pGt + 1;
                boolean found = false;
                while (searchFrom < Math.min(pScan + 4096, html.length())) {
                    pOpen = indexOfIgnoreCase(html, "<p", searchFrom);
                    if (pOpen < 0) break;
                    pGt = html.indexOf('>', pOpen);
                    if (pGt < 0) break;
                    pTag = html.substring(pOpen, pGt + 1);
                    if (containsIgnoreCase(pTag, "totop")) { found = true; break; }
                    searchFrom = pGt + 1;
                }
                if (!found) {
                    out.append(html, cursor, h3End);
                    cursor = h3End;
                    continue;
                }
            }
            int pClose = indexOfIgnoreCase(html, "</p>", pGt + 1);
            if (pClose < 0) {
                out.append(html, cursor, h3End);
                cursor = h3End;
                continue;
            }
            int segmentEnd = pClose + 4;
            String core = html.substring(h3, segmentEnd);
            String suiteClass = "";
            if (containsIgnoreCase(h3Tag, "scenario-anchor--before-suite")) {
                suiteClass = " scenario-detail-card--before-suite";
            } else if (containsIgnoreCase(h3Tag, "scenario-anchor--after-suite")) {
                suiteClass = " scenario-detail-card--after-suite";
            }
            String wrapped =
                    "<section class=\"scenario-detail-card"
                            + suiteClass
                            + "\" data-scenario-anchor=\""
                            + escapeHtml(anchorId)
                            + "\" aria-label=\"Scenario detail "
                            + escapeHtml(anchorId)
                            + "\">"
                            + "<div class=\"scenario-detail-card__inner\">"
                            + core
                            + "</div></section>";
            out.append(html, cursor, h3);
            out.append(wrapped);
            cursor = segmentEnd;
        }
        return out.toString();
    }

    /**
     * Moves each scenario card body (everything after its H3) into a gzip+base64
     * {@code <script type="application/gzip" class="scenario-fold-src">}. The browser keeps
     * script text as one inert string, so opening a full-run report no longer builds hundreds
     * of thousands of elements and tens of thousands of textareas up front, and the file is a
     * fraction of the size (endpoint scanners such as Sophos hold the first open of a large
     * HTML file for tens of seconds). {@link #REPORT_UX_SCRIPT} unpacks a body when its card
     * is opened. The scenario title is copied into the empty H3 so collapsed cards stay
     * readable. Run {@link ReportIntegrityValidator} on the HTML before this pass: step ids
     * are not visible afterwards.
     */
    static String deferScenarioBodies(String html) {
        if (html == null || !html.contains(SCENARIO_SECTION_OPEN)) {
            return html;
        }
        StringBuilder out = new StringBuilder(html.length() / 3);
        int cursor = 0;
        int sec = html.indexOf(SCENARIO_SECTION_OPEN);
        while (sec >= 0) {
            int next = html.indexOf(SCENARIO_SECTION_OPEN, sec + SCENARIO_SECTION_OPEN.length());
            int limit = next >= 0 ? next : html.length();
            int h3 = indexOfIgnoreCase(html, "<h3", sec);
            int h3Gt = h3 < 0 ? -1 : html.indexOf('>', h3);
            int h3Close = h3Gt < 0 ? -1 : indexOfIgnoreCase(html, "</h3>", h3Gt);
            int close = html.lastIndexOf(SCENARIO_SECTION_CLOSE, limit - SCENARIO_SECTION_CLOSE.length());
            boolean deferrable = h3 >= 0 && h3 < limit && h3Close >= 0 && close > h3Close
                    && !html.startsWith(SCENARIO_FOLD_BODY_OPEN, h3Close + 5);
            if (deferrable) {
                String body = html.substring(h3Close + 5, close);
                out.append(html, cursor, h3Close);
                if (h3Close == h3Gt + 1) {
                    Matcher tm = SCENARIO_BODY_TITLE.matcher(body);
                    tm.region(0, Math.min(body.length(), 4000));
                    if (tm.find()) {
                        out.append("<span class=\"scenario-fold-title\">").append(tm.group(1).trim()).append("</span>");
                    }
                }
                out.append("</h3>")
                        .append(SCENARIO_FOLD_BODY_OPEN)
                        .append(" hidden></div><script type=\"application/gzip\" class=\"scenario-fold-src\">")
                        .append(gzipBase64(body))
                        .append("</script>");
                cursor = close;
            }
            sec = next;
        }
        out.append(html, cursor, html.length());
        return out.toString();
    }

    private static String gzipBase64(String s) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(Math.max(64, s.length() / 6));
        try (GZIPOutputStream gz = new GZIPOutputStream(bytes)) {
            gz.write(s.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return Base64.getEncoder().encodeToString(bytes.toByteArray());
    }

    private static int indexOfIgnoreCase(String haystack, String needle, int from) {
        if (needle.isEmpty()) {
            return from;
        }
        int max = haystack.length() - needle.length();
        for (int i = Math.max(0, from); i <= max; i++) {
            if (haystack.regionMatches(true, i, needle, 0, needle.length())) {
                return i;
            }
        }
        return -1;
    }

    private static boolean containsIgnoreCase(String s, String sub) {
        return indexOfIgnoreCase(s, sub, 0) >= 0;
    }

    private static String extractScenarioAnchorId(String h3OpenTag) {
        Matcher m = Pattern.compile("(?i)\\bid\\s*=\\s*(['\"])(m\\d+)\\1").matcher(h3OpenTag);
        return m.find() ? m.group(2) : null;
    }

    private static boolean tableOpensWithClassResult(String openTableTag) {
        Matcher m = Pattern.compile("(?i)\\bclass\\s*=\\s*(['\"])([^'\"]*)\\1").matcher(openTableTag);
        while (m.find()) {
            String cls = m.group(2);
            if (Pattern.compile("(^|\\s)result(\\s|$)").matcher(cls).find()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Finds the start index of the {@code </table>} that closes the table whose
     * opening tag ends just before {@code innerStart}, tracking nesting depth.
     *
     * <p><b>Textarea-safe</b>: {@code <textarea>} content is verbatim text, not HTML.
     * HTTP response payloads stored in textareas can contain literal {@code </table>}
     * strings (e.g. an API that returns an HTML-formatted error page). Without skipping
     * textarea bodies the naive depth counter would find one of those literal strings and
     * return the wrong position, causing {@link #wrapScenarioDetailCards} to fail to find
     * {@code <p class="totop">} within the expected window — and the scenario detail card
     * would silently be left unwrapped, making the scenario invisible in the right panel.
     */
    private static int findClosingTableStartIndex(String html, int innerStart) {
        int depth = 1;
        int pos = innerStart;
        while (depth > 0 && pos < html.length()) {
            int nextOpen    = indexOfIgnoreCase(html, "<table",    pos);
            int nextClose   = indexOfIgnoreCase(html, "</table>",  pos);
            int nextTextarea = indexOfIgnoreCase(html, "<textarea", pos);

            if (nextClose < 0) {
                return -1;
            }
            // If a <textarea> starts before any table tag, its body is verbatim text —
            // skip to the matching </textarea> before continuing the depth scan.
            int tableMin = nextOpen >= 0 ? Math.min(nextOpen, nextClose) : nextClose;
            if (nextTextarea >= 0 && nextTextarea < tableMin) {
                int taGt    = html.indexOf('>', nextTextarea);
                int taClose = taGt >= 0 ? indexOfIgnoreCase(html, "</textarea>", taGt + 1) : -1;
                pos = taClose >= 0 ? taClose + 11 : html.length();
                continue;
            }
            if (nextOpen >= 0 && nextOpen < nextClose) {
                depth++;
                pos = nextOpen + 6;
            } else {
                depth--;
                if (depth == 0) {
                    return nextClose;
                }
                pos = nextClose + 8;
            }
        }
        return -1;
    }

    private static String annotateTcOutcomeBanners(String html) {
        Matcher m = TC_MARKING_BANNER.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String inner = m.group(2).trim();
            String kind = tcOutcomeKind(inner);
            String[] td = tcSplitTitleDetail(inner);
            String title = td[0];
            String detail = td[1];
            String label = tcPillLabel(kind);
            String detailHtml =
                    detail.isEmpty()
                            ? ""
                            : "<div class=\"tc-outcome-detail\">" + escapeHtml(detail) + "</div>";
            String repl =
                    "<div class=\"tc-outcome-banner tc-outcome-banner--"
                            + kind
                            + "\" role=\"status\">"
                            + "<span class=\"tc-outcome-pill\">"
                            + escapeHtml(label)
                            + "</span>"
                            + "<div class=\"tc-outcome-content\">"
                            + "<div class=\"tc-outcome-title\">"
                            + escapeHtml(title)
                            + "</div>"
                            + detailHtml
                            + "</div></div>";
            m.appendReplacement(sb, Matcher.quoteReplacement(repl));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String tcOutcomeKind(String body) {
        String low = body.toLowerCase();
        if (low.contains(" as passed")) return "pass";
        if (low.contains(" as failed")) return "fail";
        if (low.contains(" as skipped") || low.contains("skipped")) return "skip";
        if (low.contains(" as ignored") || low.contains("ignored")) return "ignore";
        return "info";
    }

    private static String tcPillLabel(String kind) {
        return switch (kind) {
            case "pass" -> "Passed";
            case "fail" -> "Failed";
            case "skip" -> "Skipped";
            case "ignore" -> "Ignored";
            default -> "Result";
        };
    }

    private static String[] tcSplitTitleDetail(String body) {
        body = body.trim();
        int i = body.indexOf(". As ");
        if (i >= 0) {
            return new String[] {body.substring(0, i + 1).trim(), body.substring(i + 5).trim()};
        }
        i = body.indexOf(" As ");
        if (i >= 0) {
            return new String[] {body.substring(0, i).trim(), body.substring(i + 4).trim()};
        }
        return new String[] {body, ""};
    }

    private static String annotateExecDashboard(String html) {
        String oldTb = "<table style='width:100%; table-layout:fixed;'>";
        if (!html.contains(oldTb)) {
            return html;
        }
        html = STATS_HEADER_ROW.matcher(html).replaceFirst("</td></tr><tr>$1");
        html = replaceFirstLiteral(html, oldTb, "<table class='report-exec-table' style='width:100%; table-layout:fixed;'>");
        html =
                replaceFirstLiteral(
                        html,
                        "<table class='report-exec-table' style='width:100%; table-layout:fixed;'><tr><th colspan='7'>",
                        "<table class='report-exec-table' style='width:100%; table-layout:fixed;'><tr class='exec-title-row'><th colspan='7'>");
        html =
                replaceFirstLiteral(
                        html,
                        "<tr style='background:#f9fbfd;' style='border-bottom:2px solid #ccc;'><td colspan='3'>",
                        "<tr class='exec-kv-row exec-kv-row--footer'><td colspan='3'>");
        html =
                html.replace(
                        "<tr style='background:#f9fbfd;'><td colspan='3'>",
                        "<tr class='exec-kv-row'><td colspan='3'>");
        html =
                html.replace(
                        "<tr><td colspan='7' style='background:#eef1f5; font-size:18px; font-weight:bold; padding:12px; border-top:2px solid #ccc; text-align:center; color:#2c3e50;'>",
                        "<tr id='report-exec-summary'><td colspan='7' class='exec-section-title' style='background:#D6EAF8; font-size:13px; font-weight:700; padding:8px 10px; border:1px solid #AED6F1; text-align:center; color:#000000; text-transform:uppercase; letter-spacing:0.06em;'>");
        html =
                replaceFirstLiteral(
                        html,
                        "<tr><td colspan='7' style='background:#e0e6ed; height:2px;'></td></tr>",
                        "<tr><td colspan='7' class='exec-separator' style='background:#AED6F1; height:2px; padding:0;'></td></tr>");
        html =
                replaceFirstLiteral(
                        html,
                        "<tr><td colspan='7' style='background:#f9fafc; padding:10px; font-size:13px; text-align:left;'>",
                        "<tr><td colspan='7' class='exec-legend' style='background:#FFFFFF; padding:10px 12px; font-size:12px; text-align:left; border:1px solid #AED6F1;'>");
        html = ensureReportExecSummaryAnchor(html);
        return reflowExecLegendCell(html);
    }


    private static String ensureReportExecSummaryAnchor(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        if (html.contains("id=\"report-exec-summary\"") || html.contains("id='report-exec-summary'")) {
            return html;
        }
        Matcher m =
                Pattern.compile(
                                "(?is)(<tr)(\\s*)(>\\s*<td[^>]*>\\s*Summary of Test Results\\s*</td>\\s*</tr>)")
                        .matcher(html);
        if (m.find()) {
            return m.replaceFirst("$1 id=\"report-exec-summary\"$2$3");
        }
        return html;
    }

    private static String reflowExecLegendCell(String html) {
        Matcher m = EXEC_LEGEND_TD.matcher(html);
        if (!m.find()) {
            return html;
        }
        String openTd = m.group(1);
        String inner = m.group(2);
        String closeTd = m.group(3);
        String[] parts = LEGEND_BR.split(inner);
        List<String> cleaned = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) cleaned.add(t);
        }
        if (cleaned.size() < 2) {
            return html;
        }
        StringBuilder chunks = new StringBuilder();
        chunks.append("<div class=\"exec-legend-inner\">");
        chunks.append("<div class=\"exec-legend-title\">").append(cleaned.get(0)).append("</div>");
        chunks.append("<ul class=\"exec-legend-list\">");
        for (int i = 1; i < cleaned.size(); i++) {
            String seg = cleaned.get(i).trim();
            Matcher ic = LEGEND_ICON.matcher(seg);
            if (ic.matches()) {
                String ico = ic.group(1);
                String txt = ic.group(2).replaceFirst("<b>\\s+", "<b>");
                chunks.append(
                        "<li class=\"exec-legend-item\"><span class=\"exec-legend-ico\" aria-hidden=\"true\">");
                chunks.append(ico).append("</span><span class=\"exec-legend-txt\">").append(txt).append("</span></li>");
            } else {
                chunks.append("<li class=\"exec-legend-item exec-legend-item--plain\"><span class=\"exec-legend-txt\">");
                chunks.append(seg).append("</span></li>");
            }
        }
        chunks.append("</ul></div>");
        String rebuilt = openTd + chunks + closeTd;
        return m.replaceFirst(Matcher.quoteReplacement(rebuilt));
    }

    private static String annotateValidationPanels(String html) {
        Matcher m = VALIDATION_PANEL.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String inner = m.group(1);
            String repl =
                    "<div class=\"validation-panel\"><div class=\"validation-heading\">"
                            + "<span class=\"val-h-icon\" aria-hidden=\"true\">&#9889;</span>"
                            + "<span class=\"val-h-title\">Output validation</span>"
                            + "<span class=\"val-h-pills\">"
                            + "<span class=\"val-pill val-pill-exp\">Expected</span>"
                            + "<span class=\"val-h-vs\">vs</span>"
                            + "<span class=\"val-pill val-pill-act\">Actual</span>"
                            + "</span></div><div class='validation-table-wrap'>"
                            + "<table class='validation-table' width='100%' charset='UTF8'>"
                            + inner
                            + "</table></div></div>";
            m.appendReplacement(sb, Matcher.quoteReplacement(repl));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String annotateStepCaptureCards(String html) {
        Matcher m = STEP_CAPTURE_LEGACY_BLOCK.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String raw = m.group(1) == null ? "" : m.group(1);
            String repl = buildStepCaptureCardMarkup(raw);
            m.appendReplacement(sb, Matcher.quoteReplacement(repl));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static void appendStepCaptureLine(StringBuilder b, String label, String value, boolean mono) {
        String valueClass = mono ? " step-capture-value--mono" : "";
        b.append("<p class='step-capture-line'>");
        b.append("<span class='step-capture-label'>").append(escapeHtml(label)).append("</span>");
        b.append(" : ");
        b.append("<span class='step-capture-value").append(valueClass).append("'>");
        b.append(escapeHtml(value));
        b.append("</span></p>");
    }


    private static String buildStepCaptureCardMarkup(String rawContent) {
        String[] f = parseStepCaptureFields(rawContent);
        String name = f[0];
        String desc = f[1];
        String params = f[2];
        StringBuilder b = new StringBuilder();
        b.append("<div class='step-capture-card'>");
        b.append("<div class='step-capture-banner'>STEP · <strong>")
                .append(escapeHtml(name))
                .append("</strong></div>");
        b.append("<div class='step-capture-panel'>");
        appendStepCaptureLine(b, "Step name", name, false);
        appendStepCaptureLine(b, "Step description", desc, false);
        appendStepCaptureLine(b, "Step parameters", params, true);
        b.append("</div></div>");
        return b.toString();
    }

    private static String injectStepNavIds(String html) {
        List<Integer> starts = new ArrayList<>();
        List<Integer> ends = new ArrayList<>();
        List<String> aids = new ArrayList<>();
        Matcher hm = H3_ANCHOR.matcher(html);
        while (hm.find()) {
            aids.add(hm.group(1));
            starts.add(hm.start());
            ends.add(hm.end());
        }
        if (aids.isEmpty()) {
            return html;
        }
        StringBuilder out = new StringBuilder();
        int last = 0;
        for (int i = 0; i < aids.size(); i++) {
            int s = starts.get(i);
            int e = ends.get(i);
            out.append(html, last, s);
            out.append(html, s, e);
            int segEnd = i + 1 < aids.size() ? starts.get(i + 1) : html.length();
            String chunk = html.substring(e, segEnd);
            String aid = aids.get(i);
            int counter = 0;
            Matcher dm = STEP_CAPTURE_DIV.matcher(chunk);
            StringBuffer cbuf = new StringBuffer();
            while (dm.find()) {
                String repl = "<div class='step-capture-card' id='" + aid + "-s" + counter++ + "'>";
                dm.appendReplacement(cbuf, Matcher.quoteReplacement(repl));
            }
            dm.appendTail(cbuf);
            out.append(cbuf);
            last = segEnd;
        }
        out.append(html, last, html.length());
        return out.toString();
    }

    private static String annotateHttpBlocks(String html) {
        html =
                HTTP_REQ.matcher(html)
                        .replaceAll(
                                "<div class=\"http-callline http-callline-req\">"
                                        + "<span class=\"http-badge http-badge-req\">Request</span>"
                                        + "<span class=\"http-endpoint-chip\"><code class=\"http-endpoint\">$1</code></span></div>"
                                        + "<pre class=\"http-payload http-payload-request\"><div class=\"http-field http-headers-field\">"
                                        + "<textarea class='http-ta http-headers-ta' style='border: solid 1px gray");
        html =
                HTTP_RESP.matcher(html)
                        .replaceAll(
                                "<div class=\"http-callline http-callline-res\">"
                                        + "<span class=\"http-badge http-badge-res\">Response</span></div>"
                                        + "<pre class=\"http-payload http-payload-response\"><div class=\"http-field http-headers-field\">"
                                        + "<textarea class='http-ta http-headers-ta' style='border: solid 1px gray");
        String msgOld =
                "<div style='width: 100%; overflow: hidden;'><textarea style='border:solid 1px black; width: 100%; "
                        + "box-sizing: border-box;' name='message' rows='6' readonly='true'>";
        String msgNew =
                "<div class='http-field http-body-field'><textarea class='http-ta http-body-ta' style='border:solid 1px black; width: 100%; "
                        + "box-sizing: border-box;' name='message' rows='6' readonly='true'>";
        html = html.replace(msgOld, msgNew);

        html =
                html.replace(
                        "<div style='width:100%; overflow: hidden;'><textarea style='border:solid 1px black; width: 100%; "
                                + "box-sizing: border-box;' name='message' rows='6' readonly='true'>",
                        msgNew);
        return html;
    }

    static final class ScenarioRow {
        final String clazz;
        final String anchor;
        final String name;
        final String desc;
        final String timing;
        final String status;

        List<StepEntry> steps;

        ScenarioRow(String clazz, String anchor, String name, String desc, String timing, String status) {
            this.clazz = clazz;
            this.anchor = anchor;
            this.name = name;
            this.desc = desc;
            this.timing = timing;
            this.status = status;
        }
    }

    static final class StepEntry {
        final String id;
        final String label;
        final String desc;

        StepEntry(String id, String label, String desc) {
            this.id = id;
            this.label = label;
            this.desc = desc;
        }
    }
}
