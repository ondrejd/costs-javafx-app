/**
 * @author Ondřej Doněk <ondrejd@gmail.com>
 * @link https://github.com/ondrejd/costs-javafx-app for the canonical source repository
 * @license https://www.gnu.org/licenses/gpl-3.0.en.html GNU General Public License 3.0
 */

package ondrejd;

public class MonthConstants {
    private float workPrice;
    private float wirePrice;
    private float pourPrice;
    private float paintPrice;
    private float sheetPrice;
    
    public MonthConstants(float workPrice, float wirePrice, float pourPrice, 
            float paintPrice, float sheetPrice) {
        this.workPrice = workPrice;
        this.wirePrice = wirePrice;
        this.pourPrice = pourPrice;
        this.paintPrice = paintPrice;
        this.sheetPrice = sheetPrice;
    }

    public float getWorkPrice() {
        return workPrice;
    }

    public void setWorkPrice(float workPrice) {
        this.workPrice = workPrice;
    }

    public float getWirePrice() {
        return wirePrice;
    }

    public void setWirePrice(float wirePrice) {
        this.wirePrice = wirePrice;
    }

    public float getPourPrice() {
        return pourPrice;
    }

    public void setPourPrice(float pourPrice) {
        this.pourPrice = pourPrice;
    }

    public float getPaintPrice() {
        return paintPrice;
    }

    public void setPaintPrice(float paintPrice) {
        this.paintPrice = paintPrice;
    }

    public float getSheetPrice() {
        return sheetPrice;
    }

    public void setSheetPrice(float sheetPrice) {
        this.sheetPrice = sheetPrice;
    }
}
