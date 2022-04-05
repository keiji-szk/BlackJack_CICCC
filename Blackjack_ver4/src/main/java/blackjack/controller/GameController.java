package blackjack.controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import blackjack.model.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ResourceBundle;

public class GameController implements Initializable{

    final static double WIDTH_CARD = 80.0;
    final static double HEIGHT_CARD = 113.0;
    final static double CARD_ALIGNMENT = 20.0;
    final static String MUSIC_MAIN_THEME = "capybaranoyume.mp3";
    final static String SOUND_BUTTON_CLICK = "button_click_sound.mp3";
    final static String MUSIC_BLACKJACK = "shining_star.mp3";
    final static String MUSIC_WIN = "wintercarnival.mp3";
    final static String MUSIC_LOSE = "kaibutsu.mp3";
    final static String MUSIC_BUST = "noranekonosakamori.mp3";
    private double soundVolume;
    private Table table;
    private Canvas canvasDealer;
    private Canvas canvasUser;
    private MediaPlayer musicPlayer;
    private MediaPlayer videoPlayer;
    private MediaView videoView;
    private boolean isFinish;

    @FXML
    private AnchorPane gameLayout;

    @FXML
    private Label dealerCountLabel;

    @FXML
    private Label userCountLabel;

    @FXML
    private Label resultLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Button hitButton;

    @FXML
    private Button standButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button helpButton;

    @FXML
    private ImageView adjustVolumeImage;

