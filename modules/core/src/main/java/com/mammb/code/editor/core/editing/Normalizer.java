/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.core.editing;

import java.util.Map;

/**
 * The Normalizer.
 * @author Naotsugu Kobayashi
 */
public class Normalizer {

    /** The map of full to half. */
    private static final Map<String, String> fullToHalf = dict();

    /**
     * Converts from full width to half width as much as possible with the given mapping table.
     * @param str string to convert
     * @return converted string.
     */
    static String toHalfwidth(String str) {
        if (str == null || str.isBlank()) {
            return str;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            String s = String.valueOf(str.charAt(i));
            builder.append(fullToHalf.getOrDefault(s, s));
        }
        return builder.toString();
    }

    private static Map<String, String> dict() {
        return Map.<String, String>ofEntries(
            Map.entry("！", "!"), Map.entry("＃", "#"), Map.entry("＄", "$"), Map.entry("％", "%"), Map.entry("＆", "&"),
            Map.entry("？", "?"), Map.entry("＠", "@"), Map.entry("｜", "|"), Map.entry("＾", "^"), Map.entry("＿", "_"),
            Map.entry("’", "'"), Map.entry("｀", "`"), Map.entry("，", ","), Map.entry("．", "."), Map.entry("”", "\""),
            Map.entry("＋", "+"), Map.entry("－", "-"), Map.entry("＝", "="), Map.entry("＊", "*"), Map.entry("／", "/"),
            Map.entry("（", "("), Map.entry("）", ")"), Map.entry("＜", "<"), Map.entry("＞", ">"), Map.entry("：", ":"),
            Map.entry("［", "["), Map.entry("］", "]"), Map.entry("｛", "{"), Map.entry("｝", "}"), Map.entry("；", ";"),
            //Map.entry("「", "｢"), Map.entry("」", "｣"), Map.entry("、", "､"), Map.entry("・", "･"), Map.entry("。", "｡"),
            Map.entry("～", "~"), Map.entry("　", " "), Map.entry("￥", "\\"), Map.entry("―", "‐"),

            Map.entry("０", "0"), Map.entry("１", "1"), Map.entry("２", "2"), Map.entry("３", "3"), Map.entry("４", "4"),
            Map.entry("５", "5"), Map.entry("６", "6"), Map.entry("７", "7"), Map.entry("８", "8"), Map.entry("９", "9"),

            Map.entry("Ａ", "A"), Map.entry("Ｂ", "B"), Map.entry("Ｃ", "C"), Map.entry("Ｄ", "D"), Map.entry("Ｅ", "E"),
            Map.entry("Ｆ", "F"), Map.entry("Ｇ", "G"), Map.entry("Ｈ", "H"), Map.entry("Ｉ", "I"), Map.entry("Ｊ", "J"),
            Map.entry("Ｋ", "K"), Map.entry("Ｌ", "L"), Map.entry("Ｍ", "M"), Map.entry("Ｎ", "N"), Map.entry("Ｏ", "O"),
            Map.entry("Ｐ", "P"), Map.entry("Ｑ", "Q"), Map.entry("Ｒ", "R"), Map.entry("Ｓ", "S"), Map.entry("Ｔ", "T"),
            Map.entry("Ｕ", "U"), Map.entry("Ｖ", "V"), Map.entry("Ｗ", "W"), Map.entry("Ｘ", "X"), Map.entry("Ｙ", "Y"),
            Map.entry("Ｚ", "Z"),
            Map.entry("ａ", "a"), Map.entry("ｂ", "b"), Map.entry("ｃ", "c"), Map.entry("ｄ", "d"), Map.entry("ｅ", "e"),
            Map.entry("ｆ", "f"), Map.entry("ｇ", "g"), Map.entry("ｈ", "h"), Map.entry("ｉ", "i"), Map.entry("ｊ", "j"),
            Map.entry("ｋ", "k"), Map.entry("ｌ", "l"), Map.entry("ｍ", "m"), Map.entry("ｎ", "n"), Map.entry("ｏ", "o"),
            Map.entry("ｐ", "p"), Map.entry("ｑ", "q"), Map.entry("ｒ", "r"), Map.entry("ｓ", "s"), Map.entry("ｔ", "t"),
            Map.entry("ｕ", "u"), Map.entry("ｖ", "v"), Map.entry("ｗ", "w"), Map.entry("ｘ", "x"), Map.entry("ｙ", "y"),
            Map.entry("ｚ", "z")
        );
    }

