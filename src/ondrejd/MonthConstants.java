/**
 * @author Ondřej Doněk <ondrejd@gmail.com>
 * @link https://github.com/ondrejd/costs-javafx-app for the canonical source repository
 * @license https://www.gnu.org/licenses/gpl-3.0.en.html GNU General Public License 3.0
 */

package ondrejd;

public class MonthConstants {
    private double workPrice;
    private double wirePrice;
    private double pourPrice;
    private double paintPrice;
    private double sheetPrice;
    
    public MonthConstants(double workPrice, double wirePrice, double pourPrice, 
            double paintPrice, double sheetPrice) {
        this.workPrice = workPrice;
        this.wirePrice = wirePrice;
        this.pourPrice = pourPrice;
        this.paintPrice = paintPrice;
        this.sheetPrice = sheetPrice;
    }

    public double getWorkPrice() {
        return workPrice;
    }

    public void setWorkPrice(double workPrice) {
        this.workPrice = workPrice;
    }

    public double getWirePrice() {
        return wirePrice;
    }

    public void setWirePrice(double wirePrice) {
        this.wirePrice = wirePrice;
    }

    public double getPourPrice() {
        return pourPrice;
    }

    public void setPourPrice(double pourPrice) {
        this.pourPrice = pourPrice;
    }

    public double getPaintPrice() {
        return paintPrice;
    }

    public void setPaintPrice(double paintPrice) {
        this.paintPrice = paintPrice;
    }

    public double getSheetPrice() {
        return sheetPrice;
    }

    public void setSheetPrice(double sheetPrice) {
        this.sheetPrice = sheetPrice;
    }
}
