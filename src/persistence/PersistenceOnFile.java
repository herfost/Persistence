package persistence;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe astratta che identifica persistenze su file
 *
 * @param <K> Classe della chiave di acesso agli oggetti della pesistenza
 * @param <T> Classe dell'oggetto persistito
 */
public abstract class PersistenceOnFile<K, T extends IPersistenceObject<K>> implements IPersistence<K, T> {

    private final String FILE_PATH;
    private final List<T> persistence;

    /**
     * Caricamento della persistenza da file
     *
     * @param path: Percorso al file
     */
    public PersistenceOnFile(String path) {
        this.FILE_PATH = path;
        persistence = getPersistence(FILE_PATH);
    }

    /**
     * Specifica della sorgente concreta utilizzata come persistenza
     *
     * @param path: percorso al file
     * @return riferimento alla sorgente concreta
     */
    protected abstract List<T> getPersistence(String path);

    /**
     * Inserimento oggetto (non presente) nella persistenza
     *
     * @param data: l'oggetto persistito
     * @throws IllegalArgumentException: chiave di accesso dell'oggetto inserito
     * presente nella persistenza (non possono esserci duplicati).
     */
    @Override
    public void create(T data) throws IllegalArgumentException {
        T item = null;
        try {
            item = read(data.getKey());
        } catch (IllegalArgumentException ex) {
            T dataClone = (T) data.getClone();

            persistence.add(dataClone);
            writeChanges();
        }

        if (item != null) {
            throw new IllegalArgumentException("Key already been used: @key = " + item.getKey());
        }
    }

    /**
     * Lettura di un'oggetto mediante chiave di acccesso <code>key</code>
     *
     * @param key: la chiave di accesso utilizzata per la ricerca
     * @return l'oggetto associato alla chiave di accesso
     * @throws IllegalArgumentException: chiave di acesso non presente nella
     * persistenza
     */
    @Override
    public T read(K key) throws IllegalArgumentException {
        T item = getItem(key);
        if (item == null) {
            throw new IllegalArgumentException("Invalid Key");
        }

        return (T) item.getClone();
    }

    /**
     * Modifica di un'oggetto
     *
     * @param data: l'oggetto modificato (identificato mediante la chiave di
     * accesso restituita dal metodo <code>data.getKey()</code>)
     * @throws IllegalArgumentException: oggetto non presente nella persistenza
     * (<code>data.getKey()</code> non trovato)
     */
    @Override
    public void update(T data) throws IllegalArgumentException {
        T _data = getItem(data.getKey());
        if (_data == null) {
            throw new IllegalArgumentException("Invalid Key");
        }

        Integer dataIndex = persistence.indexOf(_data);
        T dataClone = (T) data.getClone();

        persistence.set(dataIndex, dataClone);
        writeChanges();
    }

    /**
     * *
     * Rimozione di un'oggetto mediante chiave di acccesso <code>key</code>
     *
     * @param key: la chiave di accesso utilizzata per la ricerca
     * @throws IllegalArgumentException: chiave di acesso non presente nella
     * persistenza
     */
    @Override
    public void delete(K key) throws IllegalArgumentException {
        T data = getItem(key);
        if (data == null) {
            throw new IllegalArgumentException("Invalid Key");
        }

        K dataKey = data.getKey();
        T persistenceItem = getItem(dataKey);

        persistence.remove(persistenceItem);
        writeChanges();
    }

    /**
     * Lista completa degli oggetti persistiti
     *
     * @return lista degli oggetti persistiti
     */
    @Override
    public List<T> getAll() {
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new FileInputStream(FILE_PATH));
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            return null;
        }

        /*
        List<T> copy = new ArrayList<>();
        persistence.forEach((T item) -> {
        copy.add((T) item.getClone());
        });
        return copy;
         */
    }

    /**
     * Lettura riferimento di un oggetto all'interno della sorgente concreta
     * mediante chiave di acccesso <code>key</code>
     *
     * @param key: * @param key: la chiave di accesso utilizzata per la ricerca
     * @return riferimento all'oggetto in ricerca
     */
    private T getItem(K key) {
        for (T item : persistence) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        persistence.forEach(item -> {
            builder.append(item.toString()).append('\n');
        });

        return builder.toString();
    }

    /**
     * Scrittura dello stato della persistenza nel file associato alla
     * persistenza (<code>FILE_PATH</code>)
     */
    private void writeChanges() {
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH));
            oos.writeObject(persistence);
        } catch (FileNotFoundException ex1) {
            Logger.getLogger(PersistenceOnFile.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (IOException ex1) {
            Logger.getLogger(PersistenceOnFile.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }
}
