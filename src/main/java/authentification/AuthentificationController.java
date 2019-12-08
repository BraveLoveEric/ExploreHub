package authentification;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import handlers.Convenience;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import listComponent.EventListSingleton;
import listComponent.UpdateListTask;
import models.Account;
import models.Admin;
import models.User;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;

/**
 * Class which handles the authentification process
 *
 * @author Gheorghe Mironica, Tonislav Tachev
 */
public class AuthentificationController implements Initializable {

    @FXML
    private JFXCheckBox rememberBox;
    @FXML
    private TextField usernameField, passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private JFXTextField alert;
    private EntityManager entityManager;
    private static UpdateListTask updateTask;

    /**
     * Method which initializes the views
     *
     * @throws IOException {@link IOException}
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        usernameField.setPromptText("Email address");
        passwordField.setPromptText("Password");
        loginButton.setDisable(true);
        alert.setVisible(false);
        alert.setVisible(false);
        passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event){
                if(event.getCode().equals(KeyCode.ENTER)){
                    loginButton.fire();
                }
            }
        });
    }

    /**
     * Method which handles the login process
     *
     * @param event method trigger {@link Event}
     * @throws IOException {@link IOException}
     */
    @FXML
    private void login(Event event) throws IOException{

        StrategyContext strategyContext;
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            int accessLvl = getUserAccessLvl(username, password);

            if(accessLvl==0) {
                strategyContext = new StrategyContext(new UserStrategy());
                strategyContext.executeStrategy(username, password);
            }
            else {
                strategyContext = new StrategyContext(new AdminStrategy());
                strategyContext.executeStrategy(username, password);
            }

            initiliaseApp();
            GuestConnectionSingleton.getInstance().closeConnection();

        }catch(Exception e){
            alert.setText("Invalid Email or Password");
            alert.setVisible(true);
            usernameField.clear();
            passwordField.clear();

            PauseTransition visiblePause = new PauseTransition(
                    Duration.seconds(3)
            );
            visiblePause.setOnFinished(
                    (ActionEvent ev) -> {
                        alert.setVisible(false);
                    }
            );
            visiblePause.play();
            return;
        }

        alert.setVisible(false);
        checkRememberBox(username, password);
        Convenience.switchScene(event, getClass().getResource("/FXML/mainUI.fxml"));
    }

    /**
     * Method which checks if user exists, returns access level
     *
     * @param user username {@link String}
     * @param pass password {@link String}
     * @return {@link Integer}
     */
    private int getUserAccessLvl(String user, String pass) {
            // Try to establish connection as a guest
            GuestConnectionSingleton con = GuestConnectionSingleton.getInstance();
            entityManager = con.getManager();
            @SuppressWarnings("JpaQueryApiInspection")
            Query tq1 = entityManager.createNamedQuery(
                    "Account.determineAccess",
                    Account.class);
            tq1.setParameter("email", user);
            tq1.setParameter("password", pass);
            return (int) tq1.getSingleResult();

    }

    /**
     * This method initialises main application in a new parallel thread
     */
    public static void initiliaseApp() {
        updateTask = new UpdateListTask();
        updateTask.run();
    }

    /**
     * Saves user credentials on demand
     *
     * @param user user email {@link String}
     * @param pass user password {@link String}
     */
    private void checkRememberBox(String user, String pass) {
        RememberUserDBSingleton userDBSingleton = RememberUserDBSingleton.getInstance();

        if(rememberBox.isSelected()){
            userDBSingleton.init(user, pass);
            userDBSingleton.setUser();
            GuestConnectionSingleton.getInstance().closeConnection();
        } else{
            userDBSingleton.cleanDB();
        }
    }

    @FXML
    public void handleKeyRelease(){
        //check if both input fields are not empty, then proceed to login
        String text = usernameField.getText();
        String password = passwordField.getText();
        boolean hasText = text.isEmpty() || text.trim().isEmpty();
        boolean hasPassword = password.isEmpty() || password.trim().isEmpty();
        if(hasPassword && hasText) {
            loginButton.setDisable(hasText);
        } else if(!hasPassword && !hasText){
            loginButton.setDisable(false);
        } else if(hasPassword && !hasText){
            loginButton.setDisable(true);
        } else if(!hasPassword && hasText){
            loginButton.setDisable(true);
        }
    }

    /**
     * Method which handles the register process
     *
     * @param event method trigger {@link Event}
     * @throws IOException {@link IOException}
     */
    @FXML
    private void register(Event event) throws IOException {

        try {
            Convenience.switchScene(event, getClass().getResource("/FXML/register.fxml"));
        } catch(Error e){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Check the internet connection...");
            alert.showAndWait();
            return;
        }
    }

    /**
     * Method which handles enter button pressed
     *
     * @param ae method triggers {@link Event}
     */
    @FXML
    public void onEnter(ActionEvent ae){
    }

    /**
     * Method which handles the recovery process
     *
     * @param event method trigger {@link Event}
     * @throws IOException {@link IOException}
     */
    @FXML
    private void recover(Event event) throws IOException {
        Convenience.switchScene(event, getClass().getResource("/FXML/recover.fxml"));
    }


}
