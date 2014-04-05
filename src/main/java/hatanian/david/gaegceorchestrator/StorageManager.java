package hatanian.david.gaegceorchestrator;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class StorageManager<E> {
    private final Class<E> c;
    private static Set<String> classes = new HashSet<>();

    public StorageManager(Class<E> c) {
        this.c = c;
        String classId = c.getPackage() + c.getName();
        if (!classes.contains(classId)) {
            synchronized (classes) {
                if (!classes.contains(classId)) {
                    ObjectifyService.factory().register(c);
                    classes.add(classId);
                }
            }
        }
    }

    public void clearSessionCache() {
        ofy().clear();
    }

    public E save(E e) {
        ofy().save().entity(e).now();
        return e;
    }

    public void saveAll(Iterable<? extends E> list) {
        ofy().save().entities(list).now();
    }

    public E get(Long id) {
        return ofy().load().type(c).id(id).now();
    }

    public E get(String id) {
        return ofy().load().type(c).id(id).now();
    }

    public void delete(E e) {
        ofy().delete().entity(e).now();
    }

    public void deleteAll(Iterable<E> e) {
        ofy().delete().entities(e).now();
    }

    public Collection<E> getAll(List<String> ids) {
        return ofy().load().type(c).ids(ids).values();
    }

    public Set<Entry<String, E>> getAllBySet(List<String> ids) {
        return ofy().load().type(c).ids(ids).entrySet();
    }

    public List<E> getBy(String order, int limit) {
        return ofy().load().type(c).order(order).limit(limit).list();
    }

    public List<E> getBy(String byString, Object byObject) {
        return ofy().load().type(c).filter(byString, byObject).list();
    }

    public List<E> getBy(String byString, Object byObject, String order,
                         int limit) {
        return ofy().load().type(c).filter(byString, byObject).order(order)
                .limit(limit).list();
    }

    public List<E> getBy(String filterColumns[], Object filterValues[],
                         String order, int limit) {
        Query<E> query = ofy().load().type(c);
        for (int i = 0; i < filterColumns.length; i++) {
            query = query.filter(filterColumns[i], filterValues[i]);
        }
        return query.order(order).limit(limit).list();
    }

    public int count(String filterColumns[], Object filterValues[]) {
        Query<E> query = ofy().load().type(c);
        for (int i = 0; i < filterColumns.length; i++) {
            query = query.filter(filterColumns[i], filterValues[i]);
        }
        return query.count();
    }

    public int count(String byString, Object byObject) {
        return ofy().load().type(c).filter(byString, byObject).count();
    }

    public void deleteById(String userId) {
        ofy().delete().type(c).ids(userId).now();
    }

    public List<E> getBy(String[] filterColumns, Object[] filterValues,
                         int limit) {
        Query<E> query = ofy().load().type(c);
        for (int i = 0; i < filterColumns.length; i++) {
            query = query.filter(filterColumns[i], filterValues[i]);
        }
        return query.limit(limit).list();
    }

    public QueryResultIterator<E> list(Cursor cursor, int limit, String order, String[] filterColumns, Object[] filterValues) {
        Query<E> query = ofy().load().type(c).order(order).limit(limit);
        if (cursor != null) {
            query = query.startAt(cursor);
        }
        for (int i = 0; i < filterColumns.length; i++) {
            query = query.filter(filterColumns[i], filterValues[i]);
        }
        return query.iterator();
    }
}
