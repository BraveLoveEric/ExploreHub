package bookingComponent;

import alerts.CustomAlertType;
import authentification.CurrentAccountSingleton;
import com.jfoenix.controls.JFXTextField;
import handlers.Convenience;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import listComponent.EventListSingleton;
import mainUI.MainPane;
import models.Events;
import models.User;

import java.awt.print.Book;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;

/**
 * Payment Controller
 * @author Domagoj Frecko
 */

public class PaymentController implements Initializable {
    @FXML
    Pane container;

    @FXML
    Button payBtn, cancel, back;

    @FXML
    Label totalPrice;

    Pane newPane;

    static String confirmationText;
    private List<Events> evList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        evList = CurrentAccountSingleton.getInstance().getAccount().getBookedEvents();

        totalPrice.setText("Total: €" + BookingController.getTotalPrice());


        // Cash
        if(BookingController.getPaymentType() == 1){

            try{
                newPane = FXMLLoader.load(getClass().getResource("/FXML/paymentCash.fxml"));
                container.getChildren().add(newPane);
            } catch (IOException e){e.printStackTrace();}

        }

        // Card
        else if(BookingController.getPaymentType() == 0){
            try{
                newPane = FXMLLoader.load(getClass().getResource("/FXML/paymentCard.fxml"));
                container.getChildren().add(newPane);
            } catch (IOException e){e.printStackTrace();}
        }

        // Free
        else if(BookingController.getPaymentType() == 2){
            payment();
        }

    }

    /**
     * Method which initiates the payment strategy and starts the booking process.
     */
    @FXML
    public void payment(){ // Once user presses Pay button initiate strategy

            disableControlBtns();
            totalPrice.setVisible(false);
            boolean isBooked = false;

            if (BookingController.getPaymentType() == 1) {
                CashPaymentStrategy CashPS = new CashPaymentStrategy();
                isBooked = CashPS.pay();
                confirmationText = "Booking successful.\nPayment with: Cash\nVisit Prittwitzstrasse Campus to pay and get approved.";
            } else if (BookingController.getPaymentType() == 0) {
                CardPaymentStrategy CardPS = new CardPaymentStrategy();
                isBooked = CardPS.pay();
                confirmationText = "Booking successful.\nPayment with: Card";
            } else if (BookingController.getPaymentType() == 2) {
                FreePaymentStrategy FreePS = new FreePaymentStrategy();
                isBooked = FreePS.pay();
                confirmationText = "Booking successful.\nEnjoy your trip!";
            }

            if (isBooked) {
                confirmationScene();
                EventListSingleton.getInstance().refreshList();
            }

    }

    /**
     * Method which goes back to the payment selection screen
     * @param event triggered on button ('Back') press
     */
    @FXML
    public void goBack(Event event){
        try {
            Convenience.popupDialog(MainPane.getInstance().getStackPane(), MainPane.getInstance().getBorderPane(),
                    getClass().getResource("/FXML/booking.fxml"));
        }catch(IOException e){
            Convenience.showAlert(CustomAlertType.ERROR, "Something went wrong. Please, try again later.");
        }
    }

    /**
     * Method which cancels the booking and returns the user to the homepage
     * @param event triggered on button ('Cancel') press
     */
    @FXML
    public void cancelBooking(Event event){ // Once user presses Cancel button - cancel the booking
        BookingController.setPaymentTypeValue(100);
        Convenience.closePreviousDialog();
    }

    /**
     * Method which displays the confirmation screen once booking is successful
     */
    public void confirmationScene(){
        try {
            Convenience.popupDialog(MainPane.getInstance().getStackPane(), MainPane.getInstance().getBorderPane(),
                    getClass().getResource("/FXML/paymentConfirmation.fxml"));
        } catch (IOException e) {
            Convenience.showAlert(CustomAlertType.ERROR, "Something went wrong. Please, try again later.");
        }
    }

    public void disablePayBtn(){
        Platform.runLater(()->{
            payBtn.setDisable(true);
            payBtn.setVisible(false);
        });
    }

    /**
     * Method which disables the control buttons on the screen
     */
    public void disableControlBtns(){
        Platform.runLater(()->{
            payBtn.setDisable(true);
            payBtn.setVisible(false);
            cancel.setDisable(true);
            cancel.setVisible(false);
            back.setDisable(true);
            back.setVisible(false);

        });
    }

    public static String getConfirmationText(){return confirmationText;}

}
