/**
 * @author Ondřej Doněk <ondrejd@gmail.com>
 * @link https://github.com/ondrejd/costs-javafx-app for the canonical source repository
 * @license https://www.gnu.org/licenses/gpl-3.0.en.html GNU General Public License 3.0
 */

package ondrejd;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ColoredValue<T> {
    public enum ColorType { NOCOLOR, RED, YELLOW };

    private final T value;
    private final ObjectProperty<ColorType> color = new SimpleObjectProperty<>(ColorType.NOCOLOR);
    
    public ColoredValue(T value) {
        this.value = value;
    }
    
    public ColoredValue(T value, ColorType color) {
        this(value);
        setColor(color);
    }
    
    public T getValue() {
        return value;
    }
    
    public final ObjectProperty<ColorType> colorProperty() {
        return this.color;
    }
    
    public final ColoredValue.ColorType getColor() {
        return this.colorProperty().get();
    }

    public final void setColor(final ColoredValue.ColorType color) {
        this.colorProperty().set(color);
    }
}