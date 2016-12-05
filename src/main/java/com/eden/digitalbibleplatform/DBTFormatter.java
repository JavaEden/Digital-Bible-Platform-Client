package com.eden.digitalbibleplatform;

import com.eden.bible.AbstractVerse;
import com.eden.interfaces.VerseFormatter;

public class DBTFormatter implements VerseFormatter {
    protected AbstractVerse verse;

    @Override
    public String onPreFormat(AbstractVerse verse) {
        if(!(verse instanceof DBTPassage)) {
            throw new IllegalArgumentException("DBTFormatter expects a verse of type DBTPassage");
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
