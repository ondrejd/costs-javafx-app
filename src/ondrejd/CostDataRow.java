/**
 * @author Ondřej Doněk <ondrejd@gmail.com>
 * @link https://github.com/ondrejd/costs-javafx-app for the canonical source repository
 * @license https://www.gnu.org/licenses/gpl-3.0.en.html GNU General Public License 3.0
 */

package ondrejd;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CostDataRow {
    private final int month;
    private final ObjectProperty<ColoredValue<String>> place = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<Integer>> surface = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<Integer>> workPrice = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<Integer>> wireWeight = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<Integer>> wirePrice = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<Integer>> pourPrice = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<Integer>> paintPrice = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<Integer>> sheetPrice = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<Integer>> concretePrice = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<Integer>> pumpPrice = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<Integer>> squarePrice = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<Integer>> totalCosts = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<Integer>> billPrice = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<Integer>> gain = new SimpleObjectProperty<>();
    private final ObjectProperty<ColoredValue<Integer>> profitMargin = new SimpleObjectProperty<>();
    
    public CostDataRow(int month, String place, int surface, int workPrice, 
            int wireWeight, int wirePrice, int pourPrice, int paintPrice, 
            int sheetPrice, int concretePrice, int pumpPrice, int squarePrice, 
            int totalCosts, int billPrice, int gain, int profitMargin) {
        this.month = month;
        setPlace(new ColoredValue<>(place));
        setSurface(new ColoredValue<>(surface));
        setWorkPrice(new ColoredValue<>(workPrice));
        setWireWeight(new ColoredValue<>(wireWeight));
        setWirePrice(new ColoredValue<>(wirePrice));
        setPourPrice(new ColoredValue<>(pourPrice));
        setPaintPrice(new ColoredValue<>(paintPrice));
        setSheetPrice(new ColoredValue<>(sheetPrice));
        setConcretePrice(new ColoredValue<>(concretePrice));
        setPumpPrice(new ColoredValue<>(pumpPrice));
        setSquarePrice(new ColoredValue<>(squarePrice));
        setTotalCosts(new ColoredValue<>(totalCosts));
        setBillPrice(new ColoredValue<>(billPrice));
        setGain(new ColoredValue<>(gain));
        setProfitMargin(new ColoredValue<>(profitMargin));
    }
    
    public final int getMonth() {
        return this.month;
    }
    
    //place
    public final ObjectProperty<ColoredValue<String>> placeProperty() {
        return this.place;
    }
    
    public final ColoredValue<String> getPlace() {
        return this.placeProperty().get();
    }
    
    public final void setPlace(final ColoredValue<String> place) {
        this.placeProperty().set(place);
    }
    
    //surface
    public final ObjectProperty<ColoredValue<Integer>> surfaceProperty() {
        return this.surface;
    }
    
    public final ColoredValue<Integer> getSurface() {
        return this.surfaceProperty().get();
    }
    
    public final void setSurface(final ColoredValue<Integer> surface) {
        this.surfaceProperty().set(surface);
    }
    
    //workPrice
    public final ObjectProperty<ColoredValue<Integer>> workPriceProperty() {
        return this.workPrice;
    }
    
    public final ColoredValue<Integer> getWorkPrice() {
        return this.workPriceProperty().get();
    }
    
    public final void setWorkPrice(final ColoredValue<Integer> workPrice) {
        this.workPriceProperty().set(workPrice);
    }
    
    //wireWeight
    public final ObjectProperty<ColoredValue<Integer>> wireWeightProperty() {
        return this.wireWeight;
    }
    
    public final ColoredValue<Integer> getWireWeight() {
        return this.wireWeightProperty().get();
    }
    
    public final void setWireWeight(final ColoredValue<Integer> wireWeight) {
        this.wireWeightProperty().set(wireWeight);
    }
    
    //wirePrice
    public final ObjectProperty<ColoredValue<Integer>> wirePriceProperty() {
        return this.wirePrice;
    }
    
    public final ColoredValue<Integer> getWirePrice() {
        return this.wirePriceProperty().get();
    }
    
    public final void setWirePrice(final ColoredValue<Integer> wirePrice) {
        this.wirePriceProperty().set(wirePrice);
    }
    
    //pourPrice
    public final ObjectProperty<ColoredValue<Integer>> pourPriceProperty() {
        return this.pourPrice;
    }
    
    public final ColoredValue<Integer> getPourPrice() {
        return this.pourPriceProperty().get();
    }
    
    public final void setPourPrice(final ColoredValue<Integer> pourPrice) {
        this.pourPriceProperty().set(pourPrice);
    }
    
    //paintPrice
    public final ObjectProperty<ColoredValue<Integer>> paintPriceProperty() {
        return this.paintPrice;
    }
    
    public final ColoredValue<Integer> getPaintPrice() {
        return this.paintPriceProperty().get();
    }
    
    public final void setPaintPrice(final ColoredValue<Integer> paintPrice) {
        this.paintPriceProperty().set(paintPrice);
    }
    
    //sheetPrice
    public final ObjectProperty<ColoredValue<Integer>> sheetPriceProperty() {
        return this.sheetPrice;
    }
    
    public final ColoredValue<Integer> getSheetPrice() {
        return this.sheetPriceProperty().get();
    }
    
    public final void setSheetPrice(final ColoredValue<Integer> sheetPrice) {
        this.sheetPriceProperty().set(sheetPrice);
    }
    
    //concretePrice
    public final ObjectProperty<ColoredValue<Integer>> concretePriceProperty() {
        return this.concretePrice;
    }
    
    public final ColoredValue<Integer> getConcretePrice() {
        return this.concretePriceProperty().get();
    }
    
    public final void setConcretePrice(final ColoredValue<Integer> concretePrice) {
        this.concretePriceProperty().set(concretePrice);
    }
    
    //pumpPrice
    public final ObjectProperty<ColoredValue<Integer>> pumpPriceProperty() {
        return this.pumpPrice;
    }
    
    public final ColoredValue<Integer> getPumpPrice() {
        return this.pumpPriceProperty().get();
    }
    
    public final void setPumpPrice(final ColoredValue<Integer> pumpPrice) {
        this.pumpPriceProperty().set(pumpPrice);
    }
    
    //squarePrice
    public final ObjectProperty<ColoredValue<Integer>> squarePriceProperty() {
        return this.squarePrice;
    }
    
    public final ColoredValue<Integer> getSquarePrice() {
        return this.squarePriceProperty().get();
    }
    
    public final void setSquarePrice(final ColoredValue<Integer> squarePrice) {
        this.squarePriceProperty().set(squarePrice);
    }
    
    //totalCosts
    public final ObjectProperty<ColoredValue<Integer>> totalCostsProperty() {
        return this.totalCosts;
    }
    
    public final ColoredValue<Integer> getTotalCosts() {
        return this.totalCostsProperty().get();
    }
    
    public final void setTotalCosts(final ColoredValue<Integer> totalCosts) {
        this.totalCostsProperty().set(totalCosts);
    }
    
    //billPrice
    public final ObjectProperty<ColoredValue<Integer>> billPriceProperty() {
        return this.billPrice;
    }
    
    public final ColoredValue<Integer> getBillPrice() {
        return this.billPriceProperty().get();
    }
    
    public final void setBillPrice(final ColoredValue<Integer> billPrice) {
        this.billPriceProperty().set(billPrice);
    }
    
    //gain
    public final ObjectProperty<ColoredValue<Integer>> gainProperty() {
        return this.gain;
    }
    
    public final ColoredValue<Integer> getGain() {
        return this.gainProperty().get();
    }
    
    public final void setGain(final ColoredValue<Integer> gain) {
        this.gainProperty().set(gain);
    }
    
    //profitMargin
    public final ObjectProperty<ColoredValue<Integer>> profitMarginProperty() {
        return this.profitMargin;
    }
    
    public final ColoredValue<Integer> getProfitMargin() {
        return this.profitMarginProperty().get();
    }
    
    public final void setProfitMargin(final ColoredValue<Integer> profitMargin) {
        this.profitMarginProperty().set(profitMargin);
    }
}