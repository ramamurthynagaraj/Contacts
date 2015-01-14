package phone.Contacts;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nagaraj on 12/01/15.
 */
public class SimContactsRepositoryImpl implements IContactsRepository<String> {
    private Activity parentActivity;

    private static final String SIM_DISPLAY_NAME = "name";
    private static final String SIM_PHONE_NUMBER = "number";
    private static final String SIM_CONTENT_URI = "content://icc/adn";
    private static final long SIM_CONTACT_IDENTIFIER = 9999;
    public static final String CONTACT_TYPE_SIM = "Sim";


    public SimContactsRepositoryImpl(Activity activity){
        parentActivity = activity;
    }

    @Override
    public Contact getById(String id) {
        Uri simUri = Uri.parse(SIM_CONTENT_URI);
        Cursor simContactsCursor = parentActivity.getContentResolver().query(simUri, null, null, null, null);
        while (simContactsCursor.moveToNext()){
            String name = simContactsCursor.getString(simContactsCursor.getColumnIndex(SIM_DISPLAY_NAME));
            if (name.equals(id)){
                return getContact(simContactsCursor);
            }
        }
        return null;
    }

    @Override
    public boolean deleteById(String id) {
        return false;
    }

    @Override
    public List<Contact> getAll() {
        Uri simUri = Uri.parse(SIM_CONTENT_URI);
        Cursor simContacts = parentActivity.getContentResolver().query(simUri, null, null, null, null);
        List<Contact> simContactsForApp = new ArrayList<Contact>();
        while (simContacts.moveToNext()){
            simContactsForApp.add(getContact(simContacts));
        }
        return simContactsForApp;
    }

    private Contact getContact(Cursor simContactsCursor) {
        ArrayList<String> phoneNumbers = new ArrayList<String>();

        String name =simContactsCursor.getString(simContactsCursor.getColumnIndex(SIM_DISPLAY_NAME));
        phoneNumbers.add(simContactsCursor.getString(simContactsCursor.getColumnIndex(SIM_PHONE_NUMBER)));

        Contact contact = new Contact();
        contact.id = SIM_CONTACT_IDENTIFIER;
        contact.lookupKey = name;
        contact.displayName = name;
        contact.phoneNumber = phoneNumbers;
        contact.contactType = CONTACT_TYPE_SIM;
        return contact;
    }

}
