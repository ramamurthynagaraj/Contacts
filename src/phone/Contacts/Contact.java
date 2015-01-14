package phone.Contacts;

import android.net.Uri;

import java.util.List;

/**
* Created by Nagaraj on 12/01/15.
*/
public class Contact {
    public long id;
    public String lookupKey;
    public String displayName;
    public String contactType;
    public List<String> phoneNumber;
    public Uri photoUri;

    public Contact() {
    }
}
