package phone.Contacts;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContactsLoader {

    private Activity parentActivity;

    private static final String SIM_DISPLAY_NAME = "name";
    private static final String SIM_PHONE_NUMBER = "number";
    private static final String SIM_CONTENT_URI = "content://icc/adn";
    private static final long SIM_CONTACT_IDENTIFIER = 9999;

    public static final String _ID = Contacts._ID;
    public static final String LOOKUP_KEY = Contacts.LOOKUP_KEY;
    public static final String DISPLAY_NAME = Contacts.DISPLAY_NAME_PRIMARY;
    private static final String HAS_PHONE_NUMBER = Contacts.HAS_PHONE_NUMBER;
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

    public ContactsLoader(Activity parentActivity){
        this.parentActivity = parentActivity;
    }

    public void loadAllContacts(ContactsLoaderCallback listener){
        listener.onContactsLoaded(contactsToCursor(getAllSortedContacts()));
    }

    private List<Contact> getAllSortedContacts() {
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.addAll(getPhoneContacts());
        contacts.addAll(getSimContacts());
        sortContacts(contacts);
        return contacts;
    }

    private List<Contact> getPhoneContacts() {
        Cursor phoneContacts = parentActivity.getContentResolver().query(Contacts.CONTENT_URI, null, null, null, null);
        MatrixCursor phoneContactsForApp = new MatrixCursor(CONTACT_DETAILS_PROJECTION);
        while (phoneContacts.moveToNext()){
            String name =phoneContacts.getString(phoneContacts.getColumnIndex(DISPLAY_NAME));
            String id = phoneContacts.getString(phoneContacts.getColumnIndex(_ID));
            String lookupKey = phoneContacts.getString(phoneContacts.getColumnIndex(LOOKUP_KEY));
            int hasPhoneNumber = phoneContacts.getInt(phoneContacts.getColumnIndex(HAS_PHONE_NUMBER));
            if (hasPhoneNumber == 1) {
                phoneContactsForApp.addRow(new Object[]{
                        id,
                        lookupKey,
                        name,
                        CONTACT_TYPE_MOBILE
                });
            }
        }
        return cursorToContacts(phoneContactsForApp);
    }

    private List<Contact> getSimContacts() {
        Uri simUri = Uri.parse(SIM_CONTENT_URI);
        Cursor simContacts = parentActivity.getContentResolver().query(simUri, null, null, null, null);
        MatrixCursor simContactsForApp = new MatrixCursor(CONTACT_DETAILS_PROJECTION);
        while (simContacts.moveToNext()){
            String name =simContacts.getString(simContacts.getColumnIndex(SIM_DISPLAY_NAME));
            simContactsForApp.addRow(new Object[]{
                    SIM_CONTACT_IDENTIFIER,
                    name,
                    name,
                    CONTACT_TYPE_SIM
            });
        }
        return cursorToContacts(simContactsForApp);
    }

    private void sortContacts(List<Contact> contacts) {
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                return lhs.displayName.compareToIgnoreCase(rhs.displayName);
            }
        });
    }

    private List<Contact> filterContacts(List<Contact> phoneContacts, String nameSearchString) {
        List<Contact> filteredContacts = new ArrayList<Contact>();
        for (Contact contact : phoneContacts){
            if (contact.displayName.toLowerCase().startsWith(nameSearchString.toLowerCase())){
                filteredContacts.add(contact);
            }
        }
        return filteredContacts;
    }

    private List<Contact> cursorToContacts(Cursor mergedCursor) {
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

    public List<String> getMobilePhoneNumber(long contactId){
        String whereCondition = Phone.CONTACT_ID + "=?";
        Cursor phoneDataCursor = parentActivity.getContentResolver().query(
                Phone.CONTENT_URI,
                null,
                whereCondition,
                new String[]{ String.valueOf(contactId)},
                null
        );
        List<String> phoneNumbers = new ArrayList<String>();
        while(phoneDataCursor.moveToNext()){
            String phoneNumber = phoneDataCursor.getString(phoneDataCursor.getColumnIndex(Phone.NUMBER));
            phoneNumbers.add(phoneNumber);
        }
        return phoneNumbers;
    }

    public List<String> getSimPhoneNumber(String contactName){
        Cursor simContactCursor = parentActivity.getContentResolver().query(
                Uri.parse(SIM_CONTENT_URI),
                null,
                null,
                null,
                null
        );
        List<String> phoneNumbers = new ArrayList<String>();
        while(simContactCursor.moveToNext()){
            String name = simContactCursor.getString(simContactCursor.getColumnIndex(SIM_DISPLAY_NAME));
            String phoneNumber = simContactCursor.getString(simContactCursor.getColumnIndex(SIM_PHONE_NUMBER));
            if (name.equals(contactName)) {
                phoneNumbers.add(phoneNumber);
            }
        }
        return phoneNumbers;
    }

    public Uri getContactPhotoUri(long contactId){
        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        InputStream photoInputStream = Contacts.openContactPhotoInputStream(parentActivity.getContentResolver(), contactUri);
        if (photoInputStream == null){
            return null;
        }
        return Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
    }
    public void onSearchContact(String query, ContactsLoaderCallback listener) {
        List<Contact> allSortedContacts = getAllSortedContacts();
        List<Contact> filteredContacts = filterContacts(allSortedContacts, query);
        listener.onContactsLoaded(contactsToCursor(filteredContacts));
    }

}
