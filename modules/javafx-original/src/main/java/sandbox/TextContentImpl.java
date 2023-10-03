package sandbox;

public class TextContentImpl implements TextContent {

    private StringBuilder text = new StringBuilder();

    @Override
    public String get() {
        return text.toString();
    }

    @Override
    public String get(int start, int end) {
        return text.substring(start, end);
    }

    @Override
    public void insert(int index, String text) {
        text = filterInput(text, true, true);
        if (text.isEmpty()) {
            return;
        }
        this.text.insert(index, text);
    }

    @Override
    public void delete(int start, int end) {
        if (end > start) {
            text.delete(start, end);
        }
    }

    @Override
    public int length() {
        return text.length();
    }


    /**
     * A little utility method for stripping out unwanted characters.
     * @param txt
     * @param stripNewlines
     * @param stripTabs
     * @return The string after having the unwanted characters stripped out.
     */
    static String filterInput(String txt, boolean stripNewlines, boolean stripTabs) {
        if (containsInvalidCharacters(txt, stripNewlines, stripTabs)) {
            StringBuilder s = new StringBuilder(txt.length());
            for (int i = 0; i < txt.length(); i++) {
                final char c = txt.charAt(i);
                if (!isInvalidCharacter(c, stripNewlines, stripTabs)) {
                    s.append(c);
                }
            }
            txt = s.toString();
        }
        return txt;
    }


    static boolean containsInvalidCharacters(String txt, boolean newlineIllegal, boolean tabIllegal) {
        for (int i = 0; i < txt.length(); i++) {
            final char c = txt.charAt(i);
            if (isInvalidCharacter(c, newlineIllegal, tabIllegal)) return true;
        }
        return false;
    }

    private static boolean isInvalidCharacter(char c, boolean newlineIllegal, boolean tabIllegal) {
        if (c == 0x7F) return true;
        if (c == 0xA) return newlineIllegal;
        if (c == 0x9) return tabIllegal;
        if (c < 0x20) return true;
        return false;
    }

}
