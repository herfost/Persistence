/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

final public class FileUtility {

    /**
     *
     * @param path percorso al file di lettura
     * @return lista letta della dal file: effettuare il castings
     */
    public final static Object listFromFile(String path) {
        Object list;
        ObjectInputStream ois;

        try {
            ois = new ObjectInputStream(new FileInputStream(path));
            list = ois.readObject();
        } catch (FileNotFoundException ex) {
            list = new ArrayList<>();
        } catch (IOException | ClassNotFoundException ex) {
            list = new ArrayList<>();
        }

        return list;
    }
}
