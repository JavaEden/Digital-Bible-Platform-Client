package com.eden.digitalbibleplatform;

import com.caseyjbrooks.eden.bible.AbstractVerse;
import com.caseyjbrooks.eden.interfaces.Formatter;

public class DBPFormatter implements Formatter {
    protected AbstractVerse verse;

    @Override
    public String onPreFormat(AbstractVerse verse) {
        if(!(verse instanceof DBPVerse || verse instanceof DBPPassage)) {
            throw new IllegalArgumentException("DBPFormatter expects a verse of type DBPVerse or DBPPassage");
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
