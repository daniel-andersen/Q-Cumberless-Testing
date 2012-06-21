// Copyright (c) 2012, Daniel Andersen (dani_ande@yahoo.dk)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
// 3. The name of the author may not be used to endorse or promote products derived
//    from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.trollsahead.qcumberless.plugins.pdfreport;

import com.trollsahead.qcumberless.engine.FlashingMessageManager;
import com.trollsahead.qcumberless.gui.FlashingMessage;
import com.trollsahead.qcumberless.gui.elements.BaseBarElement;
import com.trollsahead.qcumberless.gui.elements.Element;
import com.trollsahead.qcumberless.gui.elements.FeatureElement;
import com.trollsahead.qcumberless.model.PlayResult;
import com.trollsahead.qcumberless.plugins.HistoryPlugin;
import com.trollsahead.qcumberless.util.FileUtil;
import com.trollsahead.qcumberless.util.Util;
import gnu.jpdf.PDFJob;

import javax.imageio.ImageIO;

import static com.trollsahead.qcumberless.gui.Images.ThumbnailState;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

public class PdfReportPlugin implements HistoryPlugin {
    private static final Color COLOR_HEADER = new Color(0.9f, 0.9f, 1.0f);
    private static final Color COLOR_FOOTER = Color.WHITE;

    private static final Color COLOR_SUCCESS = new Color(0.0f, 0.5f, 0.0f);
    private static final Color COLOR_FAILED = new Color(0.5f, 0.0f, 0.0f);
    private static final Color COLOR_NOT_PLAYED = new Color(0.5f, 0.5f, 0.5f);

    private static final int HEADER_HEIGHT = 24;
    private static final int FOOTER_HEIGHT = 24;

    private static final int BORDER_SIZE = 32;

    private static BufferedImage thumbnailNormal;
    private static BufferedImage thumbnailHighlight;
    private static BufferedImage thumbnailPressed;

    private static Font FONT_TEXT = new Font("TimesRoman", Font.PLAIN, 12);
    private static Font FONT_HEADER = new Font("TimesRoman", Font.PLAIN, 10);
    private static Font FONT_TITLE = new Font("Helvetica", Font.PLAIN, 14);
    private static Font FONT_FEATURE = new Font("Helvetica", Font.PLAIN, 12);

    private int currentY;
    private boolean isPageEmpty;

    private Date date;
    private String runTags;

    static {
        try {
            thumbnailNormal = ImageIO.read(PdfReportPlugin.class.getResource("/resources/pictures/pdf_report_normal.png"));
            thumbnailHighlight = ImageIO.read(PdfReportPlugin.class.getResource("/resources/pictures/pdf_report_highlight.png"));
            thumbnailPressed = ImageIO.read(PdfReportPlugin.class.getResource("/resources/pictures/pdf_report_pressed.png"));
        } catch (Exception e) {
            throw new RuntimeException("PDF Report-plugin refused to start", e);
        }
    }

    public void initialize() {
        File file = new File("reports");
        file.mkdir();
    }

    public void trigger(List<FeatureElement> features, Date date, String runTags) {
        this.date = date;
        this.runTags = runTags;
        String filename = "reports/report_" + FileUtil.prettyFilenameDateAndTime(date) + ".pdf";
        if (generatePdf(filename, features)) {
            FlashingMessageManager.addMessage(new FlashingMessage("Report saved to: " + filename, 5000));
        } else {
            FlashingMessageManager.addMessage(new FlashingMessage("Could not generate report! See log for defails.", 5000));
        }
    }

