/**
 * @author Ondřej Doněk <ondrejd@gmail.com>
 * @link https://github.com/ondrejd/costs-java-app for the canonical source repository
 * @license https://www.gnu.org/licenses/gpl-3.0.en.html GNU General Public License 3.0
 */

package ondrejd;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

/**
 * Class that handles our XML data source (plain XML file located in 
 * the directory where application's JAR is).
 * @author Ondřej Doněk <ondrejd@gmail.com>
 */
public class XmlDataSource {
    public static final String DEFAULT_WORK_PRICE = "80.0";
    public static final String DEFAULT_WIRE_PRICE = "20.0";
    public static final String DEFAULT_POUR_PRICE = "6.0";
    public static final String DEFAULT_PAINT_PRICE = "9.0";
    public static final String DEFAULT_SHEET_PRICE = "2.5";
    
    private int year;
    private String fileName;
    private File file;

    private Object[][] constants;/*12*5*/
    private Object[][][] data;/*12*25*15*/
    private Color[][][] colors;/*12*25*15*/

    /**
     * Constructor.
     * @param year Year for which we want the datasource.
     */
    public XmlDataSource(int year) {
        this.year = year;
        fileName = "naklady-" + Integer.toString(this.year) + ".xml";
        file = new File(fileName);
        load();
    }

    /**
     * @param month Requested month.
     * @return Data for given month.
     */
    public Object[][] getData(int month) {
        return data[month];
    }
    
    /**
     * @param month Requested month.
     * @return Constants for given month.
     */
    public Object[] getConstants(int month) {
        return constants[month];
    }

    /**
     * @return Colors for whole data set.
     */
    public Color[][][] getColors() {
        return colors;
    }
    
    /**
     * Loads XML data source file. If XML file doesn't exist yet we create it.
     */
    private void load() {
        if(!file.exists()) {
            createBlankFile();
        }

        loadXml();
    }

