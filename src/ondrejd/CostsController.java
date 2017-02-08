/**
 * @author Ondřej Doněk <ondrejd@gmail.com>
 * @link https://github.com/ondrejd/costs-javafx-app for the canonical source repository
 * @license https://www.gnu.org/licenses/gpl-3.0.en.html GNU General Public License 3.0
 */

package ondrejd;

import java.net.URL;
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
    private Button redoButton;
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
    private MenuItem redoMenuItem;
    @FXML
    private MenuItem addRowMenuItem;
    @FXML
    private MenuItem delRowMenuItem;

    /**
     * Holds actions for undo/redo.
     */
    private ObservableList<UndoRedoDataItem> undoRedoQueue = FXCollections.<UndoRedoDataItem>observableArrayList();
    /**
     * Holds current undo/redo queue position.
     */
    private Integer undoRedoPosition = 0;

    @FXML
    private void handleUndoButtonAction(ActionEvent event) {
        if (undoRedoQueue.isEmpty() || undoRedoPosition == 0) {
            System.out.println("There is no action that can be undone.");
            return;
        }

        // Get action that should be undone
        UndoRedoDataItem action = undoRedoQueue.get(undoRedoPosition - 1);

        // According to action type do what is necessarry
        if (action.getAction().equals(UndoRedoDataItem.INSERT)) {
            try {
                //CostDataRow row = table.getItems().get(action.getOriginalRow());
                //data.remove(row);
                data.remove(action.getOriginalData());
            } catch (IndexOutOfBoundsException e) {
                // Do nothing...
            }
        }
        else if (action.getAction().equals(UndoRedoDataItem.REMOVE)) {
            CostDataRow row = action.getOriginalData();
            try {
                data.add(action.getOriginalRow(), row);
            } catch (IndexOutOfBoundsException e) {
                data.add(row);
            }
        }
        else if (action.getAction().equals(UndoRedoDataItem.UPDATE)) {
            // ...
        }

        // Move queue position and update UI
        undoRedoPosition -= 1;
        updateUndoRedoUi();
    }

    @FXML
    private void handleRedoButtonAction(ActionEvent event) {
        if (undoRedoQueue.isEmpty() || undoRedoPosition == undoRedoQueue.size()) {
            System.out.println("There is no action that can be redone.");
            return;
        }

        // Get action that should be redone
        UndoRedoDataItem action = undoRedoQueue.get(undoRedoPosition - 1);

        // According to action type do what is necessarry
        if (action.getAction().equals(UndoRedoDataItem.INSERT)) {
            CostDataRow row = action.getOriginalData();
            try {
                data.add(action.getOriginalRow(), row);
            } catch (IndexOutOfBoundsException e) {
                data.add(row);
            }
        }
        else if (action.getAction().equals(UndoRedoDataItem.REMOVE)) {
            try {
                //CostDataRow row = table.getItems().get(action.getOriginalRow());
                //data.remove(row);
                data.remove(action.getOriginalData());
            } catch (IndexOutOfBoundsException e) {
                // Do nothing...
            }
        }
        else if (action.getAction().equals(UndoRedoDataItem.UPDATE)) {
            // ...
        }

        // Move queue position and update UI
        undoRedoPosition += 1;
        updateUndoRedoUi();
    }
    
    @FXML
    private void handleAddRowButtonAction(ActionEvent event) {
        int month = getSelectedMonthIndex();
        CostDataRow row = new CostDataRow(month, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        data.add(row);
        undoRedoQueue.add(new UndoRedoDataItem(UndoRedoDataItem.INSERT, row, data.size()));
    }
    
    @FXML
    private void handleDelRowButtonAction(ActionEvent event) {
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
            // TODO Row index is from selection (current table view) not from data!!!
            undoRedoQueue.add(new UndoRedoDataItem(UndoRedoDataItem.REMOVE, row, idx));
        }
    }
    
    @FXML
    private void handleYellowButtonAction(ActionEvent event) {
        table.getSelectionModel().getSelectedCells().forEach((pos) -> {
            CostDataRow cdr = table.getItems().get(pos.getRow());
            if (pos.getTableColumn() == placeTCol) {
                cdr.getPlace().setColor(ColoredValue.ColorType.YELLOW);
            } else if (pos.getTableColumn() == surfaceTCol) {
                cdr.getSurface().setColor(ColoredValue.ColorType.YELLOW);
            } else if (pos.getTableColumn() == workPriceTCol) {
                cdr.getWorkPrice().setColor(ColoredValue.ColorType.YELLOW);
            } else if (pos.getTableColumn() == wireWeightTCol) {
                cdr.getWireWeight().setColor(ColoredValue.ColorType.YELLOW);
            } else if (pos.getTableColumn() == wirePriceTCol) {
                cdr.getWirePrice().setColor(ColoredValue.ColorType.YELLOW);
            } else if (pos.getTableColumn() == pourPriceTCol) {
                cdr.getPourPrice().setColor(ColoredValue.ColorType.YELLOW);
            } else if (pos.getTableColumn() == paintPriceTCol) {
                cdr.getPaintPrice().setColor(ColoredValue.ColorType.YELLOW);
            } else if (pos.getTableColumn() == sheetPriceTCol) {
                cdr.getSheetPrice().setColor(ColoredValue.ColorType.YELLOW);
            } else if (pos.getTableColumn() == concretePriceTCol) {
                cdr.getConcretePrice().setColor(ColoredValue.ColorType.YELLOW);
            } else if (pos.getTableColumn() == pumpPriceTCol) {
                cdr.getPumpPrice().setColor(ColoredValue.ColorType.YELLOW);
            } else if (pos.getTableColumn() == squarePriceTCol) {
                cdr.getSquarePrice().setColor(ColoredValue.ColorType.YELLOW);
            } else if (pos.getTableColumn() == totalCostsTCol) {
                cdr.getTotalCosts().setColor(ColoredValue.ColorType.YELLOW);
            } else if (pos.getTableColumn() == billPriceTCol) {
                cdr.getBillPrice().setColor(ColoredValue.ColorType.YELLOW);
            } else if (pos.getTableColumn() == gainTCol) {
                cdr.getGain().setColor(ColoredValue.ColorType.YELLOW);
            } else if (pos.getTableColumn() == profitMarginTCol) {
                cdr.getProfitMargin().setColor(ColoredValue.ColorType.YELLOW);
            }
        });
    }
    
    @FXML
    private void handleRedButtonAction(ActionEvent event) {
        table.getSelectionModel().getSelectedCells().forEach((pos) -> {
            CostDataRow cdr = table.getItems().get(pos.getRow());
            if (pos.getTableColumn() == placeTCol) {
                cdr.getPlace().setColor(ColoredValue.ColorType.RED);
            } else if (pos.getTableColumn() == surfaceTCol) {
                cdr.getSurface().setColor(ColoredValue.ColorType.RED);
            } else if (pos.getTableColumn() == workPriceTCol) {
                cdr.getWorkPrice().setColor(ColoredValue.ColorType.RED);
            } else if (pos.getTableColumn() == wireWeightTCol) {
                cdr.getWireWeight().setColor(ColoredValue.ColorType.RED);
            } else if (pos.getTableColumn() == wirePriceTCol) {
                cdr.getWirePrice().setColor(ColoredValue.ColorType.RED);
            } else if (pos.getTableColumn() == pourPriceTCol) {
                cdr.getPourPrice().setColor(ColoredValue.ColorType.RED);
            } else if (pos.getTableColumn() == paintPriceTCol) {
                cdr.getPaintPrice().setColor(ColoredValue.ColorType.RED);
            } else if (pos.getTableColumn() == sheetPriceTCol) {
                cdr.getSheetPrice().setColor(ColoredValue.ColorType.RED);
            } else if (pos.getTableColumn() == concretePriceTCol) {
                cdr.getConcretePrice().setColor(ColoredValue.ColorType.RED);
            } else if (pos.getTableColumn() == pumpPriceTCol) {
                cdr.getPumpPrice().setColor(ColoredValue.ColorType.RED);
            } else if (pos.getTableColumn() == squarePriceTCol) {
                cdr.getSquarePrice().setColor(ColoredValue.ColorType.RED);
            } else if (pos.getTableColumn() == totalCostsTCol) {
                cdr.getTotalCosts().setColor(ColoredValue.ColorType.RED);
            } else if (pos.getTableColumn() == billPriceTCol) {
                cdr.getBillPrice().setColor(ColoredValue.ColorType.RED);
            } else if (pos.getTableColumn() == gainTCol) {
                cdr.getGain().setColor(ColoredValue.ColorType.RED);
            } else if (pos.getTableColumn() == profitMarginTCol) {
                cdr.getProfitMargin().setColor(ColoredValue.ColorType.RED);
            }
        });
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
            e.printStackTrace();
            System.out.print("Exception occured when saving user preferences!");
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
    private double getWorkPriceConstant() {
        return Double.parseDouble(workPrice.getText());
    }
    
    /**
     * @return Returns constant for calculating wire price.
     */
    private double getWirePriceConstant() {
        return Double.parseDouble(wirePrice.getText());
    }
    
    /**
     * @return Returns constant for calculating pour price.
     */
    private double getPourPriceConstant() {
        return Double.parseDouble(pourPrice.getText());
    }
    
    /**
     * @return Returns constant for calculating paint price.
     */
    private double getPaintPriceConstant() {
        return Double.parseDouble(paintPrice.getText());
    }
    
    /**
     * @return Returns constant for calculating sheet price.
     */
    private double getSheetPriceConstant() {
        return Double.parseDouble(sheetPrice.getText());
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
                        replace(" Kg", "").replace(" %", "").replace(" ", "");
                T value = supplier.apply("".equals(s) ? "0" : s);
                ColoredValue.ColorType c = cell.getItem() == null 
                        ? ColoredValue.ColorType.NOCOLOR 
                        : cell.getItem().getColor();
                return new ColoredValue<>(value, c);
            }

        });

        ChangeListener<ColoredValue.ColorType> valListener = (obs, oldState, newState) -> {
            if (newState == null || newState == ColoredValue.ColorType.NOCOLOR) {
                cell.setStyle("");
            } else if (newState == ColoredValue.ColorType.YELLOW) {
                cell.setStyle("-fx-background-color: yellow ;");
            } else if (newState == ColoredValue.ColorType.RED) {
                cell.setStyle("-fx-background-color: red ;");
            } else if (newState == ColoredValue.ColorType.GREEN) {
                cell.setStyle("-fx-background-color: #cbe2ae ;");
            }
        };
        
        cell.itemProperty().addListener((obs, oldItem, newItem) -> {
            if (oldItem != null) {
                oldItem.colorProperty().removeListener(valListener);
            }
            if (newItem == null) {
                cell.setStyle("");
            } else {
                if (newItem.getColor() == null || newItem.getColor() == ColoredValue.ColorType.NOCOLOR) {
                    cell.setStyle("");
                } else if (newItem.getColor() == ColoredValue.ColorType.YELLOW) {
                    cell.setStyle("-fx-background-color: yellow ;");
                } else if (newItem.getColor() == ColoredValue.ColorType.RED) {
                    cell.setStyle("-fx-background-color: red ;");
                } else if (newItem.getColor() == ColoredValue.ColorType.GREEN) {
                    cell.setStyle("-fx-background-color: #cbe2ae ;");
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
            double pm = ((double) g / (double) bp) * 100;
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
        workPrice.setText(Double.toString(constants.getWorkPrice()));
        wirePrice.setText(Double.toString(constants.getWirePrice()));
        pourPrice.setText(Double.toString(constants.getPourPrice()));
        paintPrice.setText(Double.toString(constants.getPaintPrice()));
        sheetPrice.setText(Double.toString(constants.getSheetPrice()));
    }
    
    /**
     * Save changes in price constants into {@link ondrejd.XmlDataSource}.
     */
    private void updateConstants() {
        int month = getSelectedMonthIndex();
        MonthConstants consts = XmlDataSource.getConstants(month);
        consts.setWorkPrice(Double.parseDouble(workPrice.getText()));
        consts.setWirePrice(Double.parseDouble(wirePrice.getText()));
        consts.setPourPrice(Double.parseDouble(pourPrice.getText()));
        consts.setPaintPrice(Double.parseDouble(paintPrice.getText()));
        consts.setSheetPrice(Double.parseDouble(sheetPrice.getText()));
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
        CostDataRow row = (CostDataRow) table.getItems().get(idx);
        if(deleteOriginalRow == true) {
            data.remove(row);
        }
        // 3) Insert row into target month
        data.add(new CostDataRow(monthIdx, 
                row.getPlace().getValue(), 
                row.getSurface().getValue(), 
                row.getWorkPrice().getValue(),
                row.getWireWeight().getValue(),
                row.getWirePrice().getValue(),
                row.getPourPrice().getValue(),
                row.getPaintPrice().getValue(),
                row.getSheetPrice().getValue(),
                row.getConcretePrice().getValue(),
                row.getPumpPrice().getValue(),
                row.getSquarePrice().getValue(),
                row.getTotalCosts().getValue(),
                row.getBillPrice().getValue(),
                row.getGain().getValue(),
                row.getProfitMargin().getValue()));
        // 4) Switch to target month
        monthsComboBox.getSelectionModel().clearAndSelect(monthIdx);
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
        Image iconRedo   = new Image("resources/graphics/arrow_redo.png");
        Image iconAddRow = new Image("resources/graphics/table_row_insert.png");
        Image iconDelRow = new Image("resources/graphics/table_row_delete.png");
        undoButton.setGraphic(new ImageView(iconUndo));
        redoButton.setGraphic(new ImageView(iconRedo));
        addRowButton.setGraphic(new ImageView(iconAddRow));
        delRowButton.setGraphic(new ImageView(iconDelRow));

        // Set up popup menuitems icons
        undoMenuItem.setGraphic(new ImageView(iconUndo));
        redoMenuItem.setGraphic(new ImageView(iconRedo));
        addRowMenuItem.setGraphic(new ImageView(iconAddRow));
        delRowMenuItem.setGraphic(new ImageView(iconDelRow));

        // Set up undo/redo queue
        undoRedoQueue.addListener((ListChangeListener<UndoRedoDataItem>) change -> {
            change.next();
            if (change.wasAdded() || change.wasRemoved()) {
                undoRedoPosition = undoRedoQueue.size();
                updateUndoRedoUi();
            }
        });

        // Disable undo/redo buttons/menuitems immediately because queue is empty
        undoButton.setDisable(true);
        redoButton.setDisable(true);
        undoMenuItem.setDisable(true);
        redoMenuItem.setDisable(true);

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
        surfaceTCol.setCellValueFactory(cellData -> cellData.getValue().surfaceProperty());
        surfaceTCol.setCellFactory(tc -> createTableCell("%,d m2", Integer::new));
        surfaceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, ColoredValue<Integer>>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, ColoredValue<Integer>> e) {
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(
                            e.getTablePosition().getRow());
                    row.setSurface(e.getNewValue());
                    int s = e.getNewValue().getValue();
                    // Calculate work price
                    int price = (int)(s * getWorkPriceConstant());
                    if (price < 12000) {
                        price = 12000;
                    }
                    ColoredValue.ColorType wpc = row.workPriceProperty().get().getColor();
                    row.setWorkPrice(new ColoredValue<>(price, wpc));
                    // Calculate pour price
                    ColoredValue.ColorType ppc = row.pourPriceProperty().get().getColor();
                    row.setPourPrice(new ColoredValue<>((int)(s * getPourPriceConstant()), ppc));
                    // Calculate paint price
                    ColoredValue.ColorType papc = row.paintPriceProperty().get().getColor();
                    row.setPaintPrice(new ColoredValue<>((int)(s * getPaintPriceConstant()), papc));
                    // Calculate sheet price
                    ColoredValue.ColorType spc = row.sheetPriceProperty().get().getColor();
                    row.setSheetPrice(new ColoredValue<>((int)(s * getSheetPriceConstant()), spc));
                    // Update total costs, gain, profitMargin.
                    updateSumColumns(row);
                    // Refresh table view
                    e.getTableView().refresh();
                    // Set focus on the table
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
                    //int price = e.getNewValue().getValue();
                    //if (price < 12000) {
                    //    price = 12000;
                    //}
                    //CostDataRow row = (CostDataRow) e.getTableView().getItems().get(
                    //        e.getTablePosition().getRow());
                    //row.setWorkPrice(new ColoredValue<>(price, row.workPriceProperty().get().getColor()));
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(
                            e.getTablePosition().getRow());
                    row.setWorkPrice(e.getNewValue());
                    updateSumColumns(row);
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
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(
                            e.getTablePosition().getRow());
                    row.setWireWeight(e.getNewValue());
                    int weight = e.getNewValue().getValue();
                    // Calculate wire price
                    ColoredValue.ColorType wpc = row.wirePriceProperty().get().getColor();
                    row.setWirePrice(new ColoredValue<>((int)(weight * getWirePriceConstant()), wpc));
                    // Update all
                    updateSumColumns(row);
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
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(
                            e.getTablePosition().getRow());
                    row.setWirePrice(e.getNewValue());
                    updateSumColumns(row);
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
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(
                            e.getTablePosition().getRow());
                    row.setPourPrice(e.getNewValue());
                    updateSumColumns(row);
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
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(
                            e.getTablePosition().getRow());
                    row.setPaintPrice(e.getNewValue());
                    updateSumColumns(row);
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
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(
                            e.getTablePosition().getRow());
                    row.setSheetPrice(e.getNewValue());
                    updateSumColumns(row);
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
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(
                            e.getTablePosition().getRow());
                    row.setConcretePrice(e.getNewValue());
                    updateSumColumns(row);
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
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(
                            e.getTablePosition().getRow());
                    row.setPumpPrice(e.getNewValue());
                    updateSumColumns(row);
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
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(
                            e.getTablePosition().getRow());
                    row.setSquarePrice(e.getNewValue());
                    updateSumColumns(row);
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
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(
                            e.getTablePosition().getRow());
                    row.setBillPrice(e.getNewValue());
                    updateSumColumns(row);
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
                double o = "".equals(oldVal) ? 0 : Double.parseDouble(oldVal);
                double n = "".equals(newVal) ? 0 : Double.parseDouble(newVal);
                
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
                double o = "".equals(oldVal) ? 0 : Double.parseDouble(oldVal);
                double n = "".equals(newVal) ? 0 : Double.parseDouble(newVal);
                
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
                double o = "".equals(oldVal) ? 0 : Double.parseDouble(oldVal);
                double n = "".equals(newVal) ? 0 : Double.parseDouble(newVal);
                
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
                double o = "".equals(oldVal) ? 0 : Double.parseDouble(oldVal);
                double n = "".equals(newVal) ? 0 : Double.parseDouble(newVal);
                
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
                double o = "".equals(oldVal) ? 0 : Double.parseDouble(oldVal);
                double n = "".equals(newVal) ? 0 : Double.parseDouble(newVal);
                
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
     * Holds invormations about action for undo/redo.
     */
    private class UndoRedoDataItem {
        // All possible actions (with additional "unknown" action).
        public static final String INSERT  = "insert";
        public static final String REMOVE  = "remove";
        public static final String UPDATE  = "update";
        public static final String UNKNOWN = "unknown";

        private String action;
        private CostDataRow originalData;
        private CostDataRow updatedData;
        private Integer originalRow;
        private Integer updatedRow;

        public UndoRedoDataItem(String action) {
            setAction(action);
        }

        public UndoRedoDataItem(String action, CostDataRow originalData) {
            setAction(action);
            this.originalData = originalData;
        }

        public UndoRedoDataItem(String action, CostDataRow originalData, Integer originalRow) {
            setAction(action);
            this.originalData = originalData;
            this.originalRow = originalRow;
        }

        public UndoRedoDataItem(String action, CostDataRow originalData, 
                Integer originalRow, CostDataRow updatedData, Integer updatedRow) {
            setAction(action);
            this.originalData = originalData;
            this.originalRow = originalRow;
            this.updatedData = updatedData;
            this.updatedRow = updatedRow;
        }

        public String getAction() {
            return action;
        }

        public final void setAction(String action) {
            if ( !action.equals(INSERT) && !action.equals(REMOVE) && !action.equals(UPDATE)) {
                this.action = UNKNOWN;
                return;
            }
            this.action = action;
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
    }

    /**
     * Updates buttons and menuitems related to undo/redo actions.
     */
    private void updateUndoRedoUi() {
        if (undoRedoPosition > undoRedoQueue.size()) {
            undoRedoPosition = undoRedoQueue.size();
        }
        else if (undoRedoPosition < 0) {
            undoRedoPosition = 0;
        }
        if (undoRedoQueue.isEmpty()) {
            undoButton.setDisable(true);
            redoButton.setDisable(true);
            undoMenuItem.setDisable(true);
            redoMenuItem.setDisable(true);
        }
        else {
            boolean undoDisabled = (undoRedoPosition == 0);
            boolean redoDisabled = ! (undoRedoQueue.size() >= undoRedoPosition);

            undoButton.setDisable(undoDisabled);
            redoButton.setDisable(redoDisabled);
            undoMenuItem.setDisable(undoDisabled);
            redoMenuItem.setDisable(redoDisabled);
        }
        // TODO Remove this!
        System.out.println("Current items in undo/redo queue is " +
                undoRedoQueue.size() + ". Current position is " +
                undoRedoPosition + ".");
    }
}
