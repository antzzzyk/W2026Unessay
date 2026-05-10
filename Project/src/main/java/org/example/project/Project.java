package org.example.project;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Project extends Application {

    // Track the animation state for the snap-scroll
    private int currentPostIndex = 0;
    private boolean isAnimating = false;

    @Override
    public void start(Stage primaryStage) {
        // --- 1. THE FATIGUE TRACKER (Left Side) ---
        ProgressBar fatigueBar = new ProgressBar(0);
        fatigueBar.setPrefWidth(200);
        fatigueBar.setPrefHeight(15);
        fatigueBar.setStyle("-fx-accent: #ff3366; -fx-control-inner-background: #1a1a1a; -fx-background-radius: 10;");

        Label hoursLabel = new Label("Hours Wasted: 0.0");
        hoursLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold;");

        VBox leftPanel = new VBox(20, hoursLabel, fatigueBar);
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setPadding(new Insets(30));
        leftPanel.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.05); " +
            "-fx-background-radius: 20; " +
            "-fx-border-color: rgba(255, 255, 255, 0.1); " +
            "-fx-border-radius: 20; " +
            "-fx-border-width: 1; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5);"
        );

        // --- 2. THE INSTAGRAM FEED ---
        VBox feed = new VBox();
        feed.setSpacing(0); // Set to 0 so posts stack perfectly for the snap scroll
        feed.setStyle("-fx-background-color: black;");

        VBox firstPost = createPost("michael_mental",
                "73% of young adults report negative mental health effects.", "Are we connected, or just lonely?");
                
        // --- STORIES BAR ---
        ScrollPane storiesScroll = new ScrollPane();
        storiesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        storiesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        storiesScroll.setStyle("-fx-background: black; -fx-background-color: black; -fx-padding: 0;");
        HBox storiesBar = new HBox(15);
        storiesBar.setPadding(new Insets(10));
        storiesBar.setStyle("-fx-background-color: black;");
        
        for (int i = 0; i < 8; i++) {
            Circle storyCircle = new Circle(32, Color.TRANSPARENT);
            Stop[] stops = new Stop[] {
                new Stop(0, Color.web("#f09433")),
                new Stop(0.25, Color.web("#e6683c")),
                new Stop(0.5, Color.web("#dc2743")),
                new Stop(0.75, Color.web("#cc2366")),
                new Stop(1, Color.web("#bc1888"))
            };
            LinearGradient igGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
            storyCircle.setStroke(igGradient);
            storyCircle.setStrokeWidth(3);

            Circle avatar = new Circle(28, Color.GRAY);
            StackPane storyWrapper = new StackPane(storyCircle, avatar);
            storiesBar.getChildren().add(storyWrapper);
        }
        storiesScroll.setContent(storiesBar);
        
        Font igFont = Font.loadFont(Project.class.getResourceAsStream("/org/example/project/igfont.otf"), 32);
        Label igTitle = new Label("Instagram");
        igTitle.setTextFill(Color.WHITE);
        if (igFont != null) {
            igTitle.setFont(igFont);
        } else {
            igTitle.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold;");
        }
        
        HBox titleBox = new HBox(igTitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(25, 0, 10, 0)); // Extra top padding to clear the notch
        titleBox.setStyle("-fx-background-color: black;");

        VBox topArea = new VBox(titleBox, storiesScroll);
        firstPost.getChildren().add(0, topArea); // Add at the top

        // Add the Unessay sections
        feed.getChildren().add(firstPost);
        feed.getChildren()
                .add(createPost("terry_physical",
                        "19% of 13-15 year olds are active on TikTok between Midnight and 5 AM.",
                        "Sleep deprivation is the silent epidemic."));
        feed.getChildren().add(createPost("antaney_time",
                "An average user spends over 6 years of their life on social media.", "Doomscrolling into oblivion."));

        int totalPosts = feed.getChildren().size();

        ScrollPane scrollPane = new ScrollPane(feed);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(380, 750);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: black; -fx-background-color: black; -fx-padding: 0;");

        // --- 3. THE SNAP SCROLL LOGIC ---
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            event.consume(); // Hijack the default scroll

            if (isAnimating)
                return;

            if (event.getDeltaY() < 0) {
                // Scrolled Down
                if (currentPostIndex < totalPosts - 1) {
                    currentPostIndex++;
                    snapToPost(scrollPane, currentPostIndex, feed, totalPosts, fatigueBar, hoursLabel);
                }
            } else if (event.getDeltaY() > 0) {
                // Scrolled Up
                if (currentPostIndex > 0) {
                    currentPostIndex--;
                    snapToPost(scrollPane, currentPostIndex, feed, totalPosts, fatigueBar, hoursLabel);
                }
            }
        });

        // --- 4. THE PHONE FRAME ---
        scrollPane.setMinSize(380, 750);
        scrollPane.setMaxSize(380, 750);

        Rectangle clip = new Rectangle(380, 750);
        clip.setArcWidth(56);
        clip.setArcHeight(56);
        scrollPane.setClip(clip);

        StackPane phoneFrame = new StackPane(scrollPane);
        phoneFrame.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        phoneFrame.getStyleClass().add("phone-frame");
        phoneFrame.setStyle(
            "-fx-background-color: black; " +
            "-fx-border-color: #222222; " +
            "-fx-border-width: 12; " +
            "-fx-border-radius: 40; " +
            "-fx-background-radius: 40; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 30, 0, 0, 15);"
        );

        Rectangle notch = new Rectangle(130, 30, Color.BLACK);
        notch.setArcWidth(25);
        notch.setArcHeight(25);
        StackPane.setAlignment(notch, Pos.TOP_CENTER);
        StackPane.setMargin(notch, new Insets(-2, 0, 0, 0)); // Slightly overlap the top border

        phoneFrame.getChildren().add(notch);

        // --- 5. MAIN LAYOUT ---
        BorderPane root = new BorderPane();
        root.setLeft(leftPanel);
        BorderPane.setMargin(leftPanel, new Insets(0, 0, 0, 50));
        root.setCenter(phoneFrame);
        
        // Add a right spacer with the same width as the left panel to perfectly center the phone
        Region rightSpacer = new Region();
        rightSpacer.prefWidthProperty().bind(leftPanel.widthProperty());
        BorderPane.setMargin(rightSpacer, new Insets(0, 50, 0, 0));
        root.setRight(rightSpacer);

        root.getStyleClass().add("root"); // For the black background CSS
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #08080a, #121214, #0a0a0c);");

        Scene scene = new Scene(root, 1000, 850);

        // Uncomment this once you create your styles.css file!
        // scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("The Scroll of Exhaustion");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    // --- ANIMATION HELPER ---
    private void snapToPost(ScrollPane scrollPane, int targetIndex, VBox feed, int totalPosts, ProgressBar fatigueBar,
            Label hoursLabel) {
        isAnimating = true;

        double targetY = 0;
        for (int i = 0; i < targetIndex; i++) {
            targetY += feed.getChildren().get(i).getBoundsInParent().getHeight();
        }

        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double contentHeight = feed.getBoundsInParent().getHeight();
        
        double targetVvalue;
        if (contentHeight <= viewportHeight) {
            targetVvalue = 0;
        } else {
            targetVvalue = targetY / (contentHeight - viewportHeight);
        }

        if (targetVvalue > 1.0) targetVvalue = 1.0;
        if (targetVvalue < 0.0) targetVvalue = 0.0;

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(400),
                        new KeyValue(scrollPane.vvalueProperty(), targetVvalue, Interpolator.EASE_BOTH)));

        timeline.setOnFinished(e -> isAnimating = false);
        timeline.play();

        // Update fatigue stats based on the new post index
        double progress = (double) targetIndex / (totalPosts - 1);
        fatigueBar.setProgress(progress);
        hoursLabel.setText(String.format("Hours Wasted: %.1f", (progress * 6)));
    }

    // --- POST BUILDER HELPER ---
    private VBox createPost(String username, String stat, String caption) {
        VBox post = new VBox();
        post.getStyleClass().add("post-container");
        post.setMinHeight(750); // Forces the post to fill the phone screen
        post.setStyle("-fx-background-color: black;");

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 15, 10, 15));
        Circle avatar = new Circle(16, Color.GRAY);
        Label userLabel = new Label(username);
        userLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-font-size: 14px;");
        header.getChildren().addAll(avatar, userLabel);

        // Content Area
        StackPane imageArea = new StackPane();
        imageArea.setStyle("-fx-background-color: linear-gradient(to bottom right, #2a2a2a, #1a1a1a); -fx-background-radius: 15;");
        VBox.setMargin(imageArea, new Insets(0, 10, 0, 10));
        imageArea.setMinHeight(400);
        Label statLabel = new Label(stat);
        statLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold;");
        statLabel.setWrapText(true);
        statLabel.setPadding(new Insets(20));
        imageArea.getChildren().add(statLabel);

        // Action Bar
        HBox actions = new HBox(15);
        actions.setPadding(new Insets(10, 15, 0, 15));
        Button likeBtn = new Button("♡");
        likeBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 26px; -fx-cursor: hand; -fx-padding: 0;");

        likeBtn.setOnAction(e -> {
            if (likeBtn.getText().equals("♡")) {
                likeBtn.setText("♥");
                likeBtn.setStyle(
                        "-fx-background-color: transparent; -fx-text-fill: #ff3366; -fx-font-size: 26px; -fx-cursor: hand; -fx-padding: 0;");
            } else {
                likeBtn.setText("♡");
                likeBtn.setStyle(
                        "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 26px; -fx-cursor: hand; -fx-padding: 0;");
            }
        });
        actions.getChildren().add(likeBtn);

        // Caption
        Label captionLabel = new Label(username + " " + caption);
        captionLabel.setStyle("-fx-text-fill: #dddddd; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px; -fx-padding: 5 15 20 15;");
        captionLabel.setWrapText(true);

        post.getChildren().addAll(header, imageArea, actions, captionLabel);
        return post;
    }

    public static void main(String[] args) {
        launch(args);
    }
}