    /**
     * Loads XML file.
     */
    private void loadXml() {
        try {
            // Initialize constants and data
            constants = new Object[12][5];
            data = new Object[12][25][15];
            colors = new Color[12][25][15];
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
                constants[monthNum][0] = monthElm.getAttribute("workCost");
                constants[monthNum][1] = monthElm.getAttribute("wireCost");
                constants[monthNum][2] = monthElm.getAttribute("pourCost");
                constants[monthNum][3] = monthElm.getAttribute("paintCost");
                constants[monthNum][4] = monthElm.getAttribute("sheetCost");
                // Row nodes
                NodeList rowNodes = monthElm.getElementsByTagName("row");
                // Go through all the row elements
                for(int x = 0; x < rowNodes.getLength(); x++) {
                    Element rowElm = (Element) rowNodes.item(x);
                    // Get all data (attributes)
                    data[monthNum][x][0] = rowElm.getAttribute("place");
                    data[monthNum][x][1] = rowElm.getAttribute("surface");
                    data[monthNum][x][2] = rowElm.getAttribute("workPrice");
                    data[monthNum][x][3] = rowElm.getAttribute("wireWeight");
                    data[monthNum][x][4] = rowElm.getAttribute("wirePrice");
                    data[monthNum][x][5] = rowElm.getAttribute("pourPrice");
                    data[monthNum][x][6] = rowElm.getAttribute("paintPrice");
                    data[monthNum][x][7] = rowElm.getAttribute("sheetPrice");
                    data[monthNum][x][8] = rowElm.getAttribute("concretePrice");
                    data[monthNum][x][9] = rowElm.getAttribute("pumpPrice");
                    data[monthNum][x][10] = rowElm.getAttribute("squarePrice");
                    data[monthNum][x][11] = rowElm.getAttribute("totalCosts");
                    data[monthNum][x][12] = rowElm.getAttribute("billPrice");
                    data[monthNum][x][13] = rowElm.getAttribute("gain");
                    data[monthNum][x][14] = rowElm.getAttribute("profitMargin");
                    // Get colors
                    colors[monthNum][x][0] = getColorForAttr(rowElm, "place");
                    colors[monthNum][x][1] = getColorForAttr(rowElm, "surface");
                    colors[monthNum][x][2] = getColorForAttr(rowElm, "workPrice");
                    colors[monthNum][x][3] = getColorForAttr(rowElm, "wireWeight");
                    colors[monthNum][x][4] = getColorForAttr(rowElm, "wirePrice");
                    colors[monthNum][x][5] = getColorForAttr(rowElm, "pourPrice");
                    colors[monthNum][x][6] = getColorForAttr(rowElm, "paintPrice");
                    colors[monthNum][x][7] = getColorForAttr(rowElm, "sheetPrice");
                    colors[monthNum][x][8] = getColorForAttr(rowElm, "concretePrice");
                    colors[monthNum][x][9] = getColorForAttr(rowElm, "pumpPrice");
                    colors[monthNum][x][10] = getColorForAttr(rowElm, "squarePrice");
                    colors[monthNum][x][11] = getColorForAttr(rowElm, "totalCosts");
                    colors[monthNum][x][12] = getColorForAttr(rowElm, "billPrice");
                    colors[monthNum][x][13] = getColorForAttr(rowElm, "gain");
                    colors[monthNum][x][14] = getColorForAttr(rowElm, "profitMargin");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param rowElm Element where we look for color attribute.
     * @param attrName Base name of attribute.
     * @return Returns correct {@link java.awt.Color}.
     */
    private Color getColorForAttr(Element elm, String attr) {
        if(elm.hasAttribute(attr + "Color")) {
            return getColorForIdent(elm.getAttribute(attr + "Color"));
        } else {
            return Color.WHITE;
        }
    }

    /**
     * @param ident Color identifier ("R","Y","W").
     * @return Returns correct {@link java.awt.Color}.
     */
    private Color getColorForIdent(String ident) {
        switch(ident) {
            case "Y": return Color.YELLOW;
            case "R": return Color.RED;
            case "W":
            default: return Color.WHITE;
        }
    }
    
    /**
     * @return Blank XML document.
     * @throws ParserConfigurationException 
     */
    private Document createBlankDoc() throws ParserConfigurationException {
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
            // Now create the rows
            for(int x = 0; x < 25; x++) {
                Element row = doc.createElement("row");
                // Set value attributes
                row.setAttribute("place", "");
                row.setAttribute("surface", "0 m2");
                row.setAttribute("workPrice", "0 Kč");
                row.setAttribute("wireWeight", "0 kg");
                row.setAttribute("wirePrice", "0 Kč");
                row.setAttribute("pourPrice", "0 Kč");
                row.setAttribute("paintPrice", "0 Kč");
                row.setAttribute("sheetPrice", "0 Kč");
                row.setAttribute("concretePrice", "0 Kč");
                row.setAttribute("pumpPrice", "0 Kč");
                row.setAttribute("squarePrice", "0 Kč");
                row.setAttribute("totalCosts", "0 Kč");
                row.setAttribute("billPrice", "0 Kč");
                row.setAttribute("gain", "0 Kč");
                row.setAttribute("profitMargin", "0 %");
                // Set color attributes
                row.setAttribute("placeColor", "W");
                row.setAttribute("surfaceColor", "W");
                row.setAttribute("workPriceColor", "W");
                row.setAttribute("wireWeightColor", "W");
                row.setAttribute("wirePriceColor", "W");
                row.setAttribute("pourPriceColor", "W");
                row.setAttribute("paintPriceColor", "W");
                row.setAttribute("sheetPriceColor", "W");
                row.setAttribute("concretePriceColor", "W");
                row.setAttribute("pumpPriceColor", "W");
                row.setAttribute("squarePriceColor", "W");
                row.setAttribute("totalCostsColor", "W");
                row.setAttribute("billPriceColor", "W");
                row.setAttribute("gainColor", "W");
                row.setAttribute("profitMarginColor", "W");
                // Append data row to month
                month.appendChild(row);
            }
            // It's all - there are no default data
            root.appendChild(month);
        }

        return doc;
    }

    /**
     * Creates blank XML file.
     */
    private void createBlankFile() {
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
     * Saves data source.
     * @param constants
     * @param dataModels
     */
    public void save(Object[][] constants, javax.swing.table.TableModel[] dataModels, Color[][][] colors) {
        try {
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
                month.setAttribute("workCost", constants[i][0].toString());
                month.setAttribute("wireCost", constants[i][1].toString());
                month.setAttribute("pourCost", constants[i][2].toString());
                month.setAttribute("paintCost", constants[i][3].toString());
                month.setAttribute("sheetCost", constants[i][4].toString());
                // Go through all rows
                for(int x = 0; x < 25/*dataModels[i].getRowCount()*/; x++) {
                    Element row = doc.createElement("row");
                    // Set value attributes
                    row.setAttribute("place", dataModels[i].getValueAt(x, 0).toString());
                    row.setAttribute("surface", dataModels[i].getValueAt(x, 1).toString());
                    row.setAttribute("workPrice", dataModels[i].getValueAt(x, 2).toString());
                    row.setAttribute("wireWeight", dataModels[i].getValueAt(x, 3).toString());
                    row.setAttribute("wirePrice", dataModels[i].getValueAt(x, 4).toString());
                    row.setAttribute("pourPrice", dataModels[i].getValueAt(x, 5).toString());
                    row.setAttribute("paintPrice", dataModels[i].getValueAt(x, 6).toString());
                    row.setAttribute("sheetPrice", dataModels[i].getValueAt(x, 7).toString());
                    row.setAttribute("concretePrice", dataModels[i].getValueAt(x, 8).toString());
                    row.setAttribute("pumpPrice", dataModels[i].getValueAt(x, 9).toString());
                    row.setAttribute("squarePrice", dataModels[i].getValueAt(x, 10).toString());
                    row.setAttribute("totalCosts", dataModels[i].getValueAt(x, 11).toString());
                    row.setAttribute("billPrice", dataModels[i].getValueAt(x, 12).toString());
                    row.setAttribute("gain", dataModels[i].getValueAt(x, 13).toString());
                    row.setAttribute("profitMargin", dataModels[i].getValueAt(x, 14).toString());
                    // Set color attributes
                    row.setAttribute("placeColor", getColorIdentFromColor(colors[i][x][0]));
                    row.setAttribute("surfaceColor", getColorIdentFromColor(colors[i][x][1]));
                    row.setAttribute("workPriceColor", getColorIdentFromColor(colors[i][x][2]));
                    row.setAttribute("wireWeightColor", getColorIdentFromColor(colors[i][x][3]));
                    row.setAttribute("wirePriceColor", getColorIdentFromColor(colors[i][x][4]));
                    row.setAttribute("pourPriceColor", getColorIdentFromColor(colors[i][x][5]));
                    row.setAttribute("paintPriceColor", getColorIdentFromColor(colors[i][x][6]));
                    row.setAttribute("sheetPriceColor", getColorIdentFromColor(colors[i][x][7]));
                    row.setAttribute("concretePriceColor", getColorIdentFromColor(colors[i][x][8]));
                    row.setAttribute("pumpPriceColor", getColorIdentFromColor(colors[i][x][9]));
                    row.setAttribute("squarePriceColor", getColorIdentFromColor(colors[i][x][10]));
                    row.setAttribute("totalCostsColor", getColorIdentFromColor(colors[i][x][11]));
                    row.setAttribute("billPriceColor", getColorIdentFromColor(colors[i][x][12]));
                    row.setAttribute("gainColor", getColorIdentFromColor(colors[i][x][13]));
                    row.setAttribute("profitMarginColor", getColorIdentFromColor(colors[i][x][14]));
                    // Append row element to the month element
                    month.appendChild(row);
                }
                // Append month to the root element
                root.appendChild(month);
            }
            // Backup file
            backupXml();
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
     * @param color {@link java.awt.Color} we want to convert.
     * @return Color identifier.
     */
    private String getColorIdentFromColor(Color color) {
        if(color == Color.RED) {
            return "R";
        }
        else if(color == Color.YELLOW) {
            return "Y";
        }
        else {
            return "W";
        }
    }

    /**
     * Creates backup XML file (if needed).
     */
    private void backupXml() {
        if(!file.exists()) {
            return;
        }

        String backupFileName = "naklady-" + Integer.toString(this.year) + ".xml.bak";
        File backupFile = new File(backupFileName);

        try {
            Files.copy(file.toPath(), backupFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * @return Default constants for single month.
     */
    private Object[] getDefaultConstants() {
        return new Object[] {
            DEFAULT_WORK_PRICE, DEFAULT_WIRE_PRICE, DEFAULT_POUR_PRICE,
            DEFAULT_PAINT_PRICE, DEFAULT_SHEET_PRICE
        };
    }

    /**
     * @return Blank data for our table and for single month.
     */
    private Object[][] getBlankMonthData() {
        return new Object[][] {
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"},
            {"", "0 m2", "0 Kč", "0 kg", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 Kč", "0 %"}
        };
    }

    /**
     * @return Default colors for our table and for single month.
     */
    private Color[][] getDefaultColors() {
        return new Color[][] {
            {Color.WHITE, Color.WHITE, Color.YELLOW, Color.RED, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE},
            {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE}
        };
    }
}
