/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ondrejd;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.TablePosition;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

/**
 *
 * @author ondrejd
 */
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
    private TableColumn/*<FormTokens, String>*/ placeTCol;
    @FXML
    private TableColumn surfaceTCol;
    @FXML
    private TableColumn workPriceTCol;
    @FXML
    private TableColumn wireWeightTCol;
    @FXML
    private TableColumn wirePriceTCol;
    @FXML
    private TableColumn pourPriceTCol;
    @FXML
    private TableColumn paintPriceTCol;
    @FXML
    private TableColumn sheetPriceTCol;
    @FXML
    private TableColumn concretePriceTCol;
    @FXML
    private TableColumn pumpPriceTCol;
    @FXML
    private TableColumn squarePriceTCol;
    @FXML
    private TableColumn totalCostsTCol;
    @FXML
    private TableColumn billPriceTCol;
    @FXML
    private TableColumn gainTCol;
    @FXML
    private TableColumn profitMarginTCol;
    
    @FXML
    private void handleAddRowButtonAction(ActionEvent event) {
        data.add(
                new CostDataRow("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        );
        table.refresh();
    }
    
    @FXML
    private void handleYellowButtonAction(ActionEvent event) {
        System.out.println("You clicked me - YELLOW!");
        table.getSelectionModel().getSelectedCells().forEach(pos -> {
            //int row = pos.getRow();
            //int col = pos.getColumn();
            //pos.setStyle("-fx-background-color: yellow");
        });
    }
    
    @FXML
    private void handleRedButtonAction(ActionEvent event) {
        System.out.println("You clicked me - RED!");
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
        placeTCol.setCellValueFactory(new PropertyValueFactory<>("place"));
        placeTCol.setCellFactory(TextFieldTableCell.forTableColumn());
        placeTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, String>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, String> t) {
                    ((CostDataRow) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setPlace(t.getNewValue());
                }
            }
        );
        surfaceTCol.setCellValueFactory(new PropertyValueFactory<>("surface"));
        surfaceTCol.setCellFactory(TextFieldTableCell.<CostDataRow, Integer>forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(final Integer value) {
                return value.toString() + " m2";
            }
            @Override
            public Integer fromString(final String s) {
                return Integer.parseInt(s.replace(" m2", ""));
            }
        }));
        surfaceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, Integer>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, Integer> t) {
                    CostDataRow row = (CostDataRow) t.getTableView().getItems().get(
                            t.getTablePosition().getRow());
                    int surface = t.getNewValue();
                    row.setSurface(t.getNewValue());
                    // Calculate work price
                    row.setWorkPrice((int)(surface * getWorkPriceConstant()));
                    // Calculate pour price
                    row.setPourPrice((int)(surface * getPourPriceConstant()));
                    // Calculate paint price
                    row.setPaintPrice((int)(surface * getPaintPriceConstant()));
                    // Calculate sheet price
                    row.setSheetPrice((int)(surface * getSheetPriceConstant()));
                    // TODO Update total costs, gain, profitMargin.
                    // Refresh table view
                    t.getTableView().refresh();
                }
            }
        );
        workPriceTCol.setCellValueFactory(new PropertyValueFactory<>("workPrice"));
        workPriceTCol.setCellFactory(tc -> {
            TableCell<CostDataRow, ColoredValue<Integer>> cell = new TextFieldTableCell<>(new StringConverter<ColoredValue<Integer>>() {
                @Override
                public String toString(final ColoredValue<Integer> value) {
                    return Integer.toString(value.getValue()) + " Kč";
                }
                @Override
                public ColoredValue<Integer> fromString(final String s) {
                    return new ColoredValue<>(new Integer(s));
                }
            });
           ChangeListener<String> coloredValListener = (obs, oldVal, newVal) -> {
                if("".equals(newVal)) {
                    cell.getStyleClass().remove("yellow-color");
                    cell.getStyleClass().remove("red-color");
                }
                else if("Y".equals(newVal)) {
                    cell.getStyleClass().remove("red-color");
                    cell.getStyleClass().add("yellow-color");
                }
                else if("R".equals(newVal)) {
                    cell.getStyleClass().remove("yellow-color");
                    cell.getStyleClass().add("red-color");
                }
            };
            cell.itemProperty().addListener((obs, oldVal, newVal) -> {
                if(oldVal != null) {
                    oldVal.getColor().removeListener(coloredValListener);
                }
                if(newVal == null) {
                    cell.getStyleClass().remove("yellow-color");
                    cell.getStyleClass().remove("red-color");
                } else {
                    if("".equals(newVal.getColor().get())) {
                        cell.getStyleClass().remove("yellow-color");
                        cell.getStyleClass().remove("red-color");
                    }
                    else if("Y".equals(newVal.getColor().get())) {
                        cell.getStyleClass().remove("red-color");
                        cell.getStyleClass().add("yellow-color");
                    }
                    else if("R".equals(newVal.getColor().get())) {
                        cell.getStyleClass().remove("yellow-color");
                        cell.getStyleClass().add("red-color");
                    }
                    newVal.getColor().addListener(coloredValListener);
                }
            });

            return cell;
        });
        workPriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, Integer>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, Integer> t) {
                    ((CostDataRow) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setWorkPrice(t.getNewValue());
                    // Refresh table view (because of total costs)
                    t.getTableView().refresh();
                }
            }
        );
        wireWeightTCol.setCellValueFactory(new PropertyValueFactory<>("wireWeight"));
        wireWeightTCol.setCellFactory(TextFieldTableCell.<CostDataRow, Integer>forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(final Integer value) {
                return value.toString() + " kg";
            }
            @Override
            public Integer fromString(final String s) {
                return Integer.parseInt(s.replace(" kg", ""));
            }
        }));
        wireWeightTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, Integer>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, Integer> t) {
                    CostDataRow row = (CostDataRow) t.getTableView().getItems().get(
                            t.getTablePosition().getRow());
                    int weight = t.getNewValue();
                    row.setWireWeight(t.getNewValue());
                    // Calculate wire price
                    row.setWirePrice((int)(weight * getWirePriceConstant()));
                    // TODO Update total costs, gain, profitMargin.
                    // Refresh table view
                    t.getTableView().refresh();
                }
            }
        );
        wirePriceTCol.setCellValueFactory(new PropertyValueFactory<>("wirePrice"));
        wirePriceTCol.setCellFactory(TextFieldTableCell.<CostDataRow, Integer>forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(final Integer value) {
                return value.toString() + " Kč";
            }
            @Override
            public Integer fromString(final String s) {
                return Integer.parseInt(s.replace(" Kč", ""));
            }
        }));
        wirePriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, Integer>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, Integer> t) {
                    ((CostDataRow) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setWirePrice(t.getNewValue());
                    // Refresh table view (because of total costs)
                    t.getTableView().refresh();
                }
            }
        );
        pourPriceTCol.setCellValueFactory(new PropertyValueFactory<>("pourPrice"));
        pourPriceTCol.setCellFactory(TextFieldTableCell.<CostDataRow, Integer>forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(final Integer value) {
                return value.toString() + " Kč";
            }
            @Override
            public Integer fromString(final String s) {
                return Integer.parseInt(s.replace(" Kč", ""));
            }
        }));
        pourPriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, Integer>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, Integer> t) {
                    ((CostDataRow) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setPourPrice(t.getNewValue());
                    // Refresh table view (because of total costs)
                    t.getTableView().refresh();
                }
            }
        );
        paintPriceTCol.setCellValueFactory(new PropertyValueFactory<>("paintPrice"));
        paintPriceTCol.setCellFactory(TextFieldTableCell.<CostDataRow, Integer>forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(final Integer value) {
                return value.toString() + " Kč";
            }
            @Override
            public Integer fromString(final String s) {
                return Integer.parseInt(s.replace(" Kč", ""));
            }
        }));
        paintPriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, Integer>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, Integer> t) {
                    ((CostDataRow) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setPaintPrice(t.getNewValue());
                    // Refresh table view (because of total costs)
                    t.getTableView().refresh();
                }
            }
        );
        sheetPriceTCol.setCellValueFactory(new PropertyValueFactory<>("sheetPrice"));
        sheetPriceTCol.setCellFactory(TextFieldTableCell.<CostDataRow, Integer>forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(final Integer value) {
                return value.toString() + " Kč";
            }
            @Override
            public Integer fromString(final String s) {
                return Integer.parseInt(s.replace(" Kč", ""));
            }
        }));
        sheetPriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, Integer>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, Integer> t) {
                    ((CostDataRow) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setSheetPrice(t.getNewValue());
                    // Refresh table view (because of total costs)
                    t.getTableView().refresh();
                }
            }
        );
        concretePriceTCol.setCellValueFactory(new PropertyValueFactory<>("concretePrice"));
        concretePriceTCol.setCellFactory(TextFieldTableCell.<CostDataRow, Integer>forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(final Integer value) {
                return value.toString() + " Kč";
            }
            @Override
            public Integer fromString(final String s) {
                return Integer.parseInt(s.replace(" Kč", ""));
            }
        }));
        concretePriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, Integer>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, Integer> t) {
                    ((CostDataRow) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setConcretePrice(t.getNewValue());
                    // Refresh table view (because of total costs)
                    t.getTableView().refresh();
                }
            }
        );
        pumpPriceTCol.setCellValueFactory(new PropertyValueFactory<>("pumpPrice"));
        pumpPriceTCol.setCellFactory(TextFieldTableCell.<CostDataRow, Integer>forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(final Integer value) {
                return value.toString() + " Kč";
            }
            @Override
            public Integer fromString(final String s) {
                return Integer.parseInt(s.replace(" Kč", ""));
            }
        }));
        pumpPriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, Integer>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, Integer> t) {
                    ((CostDataRow) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setPumpPrice(t.getNewValue());
                    // Refresh table view (because of total costs)
                    t.getTableView().refresh();
                }
            }
        );
        squarePriceTCol.setCellValueFactory(new PropertyValueFactory<>("squarePrice"));
        squarePriceTCol.setCellFactory(TextFieldTableCell.<CostDataRow, Integer>forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(final Integer value) {
                return value.toString() + " Kč";
            }
            @Override
            public Integer fromString(final String s) {
                return Integer.parseInt(s.replace(" Kč", ""));
            }
        }));
        squarePriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, Integer>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, Integer> t) {
                    ((CostDataRow) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setSquarePrice(t.getNewValue());
                    // Refresh table view (because of total costs)
                    t.getTableView().refresh();
                }
            }
        );
        billPriceTCol.setCellValueFactory(new PropertyValueFactory<>("billPrice"));
        billPriceTCol.setCellFactory(TextFieldTableCell.<CostDataRow, Integer>forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(final Integer value) {
                return value.toString() + " Kč";
            }
            @Override
            public Integer fromString(final String s) {
                return Integer.parseInt(s.replace(" Kč", ""));
            }
        }));
        billPriceTCol.setOnEditCommit(
            new EventHandler<CellEditEvent<CostDataRow, Integer>>() {
                @Override
                public void handle(CellEditEvent<CostDataRow, Integer> t) {
                    ((CostDataRow) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setBillPrice(t.getNewValue());
                    // Refresh table view (because of total costs)
                    t.getTableView().refresh();
                }
            }
        );
        // These columns are not editable
        totalCostsTCol.setCellValueFactory(new PropertyValueFactory<>("totalCosts"));
        totalCostsTCol.setCellFactory(TextFieldTableCell.<CostDataRow, Integer>forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(final Integer value) {
                return value.toString() + " Kč";
            }
            @Override
            public Integer fromString(final String s) {
                return Integer.parseInt(s.replace(" Kč", ""));
            }
        }));
        gainTCol.setCellValueFactory(new PropertyValueFactory<>("gain"));
        gainTCol.setCellFactory(TextFieldTableCell.<CostDataRow, Integer>forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(final Integer value) {
                return value.toString() + " Kč";
            }
            @Override
            public Integer fromString(final String s) {
                return Integer.parseInt(s.replace(" Kč", ""));
            }
        }));
        profitMarginTCol.setCellValueFactory(new PropertyValueFactory<>("profitMargin"));
        profitMarginTCol.setCellFactory(TextFieldTableCell.<CostDataRow, Integer>forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(final Integer value) {
                return value.toString() + " %";
            }
            @Override
            public Integer fromString(final String s) {
                return Integer.parseInt(s.replace(" %", ""));
            }
        }));
        
        // TODO
    }
    
}
