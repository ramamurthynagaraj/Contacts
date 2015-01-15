package phone.Contacts;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nagaraj on 12/01/15.
 */
public class PhoneContactsRepositoryImpl implements IContactsRepository<Long> {

    public static final Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    public static final Uri CONTENT_LOOKUP_URI = ContactsContract.Contacts.CONTENT_LOOKUP_URI;
    private Activity parentActivity;
    public static final String _ID = ContactsContract.Contacts._ID;
    public static final String LOOKUP_KEY = ContactsContract.Contacts.LOOKUP_KEY;
    public static final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
    private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private static final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final String CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    public static final String CONTACT_TYPE_MOBILE = "Mobile";
    public static final String WHERE_ID = _ID + "=?";

    public PhoneContactsRepositoryImpl(Activity activity){

        parentActivity = activity;
    }

    @Override
    public Contact getById(Long contactId) {
        Cursor phoneContactsCursor = parentActivity.getContentResolver().query(CONTENT_URI, null, WHERE_ID, new String[]{ contactId.toString() }, null);
        if (phoneContactsCursor.moveToFirst()){
            Contact contact = getContact(phoneContactsCursor, getAllPhoneNumbers());
            contact.photoUri = getContactPhotoUri(contactId);
            return contact;
        }
        return null;
    }

    @Override
    public boolean delete(Contact contact) {
        Uri contentLookupUri = Uri.withAppendedPath(CONTENT_LOOKUP_URI, contact.lookupKey);
        int rowsDeleted = parentActivity.getContentResolver().delete(contentLookupUri, WHERE_ID, new String[]{String.valueOf(contact.id)});
        return rowsDeleted >= 1;
    }

    @Override
    public List<Contact> getAll() {
        Cursor phoneContactsCursor = parentActivity.getContentResolver()
                .query(CONTENT_URI, null, null, null, null);
        List<PhoneNumber> allPhoneNumbers = getAllPhoneNumbers();

        List<Contact> phoneContacts = new ArrayList<Contact>();
        while (phoneContactsCursor.moveToNext()){
            Contact contact = getContact(phoneContactsCursor, allPhoneNumbers);
            int hasPhoneNumber = phoneContactsCursor.getInt(phoneContactsCursor.getColumnIndex(HAS_PHONE_NUMBER));
            if (hasPhoneNumber == 1) {
                phoneContacts.add(contact);
            }
        }
        return phoneContacts;
    }

    private Contact getContact(Cursor phoneContacts, List<PhoneNumber> allPhoneNumbers) {
        String name = phoneContacts.getString(phoneContacts.getColumnIndex(DISPLAY_NAME));
        long id = phoneContacts.getLong(phoneContacts.getColumnIndex(_ID));
        String lookupKey = phoneContacts.getString(phoneContacts.getColumnIndex(LOOKUP_KEY));
        Contact contact = new Contact();
        contact.id = id;
        contact.lookupKey = lookupKey;
        contact.displayName = name;
        contact.phoneNumber = getAllContactNumbers(id, allPhoneNumbers);
        contact.contactType = CONTACT_TYPE_MOBILE;
        return contact;
    }

    private List<String> getAllContactNumbers(long contactId, List<PhoneNumber> phoneNumbers){
        List<String> contactPhoneNumbers = new ArrayList<String>();
        for (PhoneNumber number : phoneNumbers){
            if (contactId == number.contactId){
                contactPhoneNumbers.add(number.phoneNumber);
            }
        }
        return contactPhoneNumbers;
    }

    private List<PhoneNumber> getAllPhoneNumbers() {
        Cursor phoneNumbersCursor = parentActivity.getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
        while (phoneNumbersCursor.moveToNext()){
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.contactId = phoneNumbersCursor.getLong(phoneNumbersCursor.getColumnIndex(CONTACT_ID));
            phoneNumber.phoneNumber = phoneNumbersCursor.getString(phoneNumbersCursor.getColumnIndex(PHONE_NUMBER));
            phoneNumbers.add(phoneNumber);
        }
        return phoneNumbers;

    }

    public Uri getContactPhotoUri(long contactId){
        Uri contactUri = ContentUris.withAppendedId(CONTENT_URI, contactId);
        InputStream photoInputStream = ContactsContract.Contacts.openContactPhotoInputStream(parentActivity.getContentResolver(), contactUri);
        if (photoInputStream == null){
            return null;
        }
        return Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }
}
