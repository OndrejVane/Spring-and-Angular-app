package cz.vaneo.kiv.ir.InformationRetrieval.core.utils;


import cz.vaneo.kiv.ir.InformationRetrieval.controller.Controller;
import cz.vaneo.kiv.ir.InformationRetrieval.core.data.Document;
import cz.vaneo.kiv.ir.InformationRetrieval.core.data.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author tigi
 * <p>
 * Tuto třídu neměnte, pro vypracování semestrální práce není potřeba.
 */
public class SerializedDataHelper {

    public static final java.text.DateFormat SDF = new SimpleDateFormat("yyyy-MM-dd_HH_mm_SS");
    static Logger log = LoggerFactory.getLogger(Controller.class);

    public static List<Document> loadDocument(File serializedFile) {
        final Object object;
        try {
            final ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(serializedFile));
            object = objectInputStream.readObject();
            objectInputStream.close();
            List map = (List) object;
            if (!map.isEmpty() && map.get(0) instanceof Document) {
                return (List<Document>) object;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }


    public static void saveDocument(File outputFile, List<Document> data) {
        // save data
        try {
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(outputFile));
            objectOutputStream.writeObject(data);
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Data saved to " + outputFile.getPath());
    }

    static public List<Topic> loadTopic(File serializedFile) {
        final Object object;
        try {
            final ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(serializedFile));
            object = objectInputStream.readObject();
            objectInputStream.close();
            List map = (List) object;
            if (!map.isEmpty() && map.get(0) instanceof Topic) {
                return (List<Topic>) object;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    public static void saveTopic(File outputFile, List<Topic> data) {
        // save data
        try {
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(outputFile));
            objectOutputStream.writeObject(data);
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Data saved to " + outputFile.getPath());
    }
}
