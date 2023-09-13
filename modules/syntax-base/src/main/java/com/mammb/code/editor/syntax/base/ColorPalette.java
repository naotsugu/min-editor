/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.editor.syntax.base;

/**
 * ColorPalette.
 * @author Naotsugu Kobayashi
 */
public class ColorPalette {

    /** The base color string. */
    private String base;

    /** The base luminance(0:black - 100:white). */
    private double luminance;


    /**
     * Constructor.
     * @param baseRgb the base rgb
     */
    public ColorPalette(String baseRgb) {
        base = (baseRgb == null || baseRgb.isEmpty())
            ? "000000" : baseRgb.startsWith("#")
            ? baseRgb.substring(1) : baseRgb;
        luminance = luminance(base);
    }

    public String red() { return on(Hue.RED); }
    public String pink() { return on(Hue.PINK); }
    public String purple() { return on(Hue.PURPLE); }
    public String deepPurple() { return on(Hue.DEEP_PURPLE); }
    public String indigo() { return on(Hue.INDIGO); }
    public String blue() { return on(Hue.BLUE); }
    public String lightBlue() { return on(Hue.LIGHT_BLUE); }
    public String cyan() { return on(Hue.CYAN); }
    public String teal() { return on(Hue.TEAL); }
    public String green() { return on(Hue.GREEN); }
    public String lightGreen() { return on(Hue.LIGHT_GREEN); }
    public String lime() { return on(Hue.LIME); }
    public String yellow() { return on(Hue.YELLOW); }
    public String amber() { return on(Hue.AMBER); }
    public String orange() { return on(Hue.ORANGE); }
    public String deepOrange() { return on(Hue.DEEP_ORANGE); }
    public String brown() { return on(Hue.BROWN); }
    public String grey() { return on(Hue.GREY); }
    public String blueGrey() { return on(Hue.BLUE_GREY); }

    public String on(Hue hue) {
        if (!hue.isColored()) {
            return "";
        }
        String selected = "";
        double distance = 100;
        for (String rgb : colors[hue.ordinal()]) {
            double d = Math.abs(luminance - (luminance(rgb) * 1.16));
            if (d < distance) {
                selected = '#' + rgb;
                distance = d;
            }
        }
        return selected;
    }

    public String getBase() {
        return base;
    }

    public double getLuminance() {
        return luminance;
    }

    private static final String[][] colors;
    static {
        colors = new String[Hue.values().length][10];
        colors[Hue.RED.ordinal()]         = new String[] { "FFEBEE", "FFCDD2", "EF9A9A", "E57373", "EF5350", "F44336", "E53935", "D32F2F", "C62828", "B71C1C" };
        colors[Hue.PINK.ordinal()]        = new String[] { "FCE4EC", "F8BBD0", "F48FB1", "F06292", "EC407A", "E91E63", "D81B60", "C2185B", "AD1457", "880E4F" };
        colors[Hue.PURPLE.ordinal()]      = new String[] { "F3E5F5", "E1BEE7", "CE93D8", "BA68C8", "AB47BC", "9C27B0", "8E24AA", "7B1FA2", "6A1B9A", "4A148C" };
        colors[Hue.DEEP_PURPLE.ordinal()] = new String[] { "EDE7F6", "D1C4E9", "B39DDB", "9575CD", "7E57C2", "673AB7", "5E35B1", "512DA8", "4527A0", "311B92" };
        colors[Hue.INDIGO.ordinal()]      = new String[] { "E8EAF6", "C5CAE9", "9FA8DA", "7986CB", "5C6BC0", "3F51B5", "3949AB", "303F9F", "283593", "1A237E" };
        colors[Hue.BLUE.ordinal()]        = new String[] { "E3F2FD", "BBDEFB", "90CAF9", "64B5F6", "42A5F5", "2196F3", "1E88E5", "1976D2", "1565C0", "0D47A1" };
        colors[Hue.LIGHT_BLUE.ordinal()]  = new String[] { "E1F5FE", "B3E5FC", "81D4FA", "4FC3F7", "29B6F6", "03A9F4", "039BE5", "0288D1", "0277BD", "01579B" };
        colors[Hue.CYAN.ordinal()]        = new String[] { "E0F7FA", "B2EBF2", "80DEEA", "4DD0E1", "26C6DA", "00BCD4", "00ACC1", "0097A7", "00838F", "006064" };
        colors[Hue.TEAL.ordinal()]        = new String[] { "E0F2F1", "B2DFDB", "80CBC4", "4DB6AC", "26A69A", "009688", "00897B", "00796B", "00695C", "004D40" };
        colors[Hue.GREEN.ordinal()]       = new String[] { "E8F5E9", "C8E6C9", "A5D6A7", "81C784", "66BB6A", "4CAF50", "43A047", "388E3C", "2E7D32", "1B5E20" };
        colors[Hue.LIGHT_GREEN.ordinal()] = new String[] { "F1F8E9", "DCEDC8", "C5E1A5", "AED581", "9CCC65", "8BC34A", "7CB342", "689F38", "558B2F", "33691E" };
        colors[Hue.LIME.ordinal()]        = new String[] { "F9FBE7", "F0F4C3", "E6EE9C", "DCE775", "D4E157", "CDDC39", "C0CA33", "AFB42B", "9E9D24", "9E9D24" };
        colors[Hue.YELLOW.ordinal()]      = new String[] { "FFFDE7", "FFF9C4", "FFF59D", "FFF176", "FFEE58", "FFEB3B", "FDD835", "FBC02D", "F9A825", "F57F17" };
        colors[Hue.AMBER.ordinal()]       = new String[] { "FFF8E1", "FFECB3", "FFE082", "FFD54F", "FFCA28", "FFC107", "FFB300", "FFA000", "FF8F00", "FF6F00" };
        colors[Hue.ORANGE.ordinal()]      = new String[] { "FFF3E0", "FFE0B2", "FFCC80", "FFB74D", "FFA726", "FF9800", "FB8C00", "F57C00", "EF6C00", "EF6C00" };
        colors[Hue.DEEP_ORANGE.ordinal()] = new String[] { "FBE9E7", "FFCCBC", "FFAB91", "FF8A65", "FF7043", "FF5722", "F4511E", "E64A19", "D84315", "BF360C" };
        colors[Hue.BROWN.ordinal()]       = new String[] { "EFEBE9", "D7CCC8", "BCAAA4", "A1887F", "8D6E63", "795548", "6D4C41", "5D4037", "4E342E", "3E2723" };
        colors[Hue.GREY.ordinal()]        = new String[] { "FAFAFA", "F5F5F5", "EEEEEE", "E0E0E0", "BDBDBD", "9E9E9E", "757575", "616161", "424242", "212121" };
        colors[Hue.BLUE_GREY.ordinal()]   = new String[] { "ECEFF1", "CFD8DC", "B0BEC5", "90A4AE", "78909C", "607D8B", "546E7A", "455A64", "37474F", "263238" };
        colors[Hue.NONE.ordinal()]        = new String[] { };
    }

