/**
 * @author Ondřej Doněk <ondrejd@gmail.com>
 * @link https://github.com/ondrejd/costs-javafx-app for the canonical source repository
 * @license https://www.gnu.org/licenses/gpl-3.0.en.html GNU General Public License 3.0
 */

package ondrejd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlDataSource {
    public static final String DEFAULT_WORK_PRICE = "80.0";
    public static final String DEFAULT_WIRE_PRICE = "20.0";
    public static final String DEFAULT_POUR_PRICE = "6.0";
    public static final String DEFAULT_PAINT_PRICE = "9.0";
    public static final String DEFAULT_SHEET_PRICE = "2.5";
    
    private static final MonthConstants[] constants = new MonthConstants[12];
    private static final ObservableList<CostDataRow> data = FXCollections.<CostDataRow>observableArrayList();

    public static MonthConstants[] getConstants() {
        return constants;
    }
    
    public static MonthConstants getConstants(int month) {
        return constants[month];
    }
    
    public static void setConstants(int month, MonthConstants consts) {
        constants[month] = consts;
    }
    
    /**
     * Load data from XML file.
     * @param year Year for which we want to load data
     * @return List of cost data rows.
     */
    public static ObservableList<CostDataRow> loadData(int year) {
        String fileName = "naklady-" + Integer.toString(year) + ".xml";
        File file = new File(fileName);
        
        if(!file.exists()) {
            createBlankFile(file);
        }
        
        try {
            loadXml(file);
        } catch(Exception e) {
            e.printStackTrace();
            
            data.clear();
            data.addAll(
                    new CostDataRow(0, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                    new CostDataRow(1, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                    new CostDataRow(2, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                    new CostDataRow(3, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                    new CostDataRow(4, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                    new CostDataRow(5, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                    new CostDataRow(6, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                    new CostDataRow(7, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                    new CostDataRow(8, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                    new CostDataRow(9, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                    new CostDataRow(10, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                    new CostDataRow(11, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            );
        }
        
        return data;
    }

    /**
     * Creates blank XML file.
     * @param file
     */
    private static void createBlankFile(File file) {
        try {
            // Create blank document
            Document doc = createBlankDoc();
            // Write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
    
    /**
     * @return Blank XML document.
     * @throws ParserConfigurationException 
     */
    private static Document createBlankDoc() throws ParserConfigurationException {
        // Create document
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        // Root element
        Element root = doc.createElement("costs");
        doc.appendChild(root);
        // Set year
        Attr rootAttr = doc.createAttribute("year");
        rootAttr.setValue(Integer.toString(CostsController.getCurrentYear()));
        root.setAttributeNode(rootAttr);
        // Create months
        for(int i = 0; i < 12; i++) {
            Element month = doc.createElement("month");
            // Month number attributte
            month.setAttribute("num", Integer.toString(i));
            // Month attributes with constants
            month.setAttribute("workCost", DEFAULT_WORK_PRICE);
            month.setAttribute("wireCost", DEFAULT_WIRE_PRICE);
            month.setAttribute("pourCost", DEFAULT_POUR_PRICE);
            month.setAttribute("paintCost", DEFAULT_PAINT_PRICE);
            month.setAttribute("sheetCost", DEFAULT_SHEET_PRICE);
            // Now create one blank row
            Element row = doc.createElement("row");
            // Set value attributes
            row.setAttribute("place", "");
            row.setAttribute("surface", "0");
            row.setAttribute("workPrice", "0");
            row.setAttribute("wireWeight", "0");
            row.setAttribute("wirePrice", "0");
            row.setAttribute("pourPrice", "0");
            row.setAttribute("paintPrice", "0");
            row.setAttribute("sheetPrice", "0");
            row.setAttribute("concretePrice", "0");
            row.setAttribute("pumpPrice", "0");
            row.setAttribute("squarePrice", "0");
            row.setAttribute("totalCosts", "0");
            row.setAttribute("billPrice", "0");
            row.setAttribute("gain", "0");
            row.setAttribute("profitMargin", "0");
            // Set color attributes
            row.setAttribute("placeColor", "N");
            row.setAttribute("surfaceColor", "N");
            row.setAttribute("costsColor", "N");
            row.setAttribute("workPriceColor", "N");
            row.setAttribute("wireWeightColor", "N");
            row.setAttribute("wirePriceColor", "N");
            row.setAttribute("pourPriceColor", "N");
            row.setAttribute("paintPriceColor", "N");
            row.setAttribute("sheetPriceColor", "N");
            row.setAttribute("concretePriceColor", "N");
            row.setAttribute("pumpPriceColor", "N");
            row.setAttribute("squarePriceColor", "N");
            row.setAttribute("totalCostsColor", "N");
            row.setAttribute("billPriceColor", "N");
            row.setAttribute("gainColor", "N");
            row.setAttribute("profitMarginColor", "G");
            // Append data row to month
            month.appendChild(row);
            // It's all - there are no default data
            root.appendChild(month);
        }

        return doc;
    }

    /**
     * Loads XML file.
     * @param file
     */
    private static void loadXml(File file) throws Exception {
        // Load document
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(file);
        // Optional, but recommended, read link below
        // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();
        // Currently we don't deal with root <year> element but we directly
        // accessing months
        NodeList monthNodes = doc.getElementsByTagName("month");
        // Go ghrough all the month elements
        for(int i = 0; i < monthNodes.getLength(); i++) {
            Element monthElm = (Element) monthNodes.item(i);
            // Month's number (starting from zero)
            int monthNum = Integer.parseInt(monthElm.getAttribute("num"));
            float workCost = Float.parseFloat(monthElm.getAttribute("workCost"));
            float wireCost = Float.parseFloat(monthElm.getAttribute("wireCost"));
            float pourCost = Float.parseFloat(monthElm.getAttribute("pourCost"));
            float paintCost = Float.parseFloat(monthElm.getAttribute("paintCost"));
            float sheetCost = Float.parseFloat(monthElm.getAttribute("sheetCost"));
            
            constants[monthNum] = new MonthConstants(workCost, wireCost, pourCost, paintCost, sheetCost);
            
            // Row nodes
            NodeList rowNodes = monthElm.getElementsByTagName("row");
            // Go through all the row elements
            for(int x = 0; x < rowNodes.getLength(); x++) {
                Element rowElm = (Element) rowNodes.item(x);
                // Create data row
                CostDataRow row = new CostDataRow(monthNum, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                row.setPlace(new ColoredValue<>(rowElm.getAttribute("place"), getColorForAttr(rowElm, "place")));
                row.setSurface(new ColoredValue<>(Integer.parseInt(rowElm.getAttribute("surface")), getColorForAttr(rowElm, "surface")));
                row.setWorkPrice(new ColoredValue<>(Integer.parseInt(rowElm.getAttribute("workPrice")), getColorForAttr(rowElm, "workPrice")));
                row.setWireWeight(new ColoredValue<>(Integer.parseInt(rowElm.getAttribute("wireWeight")), getColorForAttr(rowElm, "wireWeight")));
                row.setWirePrice(new ColoredValue<>(Integer.parseInt(rowElm.getAttribute("wirePrice")), getColorForAttr(rowElm, "wirePrice")));
                row.setPourPrice(new ColoredValue<>(Integer.parseInt(rowElm.getAttribute("pourPrice")), getColorForAttr(rowElm, "pourPrice")));
                row.setPaintPrice(new ColoredValue<>(Integer.parseInt(rowElm.getAttribute("paintPrice")), getColorForAttr(rowElm, "paintPrice")));
                row.setSheetPrice(new ColoredValue<>(Integer.parseInt(rowElm.getAttribute("sheetPrice")), getColorForAttr(rowElm, "sheetPrice")));
                row.setConcretePrice(new ColoredValue<>(Integer.parseInt(rowElm.getAttribute("concretePrice")), getColorForAttr(rowElm, "concretePrice")));
                row.setPumpPrice(new ColoredValue<>(Integer.parseInt(rowElm.getAttribute("pumpPrice")), getColorForAttr(rowElm, "pumpPrice")));
                row.setSquarePrice(new ColoredValue<>(Integer.parseInt(rowElm.getAttribute("squarePrice")), getColorForAttr(rowElm, "squarePrice")));
                row.setTotalCosts(new ColoredValue<>(Integer.parseInt(rowElm.getAttribute("totalCosts")), getColorForAttr(rowElm, "totalCosts")));
                row.setBillPrice(new ColoredValue<>(Integer.parseInt(rowElm.getAttribute("billPrice")), getColorForAttr(rowElm, "billPrice")));
                row.setGain(new ColoredValue<>(Integer.parseInt(rowElm.getAttribute("gain")), getColorForAttr(rowElm, "gain")));
                row.setProfitMargin(new ColoredValue<>(Integer.parseInt(rowElm.getAttribute("profitMargin")), ColoredValue.ColorType.GREEN));
                data.add(row);
            }
        }
    }
    
    /**
     * Save XML.
     * @param data Data we want to save.
     * @param year Year for which are data.
     */
    public static void saveXml(ObservableList<CostDataRow> data, int year) {
        try {
            String fileName = "naklady-" + Integer.toString(year) + ".xml";
            File file = new File(fileName);
            // Create document
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            // Root element
            Element root = doc.createElement("costs");
            doc.appendChild(root);
            // Set year
            Attr rootAttr = doc.createAttribute("year");
            rootAttr.setValue(Integer.toString(year));
            root.setAttributeNode(rootAttr);
            // Go through the months and construct the XML
            //int i = 0;
            for(int i = 0; i < 12; i++) {
                Element month = doc.createElement("month");
                // Month number attributte
                month.setAttribute("num", Integer.toString(i));
                // Month attributes with constants
                month.setAttribute("workCost", Float.toString(constants[i].getWorkPrice()));
                month.setAttribute("wireCost", Float.toString(constants[i].getWirePrice()));
                month.setAttribute("pourCost", Float.toString(constants[i].getPourPrice()));
                month.setAttribute("paintCost", Float.toString(constants[i].getPaintPrice()));
                month.setAttribute("sheetCost", Float.toString(constants[i].getSheetPrice()));
                // Go through all rows
                for(int x = 0; x < data.size(); x++) {
                    // TODO This is pretty time wasting!
                    if (data.get(x).getMonth() != i) {
                        continue;
                    }
                    Element row = doc.createElement("row");
                    // Set value attributes
                    row.setAttribute("place", data.get(x).getPlace().getValue());
                    row.setAttribute("surface", data.get(x).getSurface().getValue().toString());
                    row.setAttribute("workPrice", data.get(x).getWorkPrice().getValue().toString());
                    row.setAttribute("wireWeight", data.get(x).getWireWeight().getValue().toString());
                    row.setAttribute("wirePrice", data.get(x).getWirePrice().getValue().toString());
                    row.setAttribute("pourPrice", data.get(x).getPourPrice().getValue().toString());
                    row.setAttribute("paintPrice", data.get(x).getPaintPrice().getValue().toString());
                    row.setAttribute("sheetPrice", data.get(x).getSheetPrice().getValue().toString());
                    row.setAttribute("concretePrice", data.get(x).getConcretePrice().getValue().toString());
                    row.setAttribute("pumpPrice", data.get(x).getPumpPrice().getValue().toString());
                    row.setAttribute("squarePrice", data.get(x).getSquarePrice().getValue().toString());
                    row.setAttribute("totalCosts", data.get(x).getTotalCosts().getValue().toString());
                    row.setAttribute("billPrice", data.get(x).getBillPrice().getValue().toString());
                    row.setAttribute("gain", data.get(x).getGain().getValue().toString());
                    row.setAttribute("profitMargin", data.get(x).getProfitMargin().getValue().toString());
                    // Set color attributes
                    row.setAttribute("placeColor", colorToIdent(data.get(x).getPlace().getColor()));
                    row.setAttribute("surfaceColor", colorToIdent(data.get(x).getSurface().getColor()));
                    row.setAttribute("workPriceColor", colorToIdent(data.get(x).getWorkPrice().getColor()));
                    row.setAttribute("wireWeightColor", colorToIdent(data.get(x).getWireWeight().getColor()));
                    row.setAttribute("wirePriceColor", colorToIdent(data.get(x).getWirePrice().getColor()));
                    row.setAttribute("pourPriceColor", colorToIdent(data.get(x).getPourPrice().getColor()));
                    row.setAttribute("paintPriceColor", colorToIdent(data.get(x).getPaintPrice().getColor()));
                    row.setAttribute("sheetPriceColor", colorToIdent(data.get(x).getSheetPrice().getColor()));
                    row.setAttribute("concretePriceColor", colorToIdent(data.get(x).getConcretePrice().getColor()));
                    row.setAttribute("pumpPriceColor", colorToIdent(data.get(x).getPumpPrice().getColor()));
                    row.setAttribute("squarePriceColor", colorToIdent(data.get(x).getSquarePrice().getColor()));
                    row.setAttribute("totalCostsColor", colorToIdent(data.get(x).getTotalCosts().getColor()));
                    row.setAttribute("billPriceColor", colorToIdent(data.get(x).getBillPrice().getColor()));
                    row.setAttribute("gainColor", colorToIdent(data.get(x).getGain().getColor()));
                    row.setAttribute("profitMarginColor", "N");
                    // Append row element to the month element
                    month.appendChild(row);
                }
                // Append month to the root element
                root.appendChild(month);
            }
            // Backup file
            backupXml(file, year);
            // Write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    /**
     * @param elm Element where we look for color attribute.
     * @param attr Base name of attribute.
     * @return Returns color.
     */
    private static ColoredValue.ColorType getColorForAttr(Element elm, String attr) {
        if(elm.hasAttribute(attr + "Color")) {
            return identToColor(elm.getAttribute(attr + "Color"));
        } else {
            return ColoredValue.ColorType.NOCOLOR;
        }
    }

    /**
     * @param ident Color identifier ("R","Y","N").
     * @return Returns color.
     */
    private static ColoredValue.ColorType identToColor(String ident) {
        switch(ident) {
            case "G": return ColoredValue.ColorType.GREEN;
            case "Y": return ColoredValue.ColorType.YELLOW;
            case "R": return ColoredValue.ColorType.RED;
            case "W":
            default: return ColoredValue.ColorType.NOCOLOR;
        }
    }

    /**
     * @param color {@link ColoredValue.ColorType} we want to convert.
     * @return Color identifier.
     */
    private static String colorToIdent(ColoredValue.ColorType color) {
        if(color == ColoredValue.ColorType.GREEN) {
            return "G";
        } else if(color == ColoredValue.ColorType.RED) {
            return "R";
        } else if(color == ColoredValue.ColorType.YELLOW) {
            return "Y";
        } else {
            return "N";
        }
    }

    /**
     * Creates backup XML file (if needed).
     * @param file
     * @param year
     */
    private static void backupXml(File file, int year) {
        if(!file.exists()) {
            return;
        }

        String backupFileName = "naklady-" + year + ".xml.bak";
        File backupFile = new File(backupFileName);

        try {
            Files.copy(file.toPath(), backupFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
