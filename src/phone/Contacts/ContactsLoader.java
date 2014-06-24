package phone.Contacts;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;

import java.util.*;

public class ContactsLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private Activity parentActivity;
    private ContactsLoaderCallback listener;

    private static final String SIM_DISPLAY_NAME = "name";
    private static final String SIM_PHONE_NUMBER = "number";
    private static final String HAS_PHONE_NUMBER = Contacts.HAS_PHONE_NUMBER;
    private static final String SIM_CONTENT_URI = "content://icc/adn";
    private static final long SIM_CONTACT_IDENTIFIER = 9999;

    public static final String _ID = Contacts._ID;
    public static final String LOOKUP_KEY = Contacts.LOOKUP_KEY;
    public static final String DISPLAY_NAME = Contacts.DISPLAY_NAME_PRIMARY;
    public static final String CONTACT_TYPE = "CONTACT_TYPE";
    public static final String CONTACT_TYPE_MOBILE = "Mobile";
    public static final String CONTACT_TYPE_SIM = "Sim";
    public static final int ID_INDEX = 0;
    public static final int LOOKUP_KEY_INDEX = 1;
    public static final int DISPLAY_NAME_INDEX = 2;
    public static final int CONTACT_TYPE_INDEX = 3;

    private static final String[] CONTACT_DETAILS_PROJECTION = new String[]{
            _ID,
            LOOKUP_KEY,
            DISPLAY_NAME,
            CONTACT_TYPE
    };

    public ContactsLoader(Activity parentActivity, ContactsLoaderCallback listener){
        this.parentActivity = parentActivity;
        this.listener = listener;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(parentActivity);
        cursorLoader.setUri(Contacts.CONTENT_URI);
        cursorLoader.setProjection(new String[]{
                _ID,
                LOOKUP_KEY,
                DISPLAY_NAME,
                HAS_PHONE_NUMBER
        });
        cursorLoader.setSortOrder(DISPLAY_NAME);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Cursor phoneContacts = getContactsWithPhoneNumber(data);
        Cursor simContacts = getSimContacts();
        listener.onContactsLoaded(getSortedAndMergedCursor(phoneContacts, simContacts));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listener.onContactsLoaded(null);
    }

    private Cursor getContactsWithPhoneNumber(Cursor allContacts){
        MatrixCursor contactsWithPhoneNumber = new MatrixCursor(CONTACT_DETAILS_PROJECTION);
        while (allContacts.moveToNext()){
            if (allContacts.getInt(allContacts.getColumnIndex(HAS_PHONE_NUMBER)) == 1){
                contactsWithPhoneNumber.addRow(new Object []{
                        allContacts.getLong(ID_INDEX),
                        allContacts.getString(LOOKUP_KEY_INDEX),
                        allContacts.getString(DISPLAY_NAME_INDEX),
                        CONTACT_TYPE_MOBILE
                });
            }
        }
        return contactsWithPhoneNumber;
    }

    private Cursor getSimContacts() {
        Uri simUri = Uri.parse(SIM_CONTENT_URI);
        Cursor simContacts = parentActivity.getContentResolver().query(simUri, null, null, null, null);
        MatrixCursor simContactsForApp = new MatrixCursor(CONTACT_DETAILS_PROJECTION);
        while (simContacts.moveToNext()){
            String name =simContacts.getString(simContacts.getColumnIndex(SIM_DISPLAY_NAME));
            String number = simContacts.getString(simContacts.getColumnIndex(SIM_PHONE_NUMBER));
            simContactsForApp.addRow(new Object[]{
                    SIM_CONTACT_IDENTIFIER,
                    name,
                    name,
                    CONTACT_TYPE_SIM
            });
        }
        return simContactsForApp;
    }

    private Cursor getSortedAndMergedCursor(Cursor phoneContacts, Cursor simContacts){
        MergeCursor mergedCursor = new MergeCursor(new Cursor[]{phoneContacts, simContacts});
        List<Contact> contacts = cursorToContacts(mergedCursor);
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                return lhs.displayName.compareToIgnoreCase(rhs.displayName);
            }
        });
        return contactsToCursor(contacts);
    }

    private List<Contact> cursorToContacts(MergeCursor mergedCursor) {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        while (mergedCursor.moveToNext()){
            contacts.add(new Contact(
                    mergedCursor.getLong(ID_INDEX),
                    mergedCursor.getString(LOOKUP_KEY_INDEX),
                    mergedCursor.getString(DISPLAY_NAME_INDEX),
                    mergedCursor.getString(CONTACT_TYPE_INDEX)
            ));
        }
        return contacts;
    }

    private Cursor contactsToCursor(List<Contact> contacts) {
        MatrixCursor cursor = new MatrixCursor(CONTACT_DETAILS_PROJECTION);
        for (Contact contact : contacts){
            cursor.addRow(new Object[]{
                    contact.id,
                    contact.lookupKey,
                    contact.displayName,
                    contact.contactType
            });
        }
        return cursor;
    }

    private class Contact {
        public long id;
        public String lookupKey;
        public String displayName;
        public String contactType;
        public Contact(long id, String lookupKey, String displayName, String contactType){
            this.id = id;
            this.lookupKey = lookupKey;
            this.displayName = displayName;
            this.contactType = contactType;
        }
    }
}
