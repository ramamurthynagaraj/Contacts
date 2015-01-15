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
    private static final String TAG = "tag";
    private static final String SIM_PHONE_NUMBER = "number";
    private static final long SIM_CONTACT_IDENTIFIER = 9999;
    public static final String CONTACT_TYPE_SIM = "Sim";
    public static final Uri SIM_CONTENT_URI = Uri.parse("content://icc/adn");
    public static final String WHERE_TAG = TAG + "=? AND " + SIM_PHONE_NUMBER + "=?";


    public SimContactsRepositoryImpl(Activity activity){
        parentActivity = activity;
    }

    @Override
    public Contact getById(String id) {
        Cursor simContactsCursor = parentActivity.getContentResolver().query(SIM_CONTENT_URI, null, null, null, null);
        while (simContactsCursor.moveToNext()){
            String name = simContactsCursor.getString(simContactsCursor.getColumnIndex(SIM_DISPLAY_NAME));
            if (name.equals(id)){
                return getContact(simContactsCursor);
            }
        }
        return null;
    }

    @Override
    public boolean delete(Contact contact) {
        String whereCondition = String.format("tag='%s' AND number='%s'", contact.displayName, contact.phoneNumber.get(0));
        int rowsDeleted = parentActivity.getContentResolver().delete(SIM_CONTENT_URI, whereCondition, null);
        return rowsDeleted >= 1;
    }

    @Override
    public List<Contact> getAll() {
        Cursor simContacts = parentActivity.getContentResolver().query(SIM_CONTENT_URI, null, null, null, null);
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