    private boolean generatePdf(String filename, List<FeatureElement> features) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(filename));
            PDFJob pdfJob = new PDFJob(out);
            writePages(pdfJob, features);
            pdfJob.end();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            FileUtil.close(out);
        }
        return true;
    }

    private void writePages(PDFJob pdfJob, List<FeatureElement> features) {
        Graphics g = newPage(pdfJob);

        isPageEmpty = false;

        g = appendSummary(g, pdfJob, features);
        g = appendScenarios(g, pdfJob, features);

        g.dispose();
    }

    private Graphics newPage(PDFJob pdfJob) {
        Graphics g = pdfJob.getGraphics();

        prependHeader(g, pdfJob, date);
        prependFooter(pdfJob, g, pdfJob.getPageDimension());

        currentY = HEADER_HEIGHT + BORDER_SIZE + g.getFontMetrics().getHeight();

        return g;
    }

    private Graphics appendScenarios(Graphics g, PDFJob pdfJob, List<FeatureElement> features) {
        if (!isPageEmpty) {
            g = newPage(pdfJob);
        }
        g = drawString(g, "SCENARIOS", BORDER_SIZE, FONT_TITLE, pdfJob);
        currentY += g.getFontMetrics().getHeight();

        for (FeatureElement feature : features) {
            setPlayResultColor(g, feature.getPlayResult());
            g = drawString(g, "Feature: " + Util.convertMagicNewlines(feature.getTitle()), BORDER_SIZE, FONT_FEATURE, pdfJob);
            currentY += g.getFontMetrics().getHeight();
            for (Element element : feature.children) {
                BaseBarElement scenario = (BaseBarElement) element;
                setPlayResultColor(g, scenario.getPlayResult());
                g = drawString(g, "Scenario: " + scenario.getTitle(), BORDER_SIZE + 30, pdfJob);
                if (scenario.getPlayResult().isFailed()) {
                    for (Element stepElement : scenario.children) {
                        g = drawString(g, stepElement.getTitle(), BORDER_SIZE + 60, pdfJob);
                    }
                }
            }
        }
        return g;
    }

    private Graphics appendSummary(Graphics g, PDFJob pdfJob, List<FeatureElement> features) {
        g = drawString(g, "SUMMARY", BORDER_SIZE, FONT_TITLE, pdfJob);
        currentY += g.getFontMetrics().getHeight();

        g = drawString(g, "Run date: " + Util.prettyDate(date), BORDER_SIZE, pdfJob);
        currentY += g.getFontMetrics().getHeight();

        g = drawString(g, "Run-tags: " + (Util.isEmpty(runTags) ? "" : runTags), BORDER_SIZE, pdfJob);
        currentY += g.getFontMetrics().getHeight();

        int scenarioCountSuccess = countScenariosInState(features, PlayResult.State.SUCCESS);
        int scenarioCountFailed = countScenariosInState(features, PlayResult.State.FAILED);
        int scenarioCountNotPlayed = countScenariosInState(features, PlayResult.State.NOT_PLAYED);
        g = drawString(g, scenarioCountSuccess + " scenarios succeeded", BORDER_SIZE, pdfJob);
        g = drawString(g, scenarioCountFailed + " scenarios failed", BORDER_SIZE, pdfJob);
        g = drawString(g, scenarioCountNotPlayed + " scenarios did not run", BORDER_SIZE, pdfJob);
        currentY += g.getFontMetrics().getHeight();

        int featureCountSuccess = countFeaturesInState(features, PlayResult.State.SUCCESS);
        int featureCountFailed = countFeaturesInState(features, PlayResult.State.FAILED);
        int featureCountNotPlayed = countFeaturesInState(features, PlayResult.State.NOT_PLAYED);
        g = drawString(g, featureCountSuccess + " features succeeded", BORDER_SIZE, pdfJob);
        g = drawString(g, featureCountFailed + " features failed", BORDER_SIZE, pdfJob);
        g = drawString(g, featureCountNotPlayed + " features did not run", BORDER_SIZE, pdfJob);
        currentY += g.getFontMetrics().getHeight();

        if (scenarioCountFailed > 0) {
            currentY += g.getFontMetrics().getHeight();
            drawString(g, "FAILED SCENARIOS", BORDER_SIZE, FONT_TITLE, pdfJob);
            currentY += g.getFontMetrics().getHeight();
            g.setColor(COLOR_FAILED);
            for (FeatureElement feature : features) {
                for (Element scenario : feature.children) {
                    if (((BaseBarElement) scenario).getPlayResult().isFailed()) {
                        String s = "Scenario: " + scenario.getTitle();
                        g = drawString(g, s, BORDER_SIZE, pdfJob);
                    }
                }
            }
        }
        return g;
    }

    private void prependHeader(Graphics g, PDFJob pdfJob, Date date) {
        Dimension dimension = pdfJob.getPageDimension();

        g.setFont(FONT_HEADER);

        g.setColor(COLOR_HEADER);
        g.fillRect(0, 0, dimension.width, HEADER_HEIGHT);

        int y = g.getFontMetrics().getHeight() + 2;

        String s = Util.prettyDate(date);

        g.setColor(Color.BLACK);
        g.drawString(s, dimension.width - g.getFontMetrics().stringWidth(s) - 4, y);
        g.drawString("Q-Cumberless Testing", BORDER_SIZE / 2, y);
    }

    private void prependFooter(PDFJob pdfJob, Graphics g, Dimension dimension) {
        g.setFont(FONT_HEADER);

        g.setColor(COLOR_FOOTER);
        g.fillRect(0, dimension.height - FOOTER_HEIGHT, dimension.width, FOOTER_HEIGHT);

        String pageNumber = String.valueOf(pdfJob.getCurrentPageNumber());

        int y = dimension.height - g.getFontMetrics().getHeight() + 2;

        g.setColor(Color.BLACK);
        g.drawString(pageNumber, (dimension.width - g.getFontMetrics().stringWidth(pageNumber)) / 2, y);
    }

    private Graphics drawString(Graphics g, String s, int x, PDFJob pdfJob) {
        return drawString(g, s, x, FONT_TEXT, pdfJob);
    }

    private Graphics drawString(Graphics g, String s, int x, Font font, PDFJob pdfJob) {
        Dimension dimension = pdfJob.getPageDimension();

        g.setFont(font);
        List<String> wrappedText = Util.wrapText(s, dimension.width - BORDER_SIZE - x, g.getFontMetrics());
        for (String line : wrappedText) {
            g.drawString(line, x, currentY);
            currentY += g.getFontMetrics().getHeight();
            isPageEmpty = false;
            if (currentY >= dimension.height) {
                g = newPage(pdfJob);
                isPageEmpty = true;
            }
        }
        return g;
    }

    private void setPlayResultColor(Graphics g, PlayResult playResult) {
        if (playResult.isSuccess()) {
            g.setColor(COLOR_SUCCESS);
        } else if (playResult.isFailed()) {
            g.setColor(COLOR_FAILED);
        } else {
            g.setColor(COLOR_NOT_PLAYED);
        }
    }

    private int countScenariosInState(List<FeatureElement> features, PlayResult.State state) {
        int count = 0;
        for (FeatureElement feature : features) {
            for (Element scenario : feature.children) {
                if (((BaseBarElement) scenario).getPlayResult().getState() == state) {
                    count++;
                }
            }
        }
        return count;
    }

    private int countFeaturesInState(List<FeatureElement> features, PlayResult.State state) {
        int count = 0;
        for (FeatureElement feature : features) {
            if (feature.getPlayResult().getState() == state) {
                count++;
            }
        }
        return count;
    }

    public Image getThumbnail(ThumbnailState thumbnailState) {
        if (thumbnailState == ThumbnailState.HIGHLIGHTED) {
            return thumbnailHighlight;
        } else if (thumbnailState == ThumbnailState.PRESSED) {
            return thumbnailPressed;
        } else {
            return thumbnailNormal;
        }
    }

    public String getTooltip() {
        return "Export report to PDF";
    }
}

