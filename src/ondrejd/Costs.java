/**
 * @author Ondřej Doněk <ondrejd@gmail.com>
 * @link https://github.com/ondrejd/costs-javafx-app for the canonical source repository
 * @license https://www.gnu.org/licenses/gpl-3.0.en.html GNU General Public License 3.0
 */

package ondrejd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Costs extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("Costs.fxml").openStream());
        Scene scene = new Scene(root);
        CostsController controller = (CostsController)loader.getController();
        
        // Setting the css style file
        scene.getStylesheets().add("resources/css/styles.css");
        
        stage.setTitle("Náklady");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            try {
                controller.saveData();
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
