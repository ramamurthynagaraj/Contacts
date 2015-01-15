package phone.Contacts;

import android.app.Activity;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.ContactsContract.Contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContactsLoader {

    public static final String _ID = Contacts._ID;
    public static final String LOOKUP_KEY = Contacts.LOOKUP_KEY;
    public static final String DISPLAY_NAME = Contacts.DISPLAY_NAME_PRIMARY;
    public static final String CONTACT_TYPE = "CONTACT_TYPE";

    public static final int ID_INDEX = 0;
    public static final int LOOKUP_KEY_INDEX = 1;
    public static final int DISPLAY_NAME_INDEX = 2;
    public static final int CONTACT_TYPE_INDEX = 3;

    public static final String CONTACT_TYPE_MOBILE = "Mobile";
    public static final String CONTACT_TYPE_SIM = "Sim";

    private static final String[] CONTACT_DETAILS_PROJECTION = new String[]{
            _ID,
            LOOKUP_KEY,
            DISPLAY_NAME,
            CONTACT_TYPE
    };

    private SimContactsRepositoryImpl simContactsRepository;
    private PhoneContactsRepositoryImpl phoneContactsRepository;

    public ContactsLoader(Activity parentActivity){
        simContactsRepository = new SimContactsRepositoryImpl(parentActivity);
        phoneContactsRepository = new PhoneContactsRepositoryImpl(parentActivity);
    }

    public void loadAllContacts(ContactsLoaderCallback listener){
        listener.onContactsLoaded(contactsToCursor(getAllSortedContacts()));
    }

    private List<Contact> getAllSortedContacts() {
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.addAll(phoneContactsRepository.getAll());
        contacts.addAll(simContactsRepository.getAll());
        sortContacts(contacts);
        return contacts;
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

    public Contact getContactFor(long contactId, String contactName, String contactType){
        if (ContactsLoader.CONTACT_TYPE_MOBILE.equalsIgnoreCase(contactType)){
            return phoneContactsRepository.getById(contactId);
        }
        else {
            return simContactsRepository.getById(contactName);
        }
    }

    public void onSearchContact(String query, ContactsLoaderCallback listener) {
        List<Contact> allSortedContacts = getAllSortedContacts();
        List<Contact> filteredContacts = filterContacts(allSortedContacts, query);
        listener.onContactsLoaded(contactsToCursor(filteredContacts));
    }

    public boolean delete(Contact contact) {
        boolean isSuccess;
        if (ContactsLoader.CONTACT_TYPE_MOBILE.equalsIgnoreCase(contact.contactType)){
            isSuccess = phoneContactsRepository.delete(contact);
        }
        else {
            isSuccess = simContactsRepository.delete(contact);
        }
        return isSuccess;
    }
}
