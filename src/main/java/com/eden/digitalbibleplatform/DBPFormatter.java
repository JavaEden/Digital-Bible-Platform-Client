package com.eden.digitalbibleplatform;

import com.caseyjbrooks.eden.bible.AbstractVerse;
import com.caseyjbrooks.eden.interfaces.VerseFormatter;

public class DBPFormatter implements VerseFormatter {
    protected AbstractVerse verse;

    @Override
    public String onPreFormat(AbstractVerse verse) {
        if(!(verse instanceof DBPPassage)) {
            throw new IllegalArgumentException("DBPFormatter expects a verse of type DBPPassage");
        }

        this.verse = verse;
        return "";
    }

    @Override
    public String onFormatVerseStart(int i) {
        return "";
    }

    @Override
    public String onFormatText(String s) {
        return s;
    }

    @Override
    public String onFormatVerseEnd() {
        return " ";
    }

    @Override
    public String onPostFormat() {
        return "";
    }
}
