package com.eden.digitalbibleplatform;

import com.eden.Eden;
import com.eden.bible.Bible;
import com.eden.utils.TextUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.lang.reflect.Type;

public class DBTBible extends Bible<DBTBook> implements JsonDeserializer<DBTBible> {
//Data Members
//--------------------------------------------------------------------------------------------------

    protected String nameEnglish;
    protected String languageEnglish;

//Constructors
//--------------------------------------------------------------------------------------------------
    public DBTBible() {
        super();
    }

//Getters and Setters
//--------------------------------------------------------------------------------------------------
    public String getNameEnglish() {
        return nameEnglish;
    }

    public void setNameEnglish(String nameEnglish) {
        this.nameEnglish = nameEnglish;
    }

    public String getLanguageEnglish() {
        return languageEnglish;
    }

    public void setLanguageEnglish(String languageEnglish) {
        this.languageEnglish = languageEnglish;
    }

//Downloadable Interface Implementation
//--------------------------------------------------------------------------------------------------
    public boolean get() {
        String APIKey = Eden.getInstance().getMetadata().getString("DBT_ApiKey", null);

        if (TextUtils.isEmpty(APIKey)) {
            throw new IllegalStateException(
                    "API key not set in Eden metadata. Please add 'DBT_ApiKey' key to metadata."
            );
        }

        String bibleUrl = "http://dbt.io/library/volume?key=" + APIKey + "&dam_id=" + this.id + "&v=2";
        String booksUrl = "http://dbt.io/library/book?key=" + APIKey + "&dam_id=" + this.id + "&v=2";

        try {
            OkHttpClient client = new OkHttpClient();

            // get the main data about the Bible version
            Request bibleRequest = new Request.Builder()
                    .url(bibleUrl)
                    .build();

            String bibleString = client
                    .newCall(bibleRequest)
                    .execute()
                    .body()
                    .string();

            JsonObject bibleJson = new JsonParser().parse(bibleString).getAsJsonArray().get(0).getAsJsonObject();

            // get the books in this Bible
            Request booksRequest = new Request.Builder()
                    .url(booksUrl)
                    .build();

            String booksString = client
                    .newCall(booksRequest)
                    .execute()
                    .body()
                    .string();

            JsonArray booksJson = new JsonParser().parse(booksString).getAsJsonArray();

            JsonObject craftedJson = new JsonObject();
            craftedJson.add("bible", bibleJson);
            craftedJson.add("books", booksJson);

            Gson gson = Eden.getInstance().getDeserializer().registerTypeAdapter(DBTBible.class, this).create();
            gson.fromJson(craftedJson, DBTBible.class);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof DBTBible)) {
            return false;
        }

        DBTBible bible = (DBTBible) o;

        if (getId().equalsIgnoreCase(bible.getId())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : 0;
    }

    @Override
    public DBTBible deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject bibleJson = json.getAsJsonObject();

        JsonObject bibleInfoJson = bibleJson.get("bible").getAsJsonObject();
        JsonArray booksJson = bibleJson.get("books").getAsJsonArray();

        //TODO: find and extract 'language' and 'languageEnglish'

        if(bibleJson.has("dam_id"))           { this.setId(              bibleJson.get("dam_id").getAsString().trim()); }
        if(bibleJson.has("version_name"))     { this.setName(            bibleJson.get("version_name").getAsString().trim()); }
        if(bibleJson.has("version_code"))     { this.setAbbreviation(    bibleJson.get("version_code").getAsString().trim()); }
        if(bibleJson.has("language_name"))    { this.setLanguage(        bibleJson.get("language_name").getAsString().trim()); }
        if(bibleJson.has("language_english")) { this.setLanguageEnglish( bibleJson.get("language_english").getAsString().trim()); }
        if(bibleJson.has("volume_name"))      { this.setCopyright(       bibleJson.get("volume_name").getAsString().trim()); }
        if(bibleJson.has("version_english"))  { this.setNameEnglish(     bibleJson.get("version_english").getAsString().trim()); }

        for(int i = 0; i < booksJson.size(); i++) {
            JsonObject bookJson = booksJson.get(i).getAsJsonObject();

            DBTBook book = new DBTBook();
            Gson gson = Eden.getInstance().getDeserializer().registerTypeAdapter(DBTBook.class, book).create();
            gson.fromJson(bookJson, DBTBook.class);

            this.books.add(book);
        }

        return this;
    }

    public static class ListJsonizer implements JsonDeserializer<DBTBible> {
        @Override
        public DBTBible deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject bibleJson = json.getAsJsonObject();
            DBTBible bible = new DBTBible();
            if(bibleJson.has("dam_id"))           { bible.setId(              bibleJson.get("dam_id").getAsString().trim()); }
            if(bibleJson.has("version_name"))     { bible.setName(            bibleJson.get("version_name").getAsString().trim()); }
            if(bibleJson.has("version_code"))     { bible.setAbbreviation(    bibleJson.get("version_code").getAsString().trim()); }
            if(bibleJson.has("language_name"))    { bible.setLanguage(        bibleJson.get("language_name").getAsString().trim()); }
            if(bibleJson.has("language_english")) { bible.setLanguageEnglish( bibleJson.get("language_english").getAsString().trim()); }
            if(bibleJson.has("volume_name"))      { bible.setCopyright(       bibleJson.get("volume_name").getAsString().trim()); }
            if(bibleJson.has("version_english"))  { bible.setNameEnglish(     bibleJson.get("version_english").getAsString().trim()); }

            return bible;
        }
    }
}
