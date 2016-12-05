package com.eden.digitalbibleplatform;

import com.eden.Eden;
import com.eden.bible.BibleList;
import com.eden.utils.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DBTBibleList extends BibleList<DBTBible> implements JsonDeserializer<DBTBibleList> {
    public DBTBibleList() {

    }

    public boolean get() {
        String APIKey = Eden.getInstance().getMetadata().getString("DBT_ApiKey", null);

        if (TextUtils.isEmpty(APIKey)) {
            throw new IllegalStateException(
                    "API key not set in Eden metadata. Please add 'DBT_ApiKey' key to metadata."
            );
        }

        String url = "http://dbt.io/library/volume?key=" + APIKey + "&v=2";

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String body = response.body().string();

            Gson gson = new GsonBuilder().registerTypeAdapter(DBTBibleList.class, this).create();
            gson.fromJson(body, DBTBibleList.class);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public DBTBibleList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonArray biblesJson = json.getAsJsonArray();

        this.bibles = new HashMap<>();

        Gson gson = Eden.getInstance().getDeserializer().registerTypeAdapter(DBTBible.class, new DBTBible.ListJsonizer()).create();

        for (int i = 0; i < biblesJson.size(); i++) {
            DBTBible bible = gson.fromJson(biblesJson.get(i), DBTBible.class);
            bibles.put(bible.getId(), bible);
        }

        return this;
    }
}
