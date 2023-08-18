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
package com.mammb.code.editor2.syntax;

/**
 * ColorPalette.
 * @author Naotsugu Kobayashi
 */
public class ColorPalette {

                                               // 50        100       200       300       400       500       600       700       800       900
    private static final String[] red        = { "FFEBEE", "FFCDD2", "EF9A9A", "E57373", "EF5350", "F44336", "E53935", "D32F2F", "C62828", "B71C1C" };
    private static final String[] pink       = { "FCE4EC", "F8BBD0", "F48FB1", "F06292", "EC407A", "E91E63", "D81B60", "C2185B", "AD1457", "880E4F" };
    private static final String[] purple     = { "F3E5F5", "E1BEE7", "CE93D8", "BA68C8", "AB47BC", "9C27B0", "8E24AA", "7B1FA2", "6A1B9A", "4A148C" };
    private static final String[] deepPurple = { "EDE7F6", "D1C4E9", "B39DDB", "9575CD", "7E57C2", "673AB7", "5E35B1", "512DA8", "4527A0", "311B92" };
    private static final String[] indigo     = { "E8EAF6", "C5CAE9", "9FA8DA", "7986CB", "5C6BC0", "3F51B5", "3949AB", "303F9F", "283593", "1A237E" };
    private static final String[] blue       = { "E3F2FD", "BBDEFB", "90CAF9", "64B5F6", "42A5F5", "2196F3", "1E88E5", "1976D2", "1565C0", "0D47A1" };
    private static final String[] lightBlue  = { "E1F5FE", "B3E5FC", "81D4FA", "4FC3F7", "29B6F6", "03A9F4", "039BE5", "0288D1", "0277BD", "01579B" };
    private static final String[] cyan       = { "E0F7FA", "B2EBF2", "80DEEA", "4DD0E1", "26C6DA", "00BCD4", "00ACC1", "0097A7", "00838F", "006064" };
    private static final String[] teal       = { "E0F2F1", "B2DFDB", "80CBC4", "4DB6AC", "26A69A", "009688", "00897B", "00796B", "00695C", "004D40" };
    private static final String[] green      = { "E8F5E9", "C8E6C9", "A5D6A7", "81C784", "66BB6A", "4CAF50", "43A047", "388E3C", "2E7D32", "1B5E20" };
    private static final String[] lightGreen = { "F1F8E9", "DCEDC8", "C5E1A5", "AED581", "9CCC65", "8BC34A", "7CB342", "689F38", "558B2F", "33691E" };
    private static final String[] lime       = { "F9FBE7", "F0F4C3", "E6EE9C", "DCE775", "D4E157", "CDDC39", "C0CA33", "AFB42B", "9E9D24", "9E9D24" };
    private static final String[] yellow     = { "FFFDE7", "FFF9C4", "FFF59D", "FFF176", "FFEE58", "FFEB3B", "FDD835", "FBC02D", "F9A825", "F57F17" };
    private static final String[] amber      = { "FFF8E1", "FFECB3", "FFE082", "FFD54F", "FFCA28", "FFC107", "FFB300", "FFA000", "FF8F00", "FF6F00" };
    private static final String[] orange     = { "FFF3E0", "FFE0B2", "FFCC80", "FFB74D", "FFA726", "FF9800", "FB8C00", "F57C00", "EF6C00", "EF6C00" };
    private static final String[] deepOrange = { "FBE9E7", "FFCCBC", "FFAB91", "FF8A65", "FF7043", "FF5722", "F4511E", "E64A19", "D84315", "BF360C" };
    private static final String[] brown      = { "EFEBE9", "D7CCC8", "BCAAA4", "A1887F", "8D6E63", "795548", "6D4C41", "5D4037", "4E342E", "3E2723" };
    private static final String[] grey       = { "FAFAFA", "F5F5F5", "EEEEEE", "E0E0E0", "BDBDBD", "9E9E9E", "757575", "616161", "424242", "212121" };
    private static final String[] blueGrey   = { "ECEFF1", "CFD8DC", "B0BEC5", "90A4AE", "78909C", "607D8B", "546E7A", "455A64", "37474F", "263238" };

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


}
