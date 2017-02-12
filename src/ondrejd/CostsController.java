/**
 * @author Ondřej Doněk <ondrejd@gmail.com>
 * @link https://github.com/ondrejd/costs-javafx-app for the canonical source repository
 * @license https://www.gnu.org/licenses/gpl-3.0.en.html GNU General Public License 3.0
 */

package ondrejd;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.prefs.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.*;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;

public class CostsController implements Initializable {
    public enum UndoActions { INSERT, REMOVE, UPDATE, COLOR, COPY, MOVE, MOVEDOWN, MOVEUP };

    private static final String SELECTED_MONTH = "selected_month";
    private static final Integer SELECTED_MONTH_DEFAULT = 0;

    private ObservableList<CostDataRow> data;
    private FilteredList<CostDataRow> filteredData;
    private Preferences prefs;
    
    @FXML
    private ComboBox monthsComboBox;
    @FXML
    private TextField workPrice;
    @FXML
    private TextField wirePrice;
    @FXML
    private TextField pourPrice;
    @FXML
    private TextField paintPrice;
    @FXML
    private TextField sheetPrice;
    @FXML
    private Button undoButton;
    @FXML
    private Button addRowButton;
    @FXML
    private Button delRowButton;
    @FXML
    private Button yellowButton;
    @FXML
    private Button redButton;
    @FXML
    private TableView<CostDataRow> table;
    @FXML
    private TableColumn<CostDataRow, ColoredValue<String>> placeTCol;
    @FXML
    private TableColumn<CostDataRow, ColoredValue<Integer>> surfaceTCol;
    @FXML
    private TableColumn<CostDataRow, ColoredValue<Integer>> workPriceTCol;
    @FXML
    private TableColumn<CostDataRow, ColoredValue<Integer>> wireWeightTCol;
    @FXML
    private TableColumn<CostDataRow, ColoredValue<Integer>> wirePriceTCol;
    @FXML
    private TableColumn<CostDataRow, ColoredValue<Integer>> pourPriceTCol;
    @FXML
    private TableColumn<CostDataRow, ColoredValue<Integer>> paintPriceTCol;
    @FXML
    private TableColumn<CostDataRow, ColoredValue<Integer>> sheetPriceTCol;
    @FXML
    private TableColumn<CostDataRow, ColoredValue<Integer>> concretePriceTCol;
    @FXML
    private TableColumn<CostDataRow, ColoredValue<Integer>> pumpPriceTCol;
    @FXML
    private TableColumn<CostDataRow, ColoredValue<Integer>> squarePriceTCol;
    @FXML
    private TableColumn<CostDataRow, ColoredValue<Integer>> totalCostsTCol;
    @FXML
    private TableColumn<CostDataRow, ColoredValue<Integer>> billPriceTCol;
    @FXML
    private TableColumn<CostDataRow, ColoredValue<Integer>> gainTCol;
    @FXML
    private TableColumn<CostDataRow, ColoredValue<Integer>> profitMarginTCol;
    @FXML
    private Label totalCostsSum;
    @FXML
    private Label billPriceSum;
    @FXML
    private Label gainSum;
    @FXML
    private Label profitMarginSum;
    @FXML
    private Menu copyRowMenu;
    @FXML
    private Menu moveRowMenu;
    @FXML
    private MenuItem undoMenuItem;
    @FXML
    private MenuItem addRowMenuItem;
    @FXML
    private MenuItem delRowMenuItem;
    @FXML
    private MenuItem colorRedMenuItem;
    @FXML
    private MenuItem colorYellowMenuItem;
    @FXML
    private MenuItem moveRowDownMenuItem;
    @FXML
    private MenuItem moveRowUpMenuItem;

    /**
     * Holds actions for undo.
     */
    private ObservableList<UndoAction> undo = FXCollections.<UndoAction>observableArrayList();

    /**
     * @param first Index of the first row to swap (in table view).
     * @param second Index of the second row to swap (in table view)
     */
    private void swapDataRows(Integer first, Integer second) {
        CostDataRow firstRow = (CostDataRow) table.getItems().get(first);
        CostDataRow secondRow = (CostDataRow) table.getItems().get(second);

        int firstIndex = data.indexOf(firstRow);
        int secondIndex = data.indexOf(secondRow);

        java.util.Collections.swap(data, firstIndex, secondIndex);
    }