                                               // 100       200       400       700
    private static final String[] redA        = { "FF8A80", "FF5252", "FF1744", "D50000" };
    private static final String[] pinkA       = { "FF80AB", "FF4081", "F50057", "C51162" };
    private static final String[] purpleA     = { "EA80FC", "E040FB", "D500F9", "AA00FF" };
    private static final String[] deepPurpleA = { "B388FF", "7C4DFF", "651FFF", "6200EA" };
    private static final String[] indigoA     = { "8C9EFF", "536DFE", "3D5AFE", "304FFE" };
    private static final String[] blueA       = { "82B1FF", "448AFF", "2979FF", "2962FF" };
    private static final String[] lightBlueA  = { "80D8FF", "40C4FF", "00B0FF", "0091EA" };
    private static final String[] cyanA       = { "84FFFF", "18FFFF", "00E5FF", "00B8D4" };
    private static final String[] tealA       = { "A7FFEB", "64FFDA", "1DE9B6", "00BFA5" };
    private static final String[] greenA      = { "B9F6CA", "69F0AE", "00E676", "00C853" };
    private static final String[] lightGreenA = { "CCFF90", "B2FF59", "76FF03", "64DD17" };
    private static final String[] limeA       = { "F4FF81", "F4FF81", "C6FF00", "AEEA00" };
    private static final String[] yellowA     = { "FFFF8D", "FFFF00", "FFEA00", "FFD600" };
    private static final String[] amberA      = { "FFE57F", "FFD740", "FFC400", "FFAB00" };
    private static final String[] orangeA     = { "FFD180", "FFAB40", "FF9100", "FF6D00" };
    private static final String[] deepOrangeA = { "FF9E80", "FF6E40", "FF3D00", "DD2C00" };


    private int index() {
        long n = Math.round((100 - luminance) / 10 + 3);
        return Math.min(9, (int) n);
    }

    private double luminance(String sRGB) {
        double vR = Integer.parseInt(sRGB.substring(0, 2), 16) / 255.0;
        double vG = Integer.parseInt(sRGB.substring(2, 4), 16) / 255.0;
        double vB = Integer.parseInt(sRGB.substring(4, 6), 16) / 255.0;
        return yToL(0.2126 * sRGBtoLin(vR) + 0.7152 * sRGBtoLin(vG) + 0.0722 * sRGBtoLin(vB));
    }

    private double sRGBtoLin(double colorChannel) {
        return (colorChannel <= 0.04045)
            ? colorChannel / 12.92
            : Math.pow((colorChannel + 0.055) / 1.055, 2.4);
    }

    private double yToL(double Y) {
        return (Y <= (216.0 / 24389.0))
            ? Y * (24389.0 / 27.0)
            : Math.pow(Y,(1.0 / 3.0)) * 116 - 16;
    }

}
