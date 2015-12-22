package minimize.com.hubrepos.realm.converters;

import com.google.gson.annotations.Expose;

import org.parceler.Parcel;

import io.realm.RealmObject;

/**
 * Created by ahmedrizwan on 9/20/15.
 *
 */
@Parcel(value = Parcel.Serialization.BEAN, analyze = { RealmString.class })
public class RealmString extends RealmObject {

    @Expose
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