    @FXML
    private void handleUndoAction(ActionEvent event) {
        if (undo.isEmpty()) {
            // There is no action that can be undone.
            return;
        }

        UndoAction action = undo.remove(undo.size() - 1);

        if (action.getType().equals(UndoActions.INSERT) || action.getType().equals(UndoActions.COPY)) {
            data.remove(action.getOriginalData());
            //switchMonth(action.getOriginalData().getMonth());
        }
        else if (action.getType().equals(UndoActions.REMOVE)) {
            data.add(action.getOriginalData());
            //switchMonth(action.getOriginalData().getMonth());
        }
        else if (action.getType().equals(UndoActions.UPDATE)) {
            if (getSelectedMonthIndex() != action.getMonth()) {
                switchMonth(action.getMonth());
            }

            // Update correct column
            CostDataRow row = table.getItems().get(action.getOriginalRow());
            boolean update = true;

            if (action.getColumn().equals(placeTCol.getId())) {
                ColoredValue<String> val = (ColoredValue<String>) action.getValue();
                row.setPlace(val);
            }
            else if (action.getColumn().equals(surfaceTCol.getId())) {
                ColoredValue<Integer> val = (ColoredValue<Integer>) action.getValue();
                row.setSurface(val);
                recalculateRowAfterSurface(row);
            }
            else if (action.getColumn().equals(workPriceTCol.getId())) {
                ColoredValue<Integer> val = (ColoredValue<Integer>) action.getValue();
                row.setWorkPrice(val);
            }
            else if (action.getColumn().equals(wireWeightTCol.getId())) {
                ColoredValue<Integer> val = (ColoredValue<Integer>) action.getValue();
                row.setWireWeight(val);
                recalculateRowAfterWireWeight(row);
            }
            else if (action.getColumn().equals(wirePriceTCol.getId())) {
                ColoredValue<Integer> val = (ColoredValue<Integer>) action.getValue();
                row.setWirePrice(val);
            }
            else if (action.getColumn().equals(pourPriceTCol.getId())) {
                ColoredValue<Integer> val = (ColoredValue<Integer>) action.getValue();
                row.setPourPrice(val);
            }
            else if (action.getColumn().equals(paintPriceTCol.getId())) {
                ColoredValue<Integer> val = (ColoredValue<Integer>) action.getValue();
                row.setPaintPrice(val);
            }
            else if (action.getColumn().equals(sheetPriceTCol.getId())) {
                ColoredValue<Integer> val = (ColoredValue<Integer>) action.getValue();
                row.setSheetPrice(val);
            }
            else if (action.getColumn().equals(concretePriceTCol.getId())) {
                ColoredValue<Integer> val = (ColoredValue<Integer>) action.getValue();
                row.setConcretePrice(val);
            }
            else if (action.getColumn().equals(pumpPriceTCol.getId())) {
                ColoredValue<Integer> val = (ColoredValue<Integer>) action.getValue();
                row.setPumpPrice(val);
            }
            else if (action.getColumn().equals(squarePriceTCol.getId())) {
                ColoredValue<Integer> val = (ColoredValue<Integer>) action.getValue();
                row.setSquarePrice(val);
            }
            else if (action.getColumn().equals(billPriceTCol.getId())) {
                ColoredValue<Integer> val = (ColoredValue<Integer>) action.getValue();
                row.setBillPrice(val);
            }
            else {
                update = false;
            }

            // Refresh table
            if (update == true && !action.getColumn().equals(placeTCol.getId())) {
                updateSumColumns(row);
                table.refresh();
                focusTable();
            }
        }
        else if (action.getType().equals(UndoActions.COLOR)) {
            action.getColorChanges().forEach(change -> {
                CostDataRow row = table.getItems().get(change.getRow());

                if (change.getColumn().equals(placeTCol.getId())) {
                    row.getPlace().setColor(change.getOldColor());
                } else if (change.getColumn().equals(surfaceTCol.getId())) {
                    row.getSurface().setColor(change.getOldColor());
                } else if (change.getColumn().equals(workPriceTCol.getId())) {
                    row.getWorkPrice().setColor(change.getOldColor());
                } else if (change.getColumn().equals(wireWeightTCol.getId())) {
                    row.getWireWeight().setColor(change.getOldColor());
                } else if (change.getColumn().equals(wirePriceTCol.getId())) {
                    row.getWirePrice().setColor(change.getOldColor());
                } else if (change.getColumn().equals(pourPriceTCol.getId())) {
                    row.getPourPrice().setColor(change.getOldColor());
                } else if (change.getColumn().equals(paintPriceTCol.getId())) {
                    row.getPaintPrice().setColor(change.getOldColor());
                } else if (change.getColumn().equals(sheetPriceTCol.getId())) {
                    row.getSheetPrice().setColor(change.getOldColor());
                } else if (change.getColumn().equals(concretePriceTCol.getId())) {
                    row.getConcretePrice().setColor(change.getOldColor());
                } else if (change.getColumn().equals(pumpPriceTCol.getId())) {
                    row.getPumpPrice().setColor(change.getOldColor());
                } else if (change.getColumn().equals(squarePriceTCol.getId())) {
                    row.getSquarePrice().setColor(change.getOldColor());
                } else if (change.getColumn().equals(totalCostsTCol.getId())) {
                    row.getTotalCosts().setColor(change.getOldColor());
                } else if (change.getColumn().equals(billPriceTCol.getId())) {
                    row.getBillPrice().setColor(change.getOldColor());
                } else if (change.getColumn().equals(gainTCol.getId())) {
                    row.getGain().setColor(change.getOldColor());
                } else if (change.getColumn().equals(profitMarginTCol.getId())) {
                    row.getProfitMargin().setColor(change.getOldColor());
                }
            });
        }
        else if (action.getType().equals(UndoActions.MOVE)) {
            data.remove(action.getUpdatedData());
            data.add(action.getOriginalData());
            //switchMonth(action.getOriginalData().getMonth());
        }
        else if (action.getType().equals(UndoActions.MOVEDOWN)) {
            switchMonth(action.getMonth());
            swapDataRows(action.getUpdatedRow() + 1, action.getOriginalRow() + 1);
        }
        else if (action.getType().equals(UndoActions.MOVEUP)) {
            switchMonth(action.getMonth());
            swapDataRows(action.getUpdatedRow() - 1, action.getOriginalRow() - 1);
        }

        updateUndoUi();
    }
    
