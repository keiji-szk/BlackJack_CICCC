module blackjack {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens blackjack to javafx.fxml;
    exports blackjack;
    opens blackjack.controller to javafx.fxml;
    exports blackjack.controller;
}
