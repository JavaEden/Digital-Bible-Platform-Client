package com.eden.digitalbibleplatform;

import com.eden.bible.Book;
import com.eden.defaults.DefaultBible;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class DBTBook extends Book implements JsonDeserializer<DBTBook> {
	public DBTBook() {
		super();
	}

	@Override
	public String toString() {
		String s = getName() + " ";
		for(int chapter : getChapters()) {
			s += chapter + ", ";
		}
		s += getId();
		return s;
	}

    @Override
    public DBTBook deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject bookJson = json.getAsJsonObject();

        this.id = bookJson.get("book_id").getAsString();

        this.name = bookJson.get("book_name").getAsString();
        this.abbreviation = bookJson.get("book_id").getAsString();
        this.location = bookJson.get("book_order").getAsInt();

        // with Digitial Bible Platform, the number of verses in each chapter in each book is difficult to obtain, as
        // that number is only provided by a separate call to each Chapter, so instead we can estimate the number of
        // verses in each chapter in this book with the default numbers in the DefaultBible class, which is based on the
        // counts in the English Standard Version. It only has knowledge of 66 books, but the Digital Bible Platform
        // has 81, with Matthew starting at order 55, so we must try to account for New/Old testament differences and
        // ignore apocryphal books for now when getting these numbers from the DefaultBible. If possible, implement your
        // UI to asynchronously fetch the number of verses for each chapter when actually searching, and only rely on
        // these numbers as a fallback, i.e. still trying to provide maximum functionality with no internet connection.
        if(this.location >= 55) {
            if((this.location - 16) < DefaultBible.defaultBookVerseCount.length) {
                this.setChapters(DefaultBible.defaultBookVerseCount[(this.location - 16)]);
            }
        }
        else if(this.location <= 39){
            if((this.location - 1) < DefaultBible.defaultBookVerseCount.length) {
                this.setChapters(DefaultBible.defaultBookVerseCount[(this.location - 1)]);
            }
        }

        return this;
    }
}