    @FXML
    private void handleAddRowAction(ActionEvent event) {
        int month = getSelectedMonthIndex();
        CostDataRow row = new CostDataRow(month, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        data.add(row);
        undo.add(new UndoAction(UndoActions.INSERT, row));
    }
    
    @FXML
    private void handleDelRowAction(ActionEvent event) {
        int idx = table.getSelectionModel().getSelectedIndex();
        CostDataRow row = (CostDataRow) table.getItems().get(idx);

        /**
         * @see http://code.makery.ch/blog/javafx-dialogs-official/
         */
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Smazat řádek");
        alert.setHeaderText("Potvrďte smazání vybraného řádku.");
        alert.setContentText("Skutečně chcete smazat řádek číslo " + idx + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            data.remove(row);
            undo.add(new UndoAction(UndoActions.REMOVE, row, idx));
        }
    }
    
    @FXML
    private void handleMoveRowDownAction(ActionEvent event) {
        int idx = table.getSelectionModel().getSelectedIndex();
        int month = table.getItems().get(idx).getMonth();
        if (idx < table.getItems().size() - 1) {
            swapDataRows(idx, idx + 1);
        }
        undo.add(new UndoAction(UndoActions.MOVEDOWN, getSelectedMonthIndex(), idx, idx - 1));
    }
    
    @FXML
    private void handleMoveRowUpAction(ActionEvent event) {
        int idx = table.getSelectionModel().getSelectedIndex();
        if (idx > 0) {
            swapDataRows(idx, idx - 1);
        }
        undo.add(new UndoAction(UndoActions.MOVEUP, getSelectedMonthIndex(), idx, idx + 1));
    }

    @FXML
    private void handleYellowButtonAction(ActionEvent event) {
        ArrayList<ColorChange> changes = new <ColorChange>ArrayList();

        table.getSelectionModel().getSelectedCells().forEach((pos) -> {
            CostDataRow row = table.getItems().get(pos.getRow());
            ColoredValue.ColorType oldColor = null;
            ColoredValue.ColorType newColor = ColoredValue.ColorType.YELLOW;
            
            if (pos.getTableColumn() == placeTCol) {
                oldColor = row.getSurface().getColor();
                row.getPlace().setColor(newColor);
            } else if (pos.getTableColumn() == surfaceTCol) {
                oldColor = row.getSurface().getColor();
                row.getSurface().setColor(newColor);
            } else if (pos.getTableColumn() == workPriceTCol) {
                oldColor = row.getWorkPrice().getColor();
                row.getWorkPrice().setColor(newColor);
            } else if (pos.getTableColumn() == wireWeightTCol) {
                oldColor = row.getWireWeight().getColor();
                row.getWireWeight().setColor(newColor);
            } else if (pos.getTableColumn() == wirePriceTCol) {
                oldColor = row.getWirePrice().getColor();
                row.getWirePrice().setColor(newColor);
            } else if (pos.getTableColumn() == pourPriceTCol) {
                oldColor = row.getPourPrice().getColor();
                row.getPourPrice().setColor(newColor);
            } else if (pos.getTableColumn() == paintPriceTCol) {
                oldColor = row.getPaintPrice().getColor();
                row.getPaintPrice().setColor(newColor);
            } else if (pos.getTableColumn() == sheetPriceTCol) {
                oldColor = row.getSheetPrice().getColor();
                row.getSheetPrice().setColor(newColor);
            } else if (pos.getTableColumn() == concretePriceTCol) {
                oldColor = row.getConcretePrice().getColor();
                row.getConcretePrice().setColor(newColor);
            } else if (pos.getTableColumn() == pumpPriceTCol) {
                oldColor = row.getPumpPrice().getColor();
                row.getPumpPrice().setColor(newColor);
            } else if (pos.getTableColumn() == squarePriceTCol) {
                oldColor = row.getSquarePrice().getColor();
                row.getSquarePrice().setColor(newColor);
            } else if (pos.getTableColumn() == totalCostsTCol) {
                oldColor = row.getTotalCosts().getColor();
                row.getTotalCosts().setColor(newColor);
            } else if (pos.getTableColumn() == billPriceTCol) {
                oldColor = row.getBillPrice().getColor();
                row.getBillPrice().setColor(newColor);
            } else if (pos.getTableColumn() == gainTCol) {
                oldColor = row.getGain().getColor();
                row.getGain().setColor(newColor);
            } else if (pos.getTableColumn().getId().equals(profitMarginTCol.getId())) {
                oldColor = row.getProfitMargin().getColor();
                row.getProfitMargin().setColor(newColor);
            }

            changes.add(new ColorChange(pos.getRow(), pos.getTableColumn().getId(), oldColor, newColor));
        });

        undo.add(new UndoAction(UndoActions.COLOR, changes));
    }
    
    @FXML
    private void handleRedButtonAction(ActionEvent event) {
        ArrayList<ColorChange> changes = new <ColorChange>ArrayList();

        table.getSelectionModel().getSelectedCells().forEach((pos) -> {
            CostDataRow row = table.getItems().get(pos.getRow());
            ColoredValue.ColorType oldColor = null;
            ColoredValue.ColorType newColor = ColoredValue.ColorType.RED;
            
            if (pos.getTableColumn() == placeTCol) {
                oldColor = row.getPlace().getColor();
                row.getPlace().setColor(newColor);
            } else if (pos.getTableColumn() == surfaceTCol) {
                oldColor = row.getSurface().getColor();
                row.getSurface().setColor(newColor);
            } else if (pos.getTableColumn() == workPriceTCol) {
                oldColor = row.getWorkPrice().getColor();
                row.getWorkPrice().setColor(newColor);
            } else if (pos.getTableColumn() == wireWeightTCol) {
                oldColor = row.getWireWeight().getColor();
                row.getWireWeight().setColor(newColor);
            } else if (pos.getTableColumn() == wirePriceTCol) {
                oldColor = row.getWirePrice().getColor();
                row.getWirePrice().setColor(newColor);
            } else if (pos.getTableColumn() == pourPriceTCol) {
                oldColor = row.getPourPrice().getColor();
                row.getPourPrice().setColor(newColor);
            } else if (pos.getTableColumn() == paintPriceTCol) {
                oldColor = row.getPaintPrice().getColor();
                row.getPaintPrice().setColor(newColor);
            } else if (pos.getTableColumn() == sheetPriceTCol) {
                oldColor = row.getSheetPrice().getColor();
                row.getSheetPrice().setColor(newColor);
            } else if (pos.getTableColumn() == concretePriceTCol) {
                oldColor = row.getConcretePrice().getColor();
                row.getConcretePrice().setColor(newColor);
            } else if (pos.getTableColumn() == pumpPriceTCol) {
                oldColor = row.getPumpPrice().getColor();
                row.getPumpPrice().setColor(newColor);
            } else if (pos.getTableColumn() == squarePriceTCol) {
                oldColor = row.getSquarePrice().getColor();
                row.getSquarePrice().setColor(newColor);
            } else if (pos.getTableColumn() == totalCostsTCol) {
                oldColor = row.getTotalCosts().getColor();
                row.getTotalCosts().setColor(newColor);
            } else if (pos.getTableColumn() == billPriceTCol) {
                oldColor = row.getBillPrice().getColor();
                row.getBillPrice().setColor(newColor);
            } else if (pos.getTableColumn() == gainTCol) {
                oldColor = row.getGain().getColor();
                row.getGain().setColor(newColor);
            } else if (pos.getTableColumn().getId().equals(profitMarginTCol.getId())) {
                oldColor = row.getProfitMargin().getColor();
                row.getProfitMargin().setColor(newColor);
            }

            changes.add(new ColorChange(pos.getRow(), pos.getTableColumn().getId(), oldColor, newColor));
        });
        undo.add(new UndoAction(UndoActions.COLOR, changes));
    }
    
    @FXML
    private void handleMonthsComboBoxAction(ActionEvent event) {
        switchMonth(getSelectedMonthIndex());
    }

    /**
     * Switch view to given month.
     * @param month Month index (starting from 0).
     */
    private void switchMonth(int month) {
        setConstants();
        filteredData.setPredicate(n -> { return (n.getMonth() == month); });
        updateSumLabels();
        focusTable();
    }

    /**
     * Save data to XML file - called from {@link ondrejd.Costs}.
     */
    public void saveData() {
        // Save data
        XmlDataSource.saveXml(data, getCurrentYear());
        // Save user preferences
        try {
            prefs.putInt(SELECTED_MONTH, getSelectedMonthIndex());
            prefs.flush();
        } catch(BackingStoreException e) {
            //e.printStackTrace();
            //System.out.print("Exception occured when saving user preferences!");
        }
    }
    
    /**
     * @return Index (starting from 0) of currently selected month.
     */
    private int getSelectedMonthIndex() {
        return getMonthIndex(monthsComboBox.getValue().toString());
    }

    /**
     * @param month Month name
     * @return Index (starting from 0) of given month.
     */
    private int getMonthIndex(String month) {
        int idx = 0;
        switch(month) {
            case "Leden"    : idx =  0; break;
            case "Únor"     : idx =  1; break;
            case "Březen"   : idx =  2; break;
            case "Duben"    : idx =  3; break;
            case "Květen"   : idx =  4; break;
            case "Červen"   : idx =  5; break;
            case "Červenec" : idx =  6; break;
            case "Srpen"    : idx =  7; break;
            case "Září"     : idx =  8; break;
            case "Říjen"    : idx =  9; break;
            case "Listopad" : idx = 10; break;
            case "Prosinec" : idx = 11; break;
        }
        return idx;
    }
    
    /**
     * @return Returns constant for calculating work price.
     */
    private float getWorkPriceConstant() {
        return Float.parseFloat(workPrice.getText());
    }
    
    /**
     * @return Returns constant for calculating wire price.
     */
    private float getWirePriceConstant() {
        return Float.parseFloat(wirePrice.getText());
    }
    
    /**
     * @return Returns constant for calculating pour price.
     */
    private float getPourPriceConstant() {
        return Float.parseFloat(pourPrice.getText());
    }
    
    /**
     * @return Returns constant for calculating paint price.
     */
    private float getPaintPriceConstant() {
        return Float.parseFloat(paintPrice.getText());
    }
    
    /**
     * @return Returns constant for calculating sheet price.
     */
    private float getSheetPriceConstant() {
        return Float.parseFloat(sheetPrice.getText());
    }

    /**
     * Create table cell.
     * @param <T> Used type.
     * @param format Used format.
     * @param supplier Method of {@link CostDataRow} that supplies value.
     * @return Table cell.
     */
    private <T> TableCell<CostDataRow, ColoredValue<T>> createTableCell(String format, Function<String, T> supplier) {
        TextFieldTableCell<CostDataRow, ColoredValue<T>> cell = new TextFieldTableCell<>();
        cell.setConverter(new StringConverter<ColoredValue<T>>() {
            @Override
            public String toString(ColoredValue<T> item) {
                return item == null ? "" : String.format(format, item.getValue());
            }
            @Override
            public ColoredValue<T> fromString(String string) {
                String s = string.replace(" m2", "").replace(" Kč", "").
                        replace(" kč", "").replace(" Kg", "").replace(" kg", "").
                        replace(" %", "").replace(" ", "").replace(",", ".").trim();
                try {
                    Float f = Float.parseFloat(s);
                    Integer i = f.intValue();
                    s = i.toString();
                } catch (java.lang.NumberFormatException e) {
                    s = s;
                }
                T value = supplier.apply("".equals(s) ? "0" : s);
                ColoredValue.ColorType c = cell.getItem() == null 
                        ? ColoredValue.ColorType.NOCOLOR 
                        : cell.getItem().getColor();
                return new ColoredValue<>(value, c);
            }
        });

        ChangeListener<ColoredValue.ColorType> valListener = (obs, oldState, newState) -> {
            if (newState == ColoredValue.ColorType.YELLOW) {
                cell.setStyle("-fx-background-color: yellow ;");
            } else if (newState == ColoredValue.ColorType.RED) {
                cell.setStyle("-fx-background-color: red ;");
            } else if (newState == ColoredValue.ColorType.GREEN) {
                cell.setStyle("-fx-background-color: #cbe2ae ;");
            } else {
                cell.setStyle("");
            }
        };
        
        cell.itemProperty().addListener((obs, oldItem, newItem) -> {
            if (oldItem != null) {
                oldItem.colorProperty().removeListener(valListener);
            }
            if (newItem == null) {
                cell.setStyle("");
            } else {
                if (newItem.getColor() == ColoredValue.ColorType.YELLOW) {
                    cell.setStyle("-fx-background-color: yellow ;");
                } else if (newItem.getColor() == ColoredValue.ColorType.RED) {
                    cell.setStyle("-fx-background-color: red ;");
                } else if (newItem.getColor() == ColoredValue.ColorType.GREEN) {
                    cell.setStyle("-fx-background-color: #cbe2ae ;");
                } else {
                    cell.setStyle("");
                }
                newItem.colorProperty().addListener(valListener);
            }
        });

        return cell ;
    }
    
    /**
     * Update sum columns (total costs, gain, profit margin) in given table row.
     * @param row Row where we need to update sum columns.
     */
    private void updateSumColumns(CostDataRow row) {
        // Update total costs
        ColoredValue.ColorType tcc = row.totalCostsProperty().get().getColor();
        int tcv = row.getWorkPrice().getValue() + row.getWirePrice().getValue() +
                row.getPourPrice().getValue() + row.getPaintPrice().getValue() +
                row.getSheetPrice().getValue() + row.getConcretePrice().getValue() +
                row.getPumpPrice().getValue() + row.getSquarePrice().getValue();
        row.setTotalCosts(new ColoredValue<>(tcv, tcc));
        
        // Update gain
        ColoredValue.ColorType gc = row.gainProperty().get().getColor();
        int bp = row.getBillPrice().getValue();
        int g  = 0;
        
        if (bp == 0) {
            row.setGain(new ColoredValue<>(0, gc));
        } else {
            g = bp - tcv;
            row.setGain(new ColoredValue<>(g, gc));
        }
        
        // Update profit margin
        if (tcv == 0 || g == 0) {
            row.setProfitMargin(new ColoredValue<>(0, ColoredValue.ColorType.GREEN));
        } else {
            float pm = ((float) g / (float) bp) * 100;
            row.setProfitMargin(new ColoredValue<>((int) pm, ColoredValue.ColorType.GREEN));
        }
        
        // Update summary labels
        updateSumLabels();
    }
    
    /**
     * Set text fields for price constants from data storred in {@link ondrejd.XmlDataSource}.
     */
    private void setConstants() {
        int month = getSelectedMonthIndex();
        MonthConstants constants = XmlDataSource.getConstants(month);
        workPrice.setText(Float.toString(constants.getWorkPrice()));
        wirePrice.setText(Float.toString(constants.getWirePrice()));
        pourPrice.setText(Float.toString(constants.getPourPrice()));
        paintPrice.setText(Float.toString(constants.getPaintPrice()));
        sheetPrice.setText(Float.toString(constants.getSheetPrice()));
    }
    
    /**
     * Save changes in price constants into {@link ondrejd.XmlDataSource}.
     */
    private void updateConstants() {
        int month = getSelectedMonthIndex();
        MonthConstants consts = XmlDataSource.getConstants(month);
        consts.setWorkPrice(Float.parseFloat(workPrice.getText()));
        consts.setWirePrice(Float.parseFloat(wirePrice.getText()));
        consts.setPourPrice(Float.parseFloat(pourPrice.getText()));
        consts.setPaintPrice(Float.parseFloat(paintPrice.getText()));
        consts.setSheetPrice(Float.parseFloat(sheetPrice.getText()));
        XmlDataSource.setConstants(month, consts);
    }

    /**
     * @return Returns current year.
     */
    public static int getCurrentYear() {
        java.util.Calendar now = java.util.Calendar.getInstance();
        return now.get(java.util.Calendar.YEAR);
    }

    /**
     * Copy or move selected row to target month.
     * @param month Name of target month.
     * @param deleteOriginalRow TRUE for moving (original row will be deleted).
     */
    private void copyRowToMonth(String month, boolean deleteOriginalRow) {
        // 1) Get target month index
        int monthIdx = getMonthIndex(month);
        // 2) Remove selected row
        int idx = table.getSelectionModel().getSelectedIndex();
        CostDataRow oldRow = (CostDataRow) table.getItems().get(idx);
        if(deleteOriginalRow == true) {
            data.remove(oldRow);
        }
        // 3) Insert row into target month
        CostDataRow newRow = new CostDataRow(monthIdx, 
                oldRow.getPlace().getValue(), 
                oldRow.getSurface().getValue(), 
                oldRow.getWorkPrice().getValue(),
                oldRow.getWireWeight().getValue(),
                oldRow.getWirePrice().getValue(),
                oldRow.getPourPrice().getValue(),
                oldRow.getPaintPrice().getValue(),
                oldRow.getSheetPrice().getValue(),
                oldRow.getConcretePrice().getValue(),
                oldRow.getPumpPrice().getValue(),
                oldRow.getSquarePrice().getValue(),
                oldRow.getTotalCosts().getValue(),
                oldRow.getBillPrice().getValue(),
                oldRow.getGain().getValue(),
                oldRow.getProfitMargin().getValue());
        data.add(newRow);
        // 4) Switch to target month
        monthsComboBox.getSelectionModel().clearAndSelect(monthIdx);
        // 5) Add action to undo queue
        if (deleteOriginalRow != true) {
            undo.add(new UndoAction(UndoActions.COPY, newRow));
        } else {
            undo.add(new UndoAction(UndoActions.MOVE, oldRow, newRow));
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize user preferences
        prefs = Preferences.userNodeForPackage(ondrejd.CostsController.class);

        // Set up months combobox
        ObservableList<String> months = FXCollections.observableArrayList(
                "Leden", "Únor", "Březen", "Duben", "Květen", "Červen", "Červenec",
                "Srpen", "Září", "Říjen", "Listopad", "Prosinec");
        monthsComboBox.setItems(months);
        monthsComboBox.getSelectionModel().selectFirst();

        // Set up "Copy row" menu
        months.forEach((month) -> {
            MenuItem mi = new MenuItem(month);
            copyRowMenu.getItems().add(mi);
            mi.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    copyRowToMonth(mi.getText(), false);
                }
            });
        });

