# Persistence 
Interfaccie e classi generiche per persistere oggett in java

## Funzionamento
L'interfaccia IPersistence offre i metodi [CRUDL](https://it.wikipedia.org/wiki/CRUD). La oggetti persistiti vengono gestiti mediante una chiave di accesso che gli identifica e al fine di evitare passaggi a metodi per riferimento, alternando involontariamente il lro stato, questi vengono copiati prima di effettuare modifiche e salvataggi.

Dunque gli oggetti da persistere devono implementare un'interfaccia che offra queste funzionalità, allora  l'interfaccia [`IPersistenceObject`](src/persistence/IPersistenceObject.java) diventa:

```java
public interface IPersistenceObject<K> extends Serializable, Cloneable {

    /** ... */
    public K getKey();

    /** ... */
    public Object getClone();
}

```

L'interfaccia per [`IPersistence`](src/persistence/IPersistence.java) diventa:
```java
public interface IPersistence<K, T extends IPersistenceObject<K>> {

    /** ... */
    public void create(T data) throws IllegalArgumentException;

    /** ... */
    public T read(K key) throws IllegalArgumentException;

    /** ... */
    public void update(T data) throws IllegalArgumentException;

    /** ... */
    public void delete(K key) throws IllegalArgumentException;

    /** ... */
    public List<T> getAll();
}
```

L'interfaccia [IPersistence](src/persistence/IPersistence.java) prevede l'implementazione dei metodi CRUDL, dunque l'operazione potrebbe risultare ripetitiva... Al fine di mantenere le generiche per la persistenza, si realizzano classi astratte che utilizzano una tipologia di sorgente della persistenza (Lista, Mappa,Database ...) specificate dalle classi figlie e ne implementa le funzioni CRUDL relative.

Ad esempio, una persistenza generica su file  ([PersistenceOnFile.java](src/persistence/PersistenceOnFile.java)) diventa:
```java
public abstract class PersistenceOnFile<K, T extends IPersistenceObject<K>> implements IPersistence<K, T> {

    private final String FILE_PATH;
    private final List<T> persistence;

    /** ...  */
    public PersistenceOnFile(String path) {
        this.FILE_PATH = path;
        persistence = getPersistence(FILE_PATH);
    }

    
    /** Specifica da parte della classe figlia  della sorgente concreta utilizzata come persistenza */
    protected abstract List<T> getPersistence(String path);

    /** ...  */
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

    /** ...  */
    @Override
    public T read(K key) throws IllegalArgumentException {
        T item = getItem(key);
        if (item == null) {
            throw new IllegalArgumentException("Invalid Key");
        }

        return (T) item.getClone();
    }

    /** ...  */
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

    /** ...  */
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

    /** ...  */
    @Override
    public List<T> getAll() {
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new FileInputStream(FILE_PATH));
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            return null;
        }
    }

    /** ...  */
    private T getItem(K key) {
        for (T item : persistence) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }

        return null;
    }

    
    /** ...  */
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
```

## Requisiti
- importare il file [`Persistence.jar`](dist/Persistence.jar)

L'oggetto da persistere deve rispettare le seguenti condizioni:
- implementare l'interfaccia [`IPersistenceObject<K>`](src/persistence/IPersistenceObject.java) dove `K` è la classe della chiave di accesso con cui l'oggetto persistito viene identificato.

La persistenza deve rispettare le seguenti condizioni:
- implementare l'interfaccia [`IPersistence<K, T>`](src/persistence/IPersistence.java) dove `K` è la classe della chiave di accesso delll'oggetto persistito e `T` è la classe dell'oggetto persistito.

### Esempio di utilizzo

```java
public class PointPersistence extends PersistenceOnFile<Integer, Point> {

    /** Sorgente persistenza: utilizzando classi concrete, permette che l'oggetto sia statico */
    private static List<Point> persistence; 

    public PointPersistence(String path) {
        super(path);
    }

    /** Metodo chiamato dalla classe madre (PersistenceOnFile) per impostare la sorgente della persistenza*/
    @Override
    protected List<Point> getPersistence(String path) {
        persistence = (List<Point>) listFromFile(path);
        return persistence;
    }
}
```
La libreria dispone del metodo [`listFromFile`](src/utilities/FileUtility.java) nel package [utilities](src/utilities/).

```java
public class Point implements IPersistenceObject<Integer> {
    private static final long serialVersionUID = 1L;
    
    private int x;
    private int y;

    /** ... */

    @Override
    public Integer getKey() {
        return x;
    }

    @Override
    public Object getClone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }
}
```
L'oggetto da persistere implementa l'interfaccia [`IPersistenceObject`](src/persistence/IPersistenceObject.java)


```java
    PointPersistence p = new PointPersistence("./points.dat");
    p.create(new Point(1, 2));
    p.create(new Point(3, 2));
    p.create(new Point(4, 2));

    Point read = p.read(1);
    System.out.println(read);

    List<Point> all = p.getAll();
    all.forEach(System.out::println);
```
> Point{x= 1; y= 2;};
> Point{x= 1; y= 2;};
> Point{x= 3; y= 2;};
> Point{x= 4; y= 2;};