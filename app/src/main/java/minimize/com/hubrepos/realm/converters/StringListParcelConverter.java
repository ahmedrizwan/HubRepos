package minimize.com.hubrepos.realm.converters;

import android.os.Parcel;

import org.parceler.Parcels;

/**
 * Created by ahmedrizwan on 9/29/15.
 */
public class StringListParcelConverter extends RealmListParcelConverter<RealmString> {
    @Override
    public void itemToParcel(RealmString input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public RealmString itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(RealmString.class.getClassLoader()));
    }
}