        // Set up "Move row" menu
        months.forEach((month) -> {
            MenuItem mi = new MenuItem(month);
            moveRowMenu.getItems().add(mi);
            mi.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    copyRowToMonth(mi.getText(), true);
                }
            });
        });

        // Set up button icons
        Image iconUndo   = new Image("resources/graphics/arrow_undo.png");
        Image iconDown   = new Image("resources/graphics/arrow_down.png");
        Image iconUp     = new Image("resources/graphics/arrow_up.png");
        Image iconAddRow = new Image("resources/graphics/table_row_insert.png");
        Image iconDelRow = new Image("resources/graphics/table_row_delete.png");
        Image iconRed    = new Image("resources/graphics/tag_red.png");
        Image iconYellow = new Image("resources/graphics/tag_yellow.png");
        undoButton.setGraphic(new ImageView(iconUndo));
        addRowButton.setGraphic(new ImageView(iconAddRow));
        delRowButton.setGraphic(new ImageView(iconDelRow));
        redButton.setGraphic(new ImageView(iconRed));
        yellowButton.setGraphic(new ImageView(iconYellow));

        // Set up popup menuitems icons
        undoMenuItem.setGraphic(new ImageView(iconUndo));
        moveRowDownMenuItem.setGraphic(new ImageView(iconDown));
        moveRowUpMenuItem.setGraphic(new ImageView(iconUp));
        addRowMenuItem.setGraphic(new ImageView(iconAddRow));
        delRowMenuItem.setGraphic(new ImageView(iconDelRow));
        colorRedMenuItem.setGraphic(new ImageView(iconRed));
        colorYellowMenuItem.setGraphic(new ImageView(iconYellow));

        // Set up undo queue
        undo.addListener((ListChangeListener<UndoAction>) change -> { updateUndoUi(); });

        // Disable undo buttons/menuitems immediately because queue is empty
        undoButton.setDisable(true);
        undoMenuItem.setDisable(true);

        // Load data
        data = XmlDataSource.loadData(getCurrentYear());
        
        // Set month which was selected when application was running last time
        int selMonth = prefs.getInt(SELECTED_MONTH, SELECTED_MONTH_DEFAULT);
        monthsComboBox.getSelectionModel().select(selMonth);

        // Set up constants
        setConstants();

        // Set up data table
        filteredData = new FilteredList<>(data, n -> {
            return (n.getMonth() == getSelectedMonthIndex());
        });
        table.setEditable(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.setItems(filteredData);

        // Set up data table columns
        placeTCol.setCellValueFactory(cellData -> cellData.getValue().placeProperty());
        placeTCol.setCellFactory(tc -> createTableCell("%s", String::new));
        placeTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, ColoredValue<String>>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, ColoredValue<String>> e) {
                    ColoredValue<String> oldVal = e.getOldValue();
                    int idx = e.getTablePosition().getRow();
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(idx);
                    row.setPlace(e.getNewValue());
                    // Undo, refresh, focus
                    undo.add(new UndoAction(UndoActions.UPDATE, idx, placeTCol.getId(), oldVal));
                    e.getTableView().refresh();
                    focusTable();
                }
            }
        );
        surfaceTCol.setCellValueFactory(cellData -> cellData.getValue().surfaceProperty());
        surfaceTCol.setCellFactory(tc -> createTableCell("%,d m2", Integer::new));
        surfaceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, ColoredValue<Integer>>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, ColoredValue<Integer>> e) {
                    ColoredValue<Integer> oldVal = e.getOldValue();
                    int idx = e.getTablePosition().getRow();
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(idx);
                    row.setSurface(e.getNewValue());
                    // Recalculate some values
                    recalculateRowAfterSurface(row);
                    // Update, undo, refresh, focus
                    updateSumColumns(row);
                    undo.add(new UndoAction(UndoActions.UPDATE, idx, surfaceTCol.getId(), oldVal));
                    e.getTableView().refresh();
                    focusTable();
                }
            }
        );
        workPriceTCol.setCellValueFactory(cellData -> cellData.getValue().workPriceProperty());
        workPriceTCol.setCellFactory(tc -> createTableCell("%,d Kč", Integer::new));
        workPriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, ColoredValue<Integer>>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, ColoredValue<Integer>> e) {
                    ColoredValue<Integer> oldVal = e.getOldValue();
                    int idx = e.getTablePosition().getRow();
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(idx);
                    row.setWorkPrice(e.getNewValue());
                    // Update, undo, refresh, focus
                    updateSumColumns(row);
                    undo.add(new UndoAction(UndoActions.UPDATE, idx, workPriceTCol.getId(), oldVal));
                    e.getTableView().refresh();
                    focusTable();
                }
            }
        );
        wireWeightTCol.setCellValueFactory(cellData -> cellData.getValue().wireWeightProperty());
        wireWeightTCol.setCellFactory(tc -> createTableCell("%,d kg", Integer::new));
        wireWeightTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, ColoredValue<Integer>>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, ColoredValue<Integer>> e) {
                    ColoredValue<Integer> oldVal = e.getOldValue();
                    int idx = e.getTablePosition().getRow();
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(idx);
                    row.setWireWeight(e.getNewValue());
                    recalculateRowAfterWireWeight(row);
                    // Update, undo, refresh, focus
                    updateSumColumns(row);
                    undo.add(new UndoAction(UndoActions.UPDATE, idx, wireWeightTCol.getId(), oldVal));
                    e.getTableView().refresh();
                    focusTable();
                }
            }
        );
        wirePriceTCol.setCellValueFactory(cellData -> cellData.getValue().wirePriceProperty());
        wirePriceTCol.setCellFactory(tc -> createTableCell("%,d Kč", Integer::new));
        wirePriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, ColoredValue<Integer>>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, ColoredValue<Integer>> e) {
                    ColoredValue<Integer> oldVal = e.getOldValue();
                    int idx = e.getTablePosition().getRow();
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(idx);
                    row.setWirePrice(e.getNewValue());
                    // Update, undo, refresh, focus
                    updateSumColumns(row);
                    undo.add(new UndoAction(UndoActions.UPDATE, idx, wirePriceTCol.getId(), oldVal));
                    e.getTableView().refresh();
                    focusTable();
                }
            }
        );
        pourPriceTCol.setCellValueFactory(cellData -> cellData.getValue().pourPriceProperty());
        pourPriceTCol.setCellFactory(tc -> createTableCell("%,d Kč", Integer::new));
        pourPriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, ColoredValue<Integer>>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, ColoredValue<Integer>> e) {
                    ColoredValue<Integer> oldVal = e.getOldValue();
                    int idx = e.getTablePosition().getRow();
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(idx);
                    row.setPourPrice(e.getNewValue());
                    // Update, undo, refresh, focus
                    updateSumColumns(row);
                    undo.add(new UndoAction(UndoActions.UPDATE, idx, pourPriceTCol.getId(), oldVal));
                    e.getTableView().refresh();
                    focusTable();
                }
            }
        );
        paintPriceTCol.setCellValueFactory(cellData -> cellData.getValue().paintPriceProperty());
        paintPriceTCol.setCellFactory(tc -> createTableCell("%,d Kč", Integer::new));
        paintPriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, ColoredValue<Integer>>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, ColoredValue<Integer>> e) {
                    ColoredValue<Integer> oldVal = e.getOldValue();
                    int idx = e.getTablePosition().getRow();
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(idx);
                    row.setPaintPrice(e.getNewValue());
                    // Update, undo, refresh, focus
                    updateSumColumns(row);
                    undo.add(new UndoAction(UndoActions.UPDATE, idx, paintPriceTCol.getId(), oldVal));
                    e.getTableView().refresh();
                    focusTable();
                }
            }
        );
        sheetPriceTCol.setCellValueFactory(cellData -> cellData.getValue().sheetPriceProperty());
        sheetPriceTCol.setCellFactory(tc -> createTableCell("%,d Kč", Integer::new));
        sheetPriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, ColoredValue<Integer>>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, ColoredValue<Integer>> e) {
                    ColoredValue<Integer> oldVal = e.getOldValue();
                    int idx = e.getTablePosition().getRow();
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(idx);
                    row.setSheetPrice(e.getNewValue());
                    // Update, undo, refresh, focus
                    updateSumColumns(row);
                    undo.add(new UndoAction(UndoActions.UPDATE, idx, sheetPriceTCol.getId(), oldVal));
                    e.getTableView().refresh();
                    focusTable();
                }
            }
        );
        concretePriceTCol.setCellValueFactory(cellData -> cellData.getValue().concretePriceProperty());
        concretePriceTCol.setCellFactory(tc -> createTableCell("%,d Kč", Integer::new));
        concretePriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, ColoredValue<Integer>>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, ColoredValue<Integer>> e) {
                    ColoredValue<Integer> oldVal = e.getOldValue();
                    int idx = e.getTablePosition().getRow();
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(idx);
                    row.setConcretePrice(e.getNewValue());
                    // Update, undo, refresh, focus
                    updateSumColumns(row);
                    undo.add(new UndoAction(UndoActions.UPDATE, idx, concretePriceTCol.getId(), oldVal));
                    e.getTableView().refresh();
                    focusTable();
                }
            }
        );
        pumpPriceTCol.setCellValueFactory(cellData -> cellData.getValue().pumpPriceProperty());
        pumpPriceTCol.setCellFactory(tc -> createTableCell("%,d Kč", Integer::new));
        pumpPriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, ColoredValue<Integer>>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, ColoredValue<Integer>> e) {
                    ColoredValue<Integer> oldVal = e.getOldValue();
                    int idx = e.getTablePosition().getRow();
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(idx);
                    row.setPumpPrice(e.getNewValue());
                    // Update, undo, refresh, focus
                    updateSumColumns(row);
                    undo.add(new UndoAction(UndoActions.UPDATE, idx, pumpPriceTCol.getId(), oldVal));
                    e.getTableView().refresh();
                    focusTable();
                }
            }
        );
        squarePriceTCol.setCellValueFactory(cellData -> cellData.getValue().squarePriceProperty());
        squarePriceTCol.setCellFactory(tc -> createTableCell("%,d Kč", Integer::new));
        squarePriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, ColoredValue<Integer>>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, ColoredValue<Integer>> e) {
                    ColoredValue<Integer> oldVal = e.getOldValue();
                    int idx = e.getTablePosition().getRow();
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(idx);
                    row.setSquarePrice(e.getNewValue());
                    // Update, undo, refresh, focus
                    updateSumColumns(row);
                    undo.add(new UndoAction(UndoActions.UPDATE, idx, squarePriceTCol.getId(), oldVal));
                    e.getTableView().refresh();
                    focusTable();
                }
            }
        );
        totalCostsTCol.setCellValueFactory(cellData -> cellData.getValue().totalCostsProperty());
        totalCostsTCol.setCellFactory(tc -> createTableCell("%,d Kč", Integer::new));
        billPriceTCol.setCellValueFactory(cellData -> cellData.getValue().billPriceProperty());
        billPriceTCol.setCellFactory(tc -> createTableCell("%,d Kč", Integer::new));
        billPriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, ColoredValue<Integer>>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, ColoredValue<Integer>> e) {
                    ColoredValue<Integer> oldVal = e.getOldValue();
                    int idx = e.getTablePosition().getRow();
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(idx);
                    row.setBillPrice(e.getNewValue());
                    // Update, undo, refresh, focus
                    updateSumColumns(row);
                    undo.add(new UndoAction(UndoActions.UPDATE, idx, billPriceTCol.getId(), oldVal));
                    e.getTableView().refresh();
                    focusTable();
                }
            }
        );
        gainTCol.setCellValueFactory(cellData -> cellData.getValue().gainProperty());
        gainTCol.setCellFactory(tc -> createTableCell("%,d Kč", Integer::new));
        profitMarginTCol.setCellValueFactory(cellData -> cellData.getValue().profitMarginProperty());
        profitMarginTCol.setCellFactory(tc -> createTableCell("%,d %%", Integer::new));
        
        // Set up disabled state on color buttons
        yellowButton.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedCells()));
        redButton.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedCells()));
        
        // On price constants change
        // Values are updated only if current value is calculated (not changed by hand).
        workPrice.textProperty().addListener((obs, oldVal, newVal) -> {
            table.getItems().forEach(row -> {
                int surface = row.getSurface().getValue();
                float o = "".equals(oldVal) ? 0 : Float.parseFloat(oldVal);
                float n = "".equals(newVal) ? 0 : Float.parseFloat(newVal);
                
                if ((int)(surface * o) == row.getWorkPrice().getValue()) {
                    ColoredValue.ColorType c = row.workPriceProperty().get().getColor();
                    row.setWorkPrice(new ColoredValue<>((int)(surface * n), c));
                    updateSumColumns(row);
                }
                
                updateConstants();
                focusTable();
            });
        });
        wirePrice.textProperty().addListener((obs, oldVal, newVal) -> {
            table.getItems().forEach(row -> {
                int wireWeight = row.getWireWeight().getValue();
                float o = "".equals(oldVal) ? 0 : Float.parseFloat(oldVal);
                float n = "".equals(newVal) ? 0 : Float.parseFloat(newVal);
                
                if ((int)(wireWeight * o) == row.getWirePrice().getValue()) {
                    ColoredValue.ColorType c = row.wirePriceProperty().get().getColor();
                    row.setWirePrice(new ColoredValue<>((int)(wireWeight * n), c));
                }
                
                updateConstants();
                focusTable();
            });
        });
        pourPrice.textProperty().addListener((obs, oldVal, newVal) -> {
            table.getItems().forEach(row -> {
                int surface = row.getSurface().getValue();
                float o = "".equals(oldVal) ? 0 : Float.parseFloat(oldVal);
                float n = "".equals(newVal) ? 0 : Float.parseFloat(newVal);
                
                if ((int)(surface * o) == row.getPourPrice().getValue()) {
                    ColoredValue.ColorType c = row.pourPriceProperty().get().getColor();
                    row.setPourPrice(new ColoredValue<>((int)(surface * n), c));
                    updateSumColumns(row);
                }
                
                updateConstants();
                focusTable();
            });
        });
        paintPrice.textProperty().addListener((obs, oldVal, newVal) -> {
            table.getItems().forEach(row -> {
                int surface = row.getSurface().getValue();
                float o = "".equals(oldVal) ? 0 : Float.parseFloat(oldVal);
                float n = "".equals(newVal) ? 0 : Float.parseFloat(newVal);
                
                if ((int)(surface * o) == row.getPaintPrice().getValue()) {
                    ColoredValue.ColorType c = row.paintPriceProperty().get().getColor();
                    row.setPaintPrice(new ColoredValue<>((int)(surface * n), c));
                    updateSumColumns(row);
                }
                
                updateConstants();
                focusTable();
            });
        });
        sheetPrice.textProperty().addListener((obs, oldVal, newVal) -> {
            table.getItems().forEach(row -> {
                int surface = row.getSurface().getValue();
                float o = "".equals(oldVal) ? 0 : Float.parseFloat(oldVal);
                float n = "".equals(newVal) ? 0 : Float.parseFloat(newVal);
                
                if ((int)(surface * o) == row.getSheetPrice().getValue()) {
                    ColoredValue.ColorType c = row.sheetPriceProperty().get().getColor();
                    row.setSheetPrice(new ColoredValue<>((int)(surface * n), c));
                    updateSumColumns(row);
                }
                
                updateConstants();
                focusTable();
            });
        });
        
        // Set on key pressed event handler for data table
        table.setOnKeyPressed(event -> {
            TablePosition<CostDataRow, ?> pos = table.getFocusModel().getFocusedCell() ;
            if (pos != null) {
                table.edit(pos.getRow(), pos.getTableColumn());
            }
        });
        
        // Update summary labels
        updateSumLabels();
        focusTable();
    }
    
    /**
     * Set table on focus.
     */
    private void focusTable() {
        Platform.runLater(new Runnable() {
            public void run() {
                table.requestFocus();
            }
        });
    }
    
    /**
     * Update summary labels.
     */
    private void updateSumLabels() {
        int tcSum = 0;
        int bpSum = 0;
        int gSum  = 0;
        int pmSum = 0;
        ObservableList<CostDataRow> rows = table.getItems();
        
        for (int i = 0; i < rows.size(); i++) {
            tcSum += rows.get(i).getTotalCosts().getValue();
            bpSum += rows.get(i).getBillPrice().getValue();
            gSum  += rows.get(i).getGain().getValue();
            pmSum += rows.get(i).getProfitMargin().getValue();
        }
        
        pmSum = (int)(pmSum / rows.size());
        
        totalCostsSum.setText(String.format("%,d Kč", tcSum));
        billPriceSum.setText(String.format("%,d Kč", bpSum));
        gainSum.setText(String.format("%,d Kč", gSum));
        profitMarginSum.setText(String.format("%,d %%", pmSum));
    }

    /**
     * Holds invormations about action for undo.
     */
    private class UndoAction {
        private final UndoActions type;
        private final Integer month;
        private final CostDataRow originalData;
        private final CostDataRow updatedData;
        private final Integer originalRow;
        private final Integer updatedRow;
        private final ArrayList<ColorChange> colorChanges;
        private final ColoredValue<?> value;
        private final String column;

        /**
         * Constructs action type INSERT.
         * @param type Action's type.
         * @param originalData Original data row.
         * @throws IllegalArgumentException
         */
        public UndoAction(UndoActions type, CostDataRow originalData) {
            if (!type.equals(UndoActions.INSERT) && !type.equals(UndoActions.COPY)) {
                throw new IllegalArgumentException("Action type should be INSERT or COPY!");
            }
            this.type = type;
            this.month = null;
            this.originalData = originalData;
            this.updatedData = null;
            this.originalRow = null;
            this.updatedRow = null;
            this.colorChanges = null;
            this.value = null;
            this.column = null;
        }

        /**
         * Constructs action type REMOVE.
         * @param type Action's type.
         * @param originalData Original data row.
         * @param originalRow Original row's index.
         * @throws IllegalArgumentException
         */
        public UndoAction(UndoActions type, CostDataRow originalData, Integer originalRow) {
            if (!type.equals(UndoActions.REMOVE)) {
                throw new IllegalArgumentException("Action type should be REMOVE!");
            }
            this.type = type;
            this.month = null;
            this.originalData = originalData;
            this.updatedData = null;
            this.originalRow = originalRow;
            this.updatedRow = null;
            this.colorChanges = null;
            this.value = null;
            this.column = null;
        }

        public UndoAction(UndoActions type, int month, int originalRow, int updatedRow) {
            if (!type.equals(UndoActions.MOVEDOWN) && !type.equals(UndoActions.MOVEUP)) {
                throw new IllegalArgumentException("Action type should be REMOVE!");
            }
            this.type = type;
            this.month = month;
            this.originalData = null;
            this.updatedData = null;
            this.originalRow = originalRow;
            this.updatedRow = updatedRow;
            this.colorChanges = null;
            this.value = null;
            this.column = null;
        }

        /**
         * Constructs action type MOVE.
         * @param type Action's type.
         * @param originalData Original data row.
         * @param updatedData Updated data row.
         * @throws IllegalArgumentException
         */
        public UndoAction(UndoActions type, CostDataRow originalData, CostDataRow updatedData) {
            if (!type.equals(UndoActions.MOVE)) {
                throw new IllegalArgumentException("Action type should be MOVE!");
            }
            this.type = type;
            this.month = null;
            this.originalData = originalData;
            this.originalRow = null;
            this.updatedData = updatedData;
            this.updatedRow = null;
            this.colorChanges = null;
            this.value = null;
            this.column = null;
        }

        /**
         * Constructs action type COLOR.
         * @param type Action's type.
         * @param colorChanges Color changes.
         * @throws IllegalArgumentException
         */
        public UndoAction(UndoActions type, ArrayList<ColorChange> colorChanges) {
            if (!type.equals(UndoActions.COLOR)) {
                throw new IllegalArgumentException("Action type should be COLOR!");
            }
            this.type = type;
            this.month = null;
            this.originalData = null;
            this.updatedData = null;
            this.originalRow = null;
            this.updatedRow = null;
            this.colorChanges = colorChanges;
            this.value = null;
            this.column = null;
        }

        public UndoAction(UndoActions type, int row, String column, ColoredValue<?> value) {
            if (!type.equals(UndoActions.UPDATE)) {
                throw new IllegalArgumentException("Action type should be UPDATE!");
            }
            this.type = type;
            this.month = getSelectedMonthIndex();
            this.originalData = null;
            this.updatedData = null;
            this.originalRow = row;
            this.updatedRow = null;
            this.colorChanges = null;
            this.value = value;
            this.column = column;
        }

        public UndoActions getType() {
            return type;
        }

        public Integer getMonth() {
            return month;
        }

        public CostDataRow getOriginalData() {
            return originalData;
        }

        public Integer getOriginalRow() {
            return originalRow;
        }

        public CostDataRow getUpdatedData() {
            return updatedData;
        }

        public Integer getUpdatedRow() {
            return updatedRow;
        }

        public ArrayList<ColorChange> getColorChanges() {
            return colorChanges;
        }

        public ColoredValue<?> getValue() {
            return value;
        }

        public String getColumn() {
            return column;
        }
    }

    /**
     * Helper class that holds record about color change (because of undo).
     */
    private class ColorChange {
        private final Integer row;
        private final String column;
        private final ColoredValue.ColorType oldColor;
        private final ColoredValue.ColorType newColor;
        public ColorChange(Integer row, String column, ColoredValue.ColorType oldColor, ColoredValue.ColorType  newColor) {
            this.row = row;
            this.column = column;
            this.oldColor = oldColor;
            this.newColor = newColor;
        }
        public Integer getRow() {
            return row;
        }
        public String getColumn() {
            return column;
        }
        public ColoredValue.ColorType getOldColor() {
            return oldColor;
        }
        public ColoredValue.ColorType getNewColor() {
            return newColor;
        }
    }

    /**
     * Updates buttons and menuitems related to undo actions.
     */
    private void updateUndoUi() {
        undoButton.setDisable(undo.isEmpty());
        undoMenuItem.setDisable(undo.isEmpty());
    }

    /**
     * Recalculates values in row after value of surface column is set.
     * @param row
     */
    private void recalculateRowAfterSurface(CostDataRow row) {
        int surface = row.getSurface().getValue();
        int price = (int)(surface * getWorkPriceConstant());
        if (price < 12000 && surface > 0) {
            price = 12000;
        }
        ColoredValue.ColorType wpc = row.workPriceProperty().get().getColor();
        row.setWorkPrice(new ColoredValue<>(price, wpc));
        // Calculate pour price
        ColoredValue.ColorType ppc = row.pourPriceProperty().get().getColor();
        row.setPourPrice(new ColoredValue<>((int)(surface * getPourPriceConstant()), ppc));
        // Calculate paint price
        ColoredValue.ColorType papc = row.paintPriceProperty().get().getColor();
        row.setPaintPrice(new ColoredValue<>((int)(surface * getPaintPriceConstant()), papc));
        // Calculate sheet price
        ColoredValue.ColorType spc = row.sheetPriceProperty().get().getColor();
        row.setSheetPrice(new ColoredValue<>((int)(surface * getSheetPriceConstant()), spc));
    }

    /**
     * Recalculates values in row after value of wire weight column is set.
     * @param row
     */
    private void recalculateRowAfterWireWeight(CostDataRow row) {
        int weight = row.getWireWeight().getValue();
        // Calculate wire price
        ColoredValue.ColorType wpc = row.wirePriceProperty().get().getColor();
        row.setWirePrice(new ColoredValue<>((int)(weight * getWirePriceConstant()), wpc));
    }
}
