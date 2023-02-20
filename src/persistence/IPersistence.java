package persistence;

import java.util.List;

/**
 * Interfaccia che identifica la persistenza generica
 *
 * @param <K> Classe della chiave di acesso agli oggetti della pesistenza
 * @param <T> Classe dell'oggetto persistito
 */
public interface IPersistence<K, T extends IPersistenceObject<K>> {

    /**
     * Inserimento oggetto (non presente) nella persistenza
     *
     * @param data: l'oggetto persistito
     * @throws IllegalArgumentException: chiave di accesso dell'oggetto inserito
     * presente nella persistenza (non possono esserci duplicati).
     */
    public void create(T data) throws IllegalArgumentException;

    /**
     * Lettura di un'oggetto mediante chiave di acccesso <code>key</code>
     *
     * @param key: la chiave di accesso utilizzata per la ricerca
     * @return l'oggetto associato alla chiave di accesso
     * @throws IllegalArgumentException: chiave di acesso non presente nella
     * persistenza
     */
    public T read(K key) throws IllegalArgumentException;

    /**
     * Modifica di un'oggetto
     *
     * @param data: l'oggetto modificato (identificato mediante la chiave di
     * accesso restituita dal metodo <code>data.getKey()</code>)
     * @throws IllegalArgumentException: oggetto non presente nella persistenza
     * (<code>data.getKey()</code> non trovato)
     */
    public void update(T data) throws IllegalArgumentException;

    /**
     * *
     * Rimozione di un'oggetto mediante chiave di acccesso <code>key</code>
     *
     * @param key: la chiave di accesso utilizzata per la ricerca
     * @throws IllegalArgumentException: chiave di acesso non presente nella
     * persistenza
     */
    public void delete(K key) throws IllegalArgumentException;

    /**
     * Lista completa degli oggetti persistiti
     *
     * @return lista degli oggetti persistiti
     */
    public List<T> getAll();
}
