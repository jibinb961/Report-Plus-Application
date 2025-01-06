package com.inhaler.report;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.borders.DoubleBorder;
import com.itextpdf.layout.borders.GrooveBorder;
import com.itextpdf.layout.borders.RoundDotsBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.renderer.DivRenderer;
import com.itextpdf.layout.renderer.DocumentRenderer;

import java.util.HashMap;

public class CustomDocumentRenderer extends DocumentRenderer {
    private HashMap<String,String> pdfMap = new HashMap<>();
    public CustomDocumentRenderer(Document document, HashMap<String,String> pdfsettings) {
        super(document);
        this.pdfMap = pdfsettings;

    }


    @Override
    protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
        LayoutArea area = super.updateCurrentArea(overflowResult); // margins are applied on this level
        Rectangle newBBox = area.getBBox().clone();
        String bgcolor[] = pdfMap.get("fontcolor").split(",");

        // apply border
        float[] borderWidths = {5, 0,5, 0};
        newBBox.applyMargins(borderWidths[0], borderWidths[1], borderWidths[2], borderWidths[3], false);

        if(pdfMap.get("bordertype").equals("Groove Border"))
        {
            Div div = new Div()
                    .setWidth(newBBox.getWidth())
                    .setHeight(newBBox.getHeight())
                    .setBorder(new GrooveBorder(Integer.parseInt(pdfMap.get("borderwidth"))))
                    .setBackgroundColor(new DeviceRgb(Integer.parseInt(bgcolor[0].trim()),
                            Integer.parseInt(bgcolor[1].trim()),
                            Integer.parseInt(bgcolor[2].trim())));
            addChild(new DivRenderer(div));
            // apply padding
            float[] paddingWidths = {20, 20, 20, 20};
            newBBox.applyMargins(paddingWidths[0], paddingWidths[1], paddingWidths[2], paddingWidths[3], false);

        }
        else if(pdfMap.get("bordertype").equals("Double Border"))
        {
            Div div = new Div()
                    .setWidth(newBBox.getWidth())
                    .setHeight(newBBox.getHeight())
                    .setBorder(new DoubleBorder(Integer.parseInt(pdfMap.get("borderwidth"))))
                    .setBackgroundColor(new DeviceRgb(Integer.parseInt(bgcolor[0].trim()),
                            Integer.parseInt(bgcolor[1].trim()),
                            Integer.parseInt(bgcolor[2].trim())));
            addChild(new DivRenderer(div));
            // apply padding
            float[] paddingWidths = {20, 20, 20, 20};
            newBBox.applyMargins(paddingWidths[0], paddingWidths[1], paddingWidths[2], paddingWidths[3], false);

        }
        else if(pdfMap.get("bordertype").equals("Solid Border"))
        {
            Div div = new Div()
                    .setWidth(newBBox.getWidth())
                    .setHeight(newBBox.getHeight())
                    .setBorder(new SolidBorder(Integer.parseInt(pdfMap.get("borderwidth"))))
                    .setBackgroundColor(new DeviceRgb(Integer.parseInt(bgcolor[0].trim()),
                            Integer.parseInt(bgcolor[1].trim()),
                            Integer.parseInt(bgcolor[2].trim())));
            addChild(new DivRenderer(div));
            // apply padding
            float[] paddingWidths = {20, 20, 20, 20};
            newBBox.applyMargins(paddingWidths[0], paddingWidths[1], paddingWidths[2], paddingWidths[3], false);

        }
        else if(pdfMap.get("bordertype").equals("Dotted Border"))
        {
            Div div = new Div()
                    .setWidth(newBBox.getWidth())
                    .setHeight(newBBox.getHeight())
                    .setBorder(new DottedBorder(Integer.parseInt(pdfMap.get("borderwidth"))))
                    .setBackgroundColor(new DeviceRgb(Integer.parseInt(bgcolor[0].trim()),
                            Integer.parseInt(bgcolor[1].trim()),
                            Integer.parseInt(bgcolor[2].trim())));
            addChild(new DivRenderer(div));
            // apply padding
            float[] paddingWidths = {20, 20, 20, 20};
            newBBox.applyMargins(paddingWidths[0], paddingWidths[1], paddingWidths[2], paddingWidths[3], false);

        }
        else if(pdfMap.get("bordertype").equals("Round Dots Border"))
        {
            Div div = new Div()
                    .setWidth(newBBox.getWidth())
                    .setHeight(newBBox.getHeight())
                    .setBorder(new RoundDotsBorder(Integer.parseInt(pdfMap.get("borderwidth"))))
                    .setBackgroundColor(new DeviceRgb(Integer.parseInt(bgcolor[0].trim()),
                            Integer.parseInt(bgcolor[1].trim()),
                            Integer.parseInt(bgcolor[2].trim())));
            addChild(new DivRenderer(div));
            // apply padding
            float[] paddingWidths = {20, 20, 20, 20};
            newBBox.applyMargins(paddingWidths[0], paddingWidths[1], paddingWidths[2], paddingWidths[3], false);

        }
        // this div will be added as a background

        return (currentArea = new RootLayoutArea(area.getPageNumber(), newBBox));


    }
}