    @FXML
    private Slider adjust_volume_slider;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        table = new Table();
        initializeCanvas();
        initializeButton();
        initializeVolumeSlider();
        resetGame();
    }

    private void initializeCanvas(){
        this.canvasDealer = new Canvas(WIDTH_CARD*3, HEIGHT_CARD);
        this.canvasDealer.setLayoutX(230);
        this.canvasDealer.setLayoutY(14);
        this.canvasUser = new Canvas(WIDTH_CARD*3, HEIGHT_CARD);
        this.canvasUser.setLayoutX(230);
        this.canvasUser.setLayoutY(234);
        gameLayout.getChildren().addAll(this.canvasDealer, this.canvasUser);
    }

    private void initializeButton(){
        addButtonEffectEvent(standButton);
        addButtonEffectEvent(hitButton);
        addButtonEffectEvent(resetButton);
        addButtonEffectEvent(helpButton);
    }

    private void initializeVolumeSlider(){
        soundVolume = 0.3;
        adjust_volume_slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                soundVolume = adjust_volume_slider.getValue();
                if(musicPlayer != null){
                    musicPlayer.setVolume(soundVolume);
                }
                if(videoPlayer != null){
                    videoPlayer.setVolume(soundVolume);
                }
            }
        });
        adjustVolumeImage.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                adjustVolumeImage.setEffect( new Glow(2.5));
            }
        });
        adjustVolumeImage.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                adjustVolumeImage.setEffect(null);
            }
        });
        setAdjustButton(false);
    }

    private void setAdjustButton(boolean on){
        if(on){
            adjust_volume_slider.setVisible(true);
            Image img= new Image(getClass().getResourceAsStream("/blackjack/assets/picture/adjust_volume_on.png"),
                    adjustVolumeImage.getFitWidth() ,adjustVolumeImage.getFitHeight() , false, false);
            adjustVolumeImage.setImage(img);
        }else{
            adjust_volume_slider.setVisible(false);
            Image img= new Image(getClass().getResourceAsStream("/blackjack/assets/picture/adjust_volume_off.png"),
                    adjustVolumeImage.getFitWidth() ,adjustVolumeImage.getFitHeight() , false, false);
            adjustVolumeImage.setImage(img);
        }
        adjust_volume_slider.toFront();
    }

    @FXML
    void OnMouseClickedStandButton(MouseEvent mouseEvent) {
        stand();
    }

    @FXML
    void OnMouseClickedHelpButton(MouseEvent event)throws IOException {
        showHelpWindow();
    }

    @FXML
    void OnMouseClickedResetButton(MouseEvent event) {
        resetGame();
    }

    @FXML
    void OnMouseClickedHitButton(MouseEvent event) {
        hit();
    }

    @FXML
    void OnMouseClickedAdjustVolume(MouseEvent event) {
        setAdjustButton(!adjust_volume_slider.isVisible());
    }

    // crate help window
    @FXML
    public void showHelpWindow() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource( "/blackjack/help.fxml"));

        Stage tableWindow = (Stage) helpButton.getScene().getWindow();

        Stage helpWindow = new Stage();

        helpWindow.setTitle("help");
        helpWindow.initModality(Modality.WINDOW_MODAL);
        helpWindow.initOwner(tableWindow);

        helpWindow.setScene(new Scene(root));

        helpWindow.show();
    }

    void addButtonEffectEvent(Button btn){
        // When the button is pressed
        btn.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btn.setEffect(new Glow());
                btn.setEffect(new InnerShadow());
                playSound(SOUND_BUTTON_CLICK);
                setAdjustButton(false);
            }
        });
        // When the button is released
        btn.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btn.setEffect(new Glow());
            }
        });
        // When the mouse cursor entered the area of the button.
        btn.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btn.setEffect(new Glow());
            }
        });
        // When the mouse cursor exited the area of the button.
        btn.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                btn.setEffect(null);
            }
        });
    }

    private void playVideo(){
        playMusic(MUSIC_BLACKJACK);
        String path = getClass().getResource("/blackjack/assets/video/Fireworks.mp4").toExternalForm();
        videoPlayer = new MediaPlayer(new Media(path));
        videoPlayer.setAutoPlay(true);
        videoView = new MediaView(videoPlayer);
        playFadeIn(videoView);
        gameLayout.getChildren().add(videoView);
    }

    private void playFadeIn(MediaView mv){
        final FadeTransition fadeIn = new FadeTransition(Duration.millis(10000), mv);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void stopVideo(){
        if(videoView == null)
            return;
        videoPlayer.stop();
        gameLayout.getChildren().remove(videoView);
    }

    private void playMusic(String musicTitle){
        String path = getClass().getResource("/blackjack/assets/music/" + musicTitle).toExternalForm();
        if(musicPlayer != null){
            musicPlayer.stop();
        }
        musicPlayer = new MediaPlayer(new Media(path));
        musicPlayer.setVolume(soundVolume);
        musicPlayer.play();
    }

    private void playSound(String soundTitle){
        String path = getClass().getResource("/blackjack/assets/music/" + soundTitle).toExternalForm();
        AudioClip clip = new AudioClip(path);
        clip.setVolume(soundVolume);
        clip.play();
    }

    private void resetGame(){
        table.initialize();
        isFinish = false;
        titleLabel.setVisible(true);
        descriptionLabel.setVisible(true);
        resultLabel.setVisible(false);
        stopVideo();
        playMusic(MUSIC_MAIN_THEME);
        redrawTable();

        if(table.getUserCount() == Table.BLACK_JACK_NUMBER){
            finishGame(GAME_RESULT.BLACK_JACK);
        }
    }

    void finishGame(GAME_RESULT result){
        String resultMessage = null;
        switch (result){
            case BLACK_JACK:
                playVideo();
                resultMessage = "Black Jack! You win!";
                break;
            case WIN:
                playMusic(MUSIC_WIN);
                resultMessage = "You win!";
                break;
            case LOSE:
                playMusic(MUSIC_LOSE);
                resultMessage = "You lose!";
                break;
            case DRAW:
                playMusic(MUSIC_LOSE);
                resultMessage = "Draw!";
                break;
            case BUST:
                playMusic(MUSIC_BUST);
                resultMessage = "Bust! You lose!";
                break;
        }

        isFinish = true;
        titleLabel.setVisible(false);
        descriptionLabel.setVisible(false);
        resultLabel.setText(resultMessage);
        resultLabel.setVisible(true);
        redrawTable();
    }

    void bust(){
        finishGame(GAME_RESULT.BUST);
    }

    void blackjack(){
        finishGame(GAME_RESULT.BLACK_JACK);
    }

    void hit(){
        table.hit();
        //playMovingCard();
        redrawTable();

        final int userCount = table.getUserCount();
        if(Table.BLACK_JACK_NUMBER == userCount) {
            blackjack();
        }
        else if(Table.BLACK_JACK_NUMBER < userCount) {
            bust();
        }
    }

    void playMovingCard(){
        Image img= new Image(getClass().getResourceAsStream("/blackjack/assets/picture/Back_card.png"), WIDTH_CARD, HEIGHT_CARD, false, false);
        ImageView imageView = new ImageView();
        imageView.setImage(img);
        TranslateTransition animation = new TranslateTransition(Duration.seconds(0.3), imageView);
        animation.setFromY(0);
        animation.setToY(250);
        animation.setFromX(0);
        animation.setToX(350);
        animation.setCycleCount(1);
        animation.play();
        gameLayout.getChildren().add(imageView);
    }

    void stand(){
        table.stand();
        final int userCount = table.getUserCount();
        final int dealerCount = table.getDealerCount();
        if(dealerCount < userCount || Table.BLACK_JACK_NUMBER < dealerCount){
            if(userCount == Table.BLACK_JACK_NUMBER){
                finishGame(GAME_RESULT.BLACK_JACK);
            }else{
                finishGame(GAME_RESULT.WIN);
            }
        }else if(dealerCount == userCount){
            finishGame(GAME_RESULT.DRAW);
        }else{
            finishGame(GAME_RESULT.LOSE);
        }
        redrawTable();
    }

    private void redrawTable(){
        redrawDealerCards();
        redrawCards(table.getUserCards(), canvasUser);
        dealerCountLabel.setText("Dealer : " + table.showDealerPossibleHandScore(isFinish));
        userCountLabel.setText("User : " + table.showUserPossibleHandScore(isFinish));
        if(isFinish)
        {
            standButton.setVisible(false);
            hitButton.setVisible(false);
        }else{
            standButton.setVisible(true);
            hitButton.setVisible(true);
        }
    }

    private void redrawCard(Canvas canvas, Image img, double xPos, double yPos ){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(img, xPos, yPos);
        gc.strokeRect(xPos, yPos, img.getWidth(), img.getHeight());
    }

    private void redrawCards(ArrayDeque<Card> cards, Canvas canvas){
        clearCanvas(canvas);
        int xPos = 0;
        for(Card c : cards){
            Image img = new Image(getClass().getResourceAsStream(getCardURL(c)), WIDTH_CARD, HEIGHT_CARD, false, false);
            redrawCard(canvas, img, xPos, 0);
            xPos += CARD_ALIGNMENT;
        }
    }

    private void redrawDealerCards(){
        redrawCards(table.getDealerCards(), canvasDealer);
        if(isFinish)
            return;
        Image trumpBackImg = new Image(getClass().getResourceAsStream("/blackjack/assets/picture/Back_card.png"), WIDTH_CARD, HEIGHT_CARD, false, false);
        redrawCard(canvasDealer, trumpBackImg, CARD_ALIGNMENT, 0);
    }

    private void clearCanvas(Canvas canvas){
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private String getCardURL(Card card){
        return getCardURL(card.getSuit(), card.getRank());
    }


    private String getCardURL(SUIT suit, RANK rank){
        StringBuilder pathString = new StringBuilder("/blackjack/assets/picture/");
        switch (suit){
            case CLUB :
                pathString.append("clover/");
                break;
            case SPADE:
                pathString.append("spade/");
                break;
            case HEART:
                pathString.append("heart/");
                break;
            case DIAMOND:
                pathString.append("diamond/");
                break;
        }

        pathString.append(rank.getValue());
        pathString.append(".png");
        return pathString.toString();
    }
}
