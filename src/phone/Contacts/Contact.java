package phone.Contacts;

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

    public Contact(long id, String lookupKey, String displayName, String contactType){
        this.id = id;
        this.lookupKey = lookupKey;
        this.displayName = displayName;
        this.contactType = contactType;
    }

    public Contact(long id, String lookupKey, String displayName, String contactType, List<String> phoneNumber){
        this.id = id;
        this.lookupKey = lookupKey;
        this.displayName = displayName;
        this.contactType = contactType;
        this.phoneNumber = phoneNumber;
    }

    public Contact() {
    }
}