    private static Map<String, String> katakana() {
        return Map.<String, String>ofEntries(
            Map.entry("ア", "ｱ"), Map.entry("イ", "ｲ"), Map.entry("ウ", "ｳ"), Map.entry("エ", "ｴ"), Map.entry("オ", "ｵ"),
            Map.entry("カ", "ｶ"), Map.entry("キ", "ｷ"), Map.entry("ク", "ｸ"), Map.entry("ケ", "ｹ"), Map.entry("コ", "ｺ"),
            Map.entry("サ", "ｻ"), Map.entry("シ", "ｼ"), Map.entry("ス", "ｽ"), Map.entry("セ", "ｾ"), Map.entry("ソ", "ｿ"),
            Map.entry("タ", "ﾀ"), Map.entry("チ", "ﾁ"), Map.entry("ツ", "ﾂ"), Map.entry("テ", "ﾃ"), Map.entry("ト", "ﾄ"),
            Map.entry("ナ", "ﾅ"), Map.entry("ニ", "ﾆ"), Map.entry("ヌ", "ﾇ"), Map.entry("ネ", "ﾈ"), Map.entry("ノ", "ﾉ"),
            Map.entry("ハ", "ﾊ"), Map.entry("ヒ", "ﾋ"), Map.entry("フ", "ﾌ"), Map.entry("ヘ", "ﾍ"), Map.entry("ホ", "ﾎ"),
            Map.entry("マ", "ﾏ"), Map.entry("ミ", "ﾐ"), Map.entry("ム", "ﾑ"), Map.entry("メ", "ﾒ"), Map.entry("モ", "ﾓ"),
            Map.entry("ヤ", "ﾔ"), Map.entry("ユ", "ﾕ"), Map.entry("ヨ", "ﾖ"),
            Map.entry("ラ", "ﾗ"), Map.entry("リ", "ﾘ"), Map.entry("ル", "ﾙ"), Map.entry("レ", "ﾚ"), Map.entry("ロ", "ﾛ"),
            Map.entry("ワ", "ﾜ"), Map.entry("ヲ", "ｦ"), Map.entry("ン", "ﾝ"),
            Map.entry("ー", "ｰ"), Map.entry("゛", "ﾞ"), Map.entry("゜", "ﾟ"),

            Map.entry("ァ", "ｧ"), Map.entry("ィ", "ｨ"), Map.entry("ゥ", "ｩ"), Map.entry("ェ", "ｪ"), Map.entry("ォ", "ｫ"),
            Map.entry("ャ", "ｬ"), Map.entry("ュ", "ｭ"), Map.entry("ョ", "ｮ"), Map.entry("ッ", "ｯ"),

            Map.entry("ガ", "ｶﾞ"), Map.entry("ギ", "ｷﾞ"), Map.entry("グ", "ｸﾞ"), Map.entry("ゲ", "ｹﾞ"), Map.entry("ゴ", "ｺﾞ"),
            Map.entry("ザ", "ｻﾞ"), Map.entry("ジ", "ｼﾞ"), Map.entry("ズ", "ｽﾞ"), Map.entry("ゼ", "ｾﾞ"), Map.entry("ゾ", "ｿﾞ"),
            Map.entry("ダ", "ﾀﾞ"), Map.entry("ヂ", "ﾁﾞ"), Map.entry("ヅ", "ﾂﾞ"), Map.entry("デ", "ﾃﾞ"), Map.entry("ド", "ﾄﾞ"),
            Map.entry("バ", "ﾊﾞ"), Map.entry("ビ", "ﾋﾞ"), Map.entry("ブ", "ﾌﾞ"), Map.entry("ベ", "ﾍﾞ"), Map.entry("ボ", "ﾎﾞ"),
            Map.entry("パ", "ﾊﾟ"), Map.entry("ピ", "ﾋﾟ"), Map.entry("プ", "ﾌﾟ"), Map.entry("ペ", "ﾍﾟ"), Map.entry("ポ", "ﾎﾟ"),
            Map.entry("ヴ", "ｳﾞ"), Map.entry("ヷ", "ﾜﾞ"), Map.entry("ヺ", "ｦﾞ")
        );
    }
}
