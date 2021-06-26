import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public class Coordinate {
    private static NodeList lights;

    public static void init(String pathNode) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(pathNode));
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("//*[@type='traffic_light']");
            lights = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getXY(String id) {
        String stringID = String.valueOf(id);
        for (int i = 0; i < lights.getLength(); i++) {
            Node node = lights.item(i);
            Element elementTest = (Element) node;
            if (elementTest.getAttribute("id").equals(stringID) ){
                String x = elementTest.getAttribute("x");
                String y = elementTest.getAttribute("y");
                return x + "," + y;
            }
        }
        return null;
    }

}

