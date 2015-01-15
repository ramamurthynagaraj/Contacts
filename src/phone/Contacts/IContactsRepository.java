package phone.Contacts;

import java.util.List;

/**
 * Created by Nagaraj on 12/01/15.
 */
public interface IContactsRepository<T> {
    Contact getById(T id);
    boolean delete(Contact contact);
    List<Contact> getAll();
}
