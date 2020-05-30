package cz.vaneo.kiv.ir.InformationRetrieval.core;

import cz.vaneo.kiv.ir.InformationRetrieval.core.data.Document;
import cz.vaneo.kiv.ir.InformationRetrieval.core.data.Result;
import cz.vaneo.kiv.ir.InformationRetrieval.core.data.Topic;
import cz.vaneo.kiv.ir.InformationRetrieval.core.searching.Index;
import cz.vaneo.kiv.ir.InformationRetrieval.core.utils.IOUtils;
import cz.vaneo.kiv.ir.InformationRetrieval.core.utils.SerializedDataHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * @author tigi
 *
 * Třída slouží pro vyhodnocení vámi vytvořeného vyhledávače
 *
 */
public class TestTrecEval {

    static Logger log = LoggerFactory.getLogger(TestTrecEval.class);
    static final String OUTPUT_DIR = "./TREC";

    /**
     * Metoda vytvoří objekt indexu, načte data, zaindexuje je provede předdefinované dotazy a výsledky vyhledávání
     * zapíše souboru a pokusí se spustit evaluační skript.
     *
     * Na windows evaluační skript pravděpodbně nebude možné spustit. Pokud chcete můžete si skript přeložit a pak
     * by mělo být možné ho spustit.
     *
     * Pokud se váme skript nechce překládat/nebo se vám to nepodaří. Můžete vygenerovaný soubor s výsledky zkopírovat a
     * spolu s přiloženým skriptem spustit (přeložit) na
     * Linuxu např. pomocí vašeho účtu na serveru ares.fav.zcu.cz
     *
     * Metodu není třeba měnit kromě řádků označených T O D O  - tj. vytvoření objektu třídy {@link Index} a
     */
    public static void main(String args[]) throws IOException {


        //TODO zde vytvořte objekt vaší implementované třídy Index
        Index index = new Index();

        List<Topic> topics = SerializedDataHelper.loadTopic(new File(OUTPUT_DIR + "/topicData.bin"));

        File serializedData = new File(OUTPUT_DIR + "/czechData.bin");

        List<Document> documents = new ArrayList<Document>();
        log.info("load");
        try {
            if (serializedData.exists()) {
                documents = SerializedDataHelper.loadDocument(serializedData);
            } else {
                log.error("Cannot find " + serializedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Documents: " + documents.size());
        index.index(documents);


        List<String> lines = new ArrayList<String>();

        for (Topic t : topics) {
            //TODO vytvoření dotazu, třída Topic představuje dotaz pro vyhledávání v zaindexovaných dokumentech
            //a obsahuje tři textová pole title, description a narrative. To jak sestavíte dotaz je na Vás a pravděpodobně
            //to ovlivní výsledné vyhledávání - zkuste změnit a uvidíte jaký MAP (Mean Average Precision) dostanete pro jednotlivé
            //kombinace např. pokud budete vyhledávat jen pomocí title (t.getTitle()) nebo jen pomocí description (t.getDescription())
            //nebo jejich kombinací (t.getTitle() + " " + t.getDescription())
            List<Result> resultHits = index.search(t.getTitle() + " " + t.getDescription() + " " + t.getNarrative());

            Comparator<Result> cmp = new Comparator<Result>() {
                public int compare(Result o1, Result o2) {
                    if (o1.getScore() > o2.getScore()) return -1;
                    if (o1.getScore() == o2.getScore()) return 0;
                    return 1;
                }
            };

            Collections.sort(resultHits, cmp);
            for (Result r : resultHits) {
                final String line = r.toString(t.getId());
                lines.add(line);
            }
            if (resultHits.size() == 0) {
                lines.add(t.getId() + " Q0 " + "abc" + " " + "99" + " " + 0.0 + " runindex1");
            }
        }
        final File outputFile = new File(OUTPUT_DIR + "/results " + SerializedDataHelper.SDF.format(System.currentTimeMillis()) + ".txt");
        IOUtils.saveFile(outputFile, lines);
        //try to run evaluation
        try {
            runTrecEval(outputFile.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String runTrecEval(String predictedFile) throws IOException {

        String commandLine = "./trec_eval.8.1/./trec_eval" +
                " ./trec_eval.8.1/czech" +
                " " + predictedFile;

        System.out.println(commandLine);
        Process process = Runtime.getRuntime().exec(commandLine);

        BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String trecEvalOutput;
        StringBuilder output = new StringBuilder("TREC EVAL output:\n");
        for (String line; (line = stdout.readLine()) != null; ) output.append(line).append("\n");
        trecEvalOutput = output.toString();
        System.out.println(trecEvalOutput);

        int exitStatus = 0;
        try {
            exitStatus = process.waitFor();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        System.out.println(exitStatus);

        stdout.close();
        stderr.close();

        return trecEvalOutput;
    }
}
