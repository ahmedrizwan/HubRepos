package minimize.com.hubrepos.realm.converters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class QLRealmStringAdapter extends TypeAdapter<RealmString> {

    @Override
    public void write(JsonWriter out, RealmString value) throws IOException {
        out.value(value.getValue());
    }

    @Override
    public RealmString read(JsonReader in) throws IOException {
        RealmString rString = new RealmString();
        if (in.hasNext()) {
            String nextStr = in.nextString();
            rString.setValue(nextStr);
        }
        return rString;
    }
}

