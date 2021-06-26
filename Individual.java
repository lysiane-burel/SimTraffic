

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Individual implements Comparable<Individual>, Cloneable {
    private final ArrayList<Intersection> intersections;
    private float score;

    public ArrayList<Intersection> getIntersections() {
        return intersections;
    }

    public Individual(ArrayList intersections) {
        this.intersections = new ArrayList<>();
        for (Object intersection : intersections) {

            this.intersections.add(new Intersection((Element) intersection));
        }
        score=0;
    }

    public Individual(Individual individual1, Individual individual2, int intersectionSize) {
        score = 0;
        this.intersections = new ArrayList<>();
        for (int i = 0; i < intersectionSize; i++) {
            this.intersections.add(new Intersection(individual1.getIntersections().get(i), individual2.getIntersections().get(i)));
        }
    }

    public void mutate() {
        Random random = new Random();
        int numberModifiedIntersection = random.nextInt(intersections.size()+1);
        List<Integer> range = IntStream.rangeClosed(0, intersections.size() - 1).boxed().collect(Collectors.toList());
        Collections.shuffle(range);
        for (int i = 0; i < numberModifiedIntersection; i++) {
            
            intersections.get(range.get(i)).mutate();
        }
        this.score();
        // TODO essayer gaussienne - poisson*Bernouilli a incrementer
        // TODO coder en binaire et muter les bits

    }

    public void toXML() {
        try {
            DocumentBuilderFactory documentBuilderFactoryOut = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilderOut = documentBuilderFactoryOut.newDocumentBuilder();
            Document documentOut = documentBuilderOut.parse("myCarrefour.xml");
            for (Intersection i : intersections) {
                i.addToNetwork(documentOut);
            }
            TransformerFactory transformerFactoryOut = TransformerFactory.newInstance();
            Transformer transformerOut = transformerFactoryOut.newTransformer();
            DOMSource domSourceOut = new DOMSource(documentOut);
            String pathCarrefour = "Carrefour.net.xml";
            StreamResult streamResultOut = new StreamResult(new File(pathCarrefour));
            transformerOut.transform(domSourceOut, streamResultOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected Individual clone() throws CloneNotSupportedException {
        return (Individual) super.clone();
    }

    public void score() {
        this.toXML();
        try {
            Process p = Runtime.getRuntime().exec("python simulation.py");

            new Thread(new Runnable() {
                public void run() {
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    BufferedReader error= new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String line;
                    String errorLine;

                    try {
                        while ((line = input.readLine()) != null) {
                            //System.out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        while ((errorLine = input.readLine()) != null) {
                            //System.out.println(errorLine);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            p.waitFor();
            File speed=new File("speed.txt");
            Scanner myReader = new Scanner(speed);
            score=Float.parseFloat(myReader.nextLine());
            System.out.println(score);
            myReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void toCSV(String pathCSV){
        File csvOutputFile = new File(pathCSV);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            for(int i=0; i<intersections.size();i++){
                pw.println(intersections.get(i).toCSV());
            }
            pw.print(getScore());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public float getScore() {
        return score;
    }


    @Override
    public String toString() {
        StringBuilder outBuilder = new StringBuilder();
        for (Intersection i : intersections) {
            outBuilder.append(i.toString()).append("\n");
        }
        String out = outBuilder.toString();
        out += "";
        return out;
    }

    @Override
    public int compareTo(Individual o) {
        return this.score < o.getScore() ? 1 : -1;
    }
}
