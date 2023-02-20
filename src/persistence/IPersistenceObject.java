package persistence;

import java.io.Serializable;

/**
 * Interfaccia che identifica oggetti generici da persistere
 *
 * @param <K> Classe della chiave di acesso all'oggetto persistito
 */
public interface IPersistenceObject<K> extends Serializable, Cloneable {

    /**
     * Metodo utilizzato dalla persistenza per ottenere la chiave di accesso
     * impiegata per la ricerca dell'oggetto persistito
     *
     * @return Chiave di acesso (e che identifica) l'oggetto.
     */
    public K getKey();

    /**
     * Metodo utilizzato dalla persistenza per ottenere copie profonde di
     * oggetti persistiti al fine di evitare modifiche dovute a riferimenti o
     * altro
     *
     * @return Copia profonda dell'oggetto
     */
    public Object getClone();
}
