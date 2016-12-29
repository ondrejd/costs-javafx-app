/**
 * @author Ondřej Doněk <ondrejd@gmail.com>
 * @link https://github.com/ondrejd/costs-javafx-app for the canonical source repository
 * @license https://www.gnu.org/licenses/gpl-3.0.en.html GNU General Public License 3.0
 */

package ondrejd;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

public class CostsController implements Initializable {
    
    protected static ObservableList<CostDataRow> data = FXCollections.<CostDataRow>observableArrayList(
            new CostDataRow("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    );
    
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
    private Button addRowButton;
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
    private void handleAddRowButtonAction(ActionEvent event) {
        data.add(
                new CostDataRow("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        );
        table.refresh();
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
        int idx = 0;
        switch(monthsComboBox.getValue().toString()) {
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
        System.out.println("Selected month: " + idx);
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
                T value = supplier.apply(string);
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
        ColoredValue.ColorType pmc = row.profitMarginProperty().get().getColor();
        
        if (tcv == 0 || g == 0) {
            row.setProfitMargin(new ColoredValue<>(0, pmc));
        } else {
            row.setProfitMargin(new ColoredValue<>((int)(g / (tcv / 100)), pmc));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up months combobox
        ObservableList<String> months = FXCollections.observableArrayList(
                "Leden", "Únor", "Březen", "Duben", "Květen", "Červen", "Červenec",
                "Srpen", "Září", "Říjen", "Listopad", "Prosinec");
        monthsComboBox.setItems(months);
        monthsComboBox.getSelectionModel().selectFirst();
        
        // Set up data table
        FilteredList<CostDataRow> filteredData = new FilteredList<>(data, n -> true);
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
                    ColoredValue.ColorType wpc = row.workPriceProperty().get().getColor();
                    row.setWorkPrice(new ColoredValue<>((int)(s * getWorkPriceConstant()), wpc));
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
                }
            }
        );
        workPriceTCol.setCellValueFactory(cellData -> cellData.getValue().workPriceProperty());
        workPriceTCol.setCellFactory(tc -> createTableCell("%,d Kč", Integer::new));
        workPriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, ColoredValue<Integer>>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, ColoredValue<Integer>> e) {
                    CostDataRow row = (CostDataRow) e.getTableView().getItems().get(
                            e.getTablePosition().getRow());
                    row.setWorkPrice(e.getNewValue());
                    // Update total costs, gain, profitMargin.
                    updateSumColumns(row);
                    // Refresh table view
                    e.getTableView().refresh();
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
                    // Update total costs, gain, profitMargin.
                    updateSumColumns(row);
                    // Refresh table view
                    e.getTableView().refresh();
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
            });
        });
        
        // TODO ....
    }
    
}
