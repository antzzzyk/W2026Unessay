package org.example.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Project extends Application {

        // Track the animation state for the snap-scroll
        private int currentPostIndex = 0;
        private boolean isAnimating = false;

        private VBox notificationContainer;
        private Label reflectionText;
        private Timeline reflectionTimeline;
        private StackPane phoneFrame;
        private VBox leftPanel;
        private BorderPane root;

        // For fatigue tracking
        private ProgressBar fatigueBar;
        private Label hoursLabel;
        private ProgressBar dopamineBar;
        private Label dopamineLabel;
        private ProgressBar attentionBar;
        private Label attentionLabel;
        private Label batteryLabel;
        private Rectangle batteryFill;
        private double currentBattery = 100.0;

        @Override
        public void start(Stage primaryStage) {
                // --- 1. THE FATIGUE TRACKER (Left Side) ---
                fatigueBar = new ProgressBar(0);
                fatigueBar.setPrefWidth(200);
                fatigueBar.setPrefHeight(15);
                fatigueBar.setStyle(
                                "-fx-accent: #ff3366; -fx-control-inner-background: #e0e0e0; -fx-background-radius: 10;");

                hoursLabel = new Label("Hours Wasted: 0.0");
                hoursLabel.setStyle(
                                "-fx-text-fill: #222222; -fx-font-size: 20px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold;");

                dopamineBar = new ProgressBar(1.0);
                dopamineBar.setPrefWidth(200);
                dopamineBar.setPrefHeight(15);
                dopamineBar.setStyle(
                                "-fx-accent: #00f2fe; -fx-control-inner-background: #e0e0e0; -fx-background-radius: 10;");

                dopamineLabel = new Label("Dopamine: 100%");
                dopamineLabel.setStyle(
                                "-fx-text-fill: #444444; -fx-font-size: 16px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold;");

                attentionBar = new ProgressBar(1.0);
                attentionBar.setPrefWidth(200);
                attentionBar.setPrefHeight(15);
                attentionBar.setStyle(
                                "-fx-accent: #ffb75e; -fx-control-inner-background: #e0e0e0; -fx-background-radius: 10;");

                attentionLabel = new Label("Attention Span: 8.0s");
                attentionLabel.setStyle(
                                "-fx-text-fill: #444444; -fx-font-size: 16px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold;");

                leftPanel = new VBox(15, hoursLabel, fatigueBar, dopamineLabel, dopamineBar, attentionLabel,
                                attentionBar);
                leftPanel.setAlignment(Pos.CENTER);
                leftPanel.setPadding(new Insets(30));
                leftPanel.setStyle("");

                // Desk Setup - Left Side (Papers, Pen, Pencil, Eraser)
                StackPane leftDeskArea = new StackPane();

                Region paper1 = new Region();
                paper1.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
                paper1.setMaxSize(280, 360);
                paper1.setRotate(-4);

                Region paper2 = new Region();
                paper2.setStyle("-fx-background-color: #fdfdfd; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");
                paper2.setMaxSize(280, 360);
                paper2.setRotate(2);

                // Pencil
                Rectangle pencil = new Rectangle(120, 8, Color.web("#f4c430")); // Yellow pencil
                pencil.setRotate(15);
                pencil.setTranslateX(100);
                pencil.setTranslateY(-140);
                pencil.setArcWidth(4);
                pencil.setArcHeight(4);
                pencil.setEffect(new javafx.scene.effect.DropShadow(5, Color.rgb(0, 0, 0, 0.3)));

                // Pencil tip
                javafx.scene.shape.Polygon pencilTip = new javafx.scene.shape.Polygon();
                pencilTip.getPoints().addAll(new Double[] {
                                0.0, 0.0,
                                15.0, 4.0,
                                0.0, 8.0
                });
                pencilTip.setFill(Color.web("#eec59f")); // wood color
                pencilTip.setRotate(15);
                pencilTip.setTranslateX(165);
                pencilTip.setTranslateY(-122);
                pencilTip.setEffect(new javafx.scene.effect.DropShadow(2, Color.rgb(0, 0, 0, 0.2)));

                // Pen
                Rectangle pen = new Rectangle(130, 10, Color.web("#1a1a1a")); // Dark pen
                pen.setRotate(-25);
                pen.setTranslateX(-90);
                pen.setTranslateY(150);
                pen.setArcWidth(5);
                pen.setArcHeight(5);
                pen.setEffect(new javafx.scene.effect.DropShadow(5, Color.rgb(0, 0, 0, 0.3)));

                // Eraser
                Rectangle eraser = new Rectangle(45, 20, Color.web("#ffb6c1")); // Pink eraser
                eraser.setRotate(40);
                eraser.setTranslateX(100);
                eraser.setTranslateY(150);
                eraser.setArcWidth(6);
                eraser.setArcHeight(6);
                eraser.setEffect(new javafx.scene.effect.DropShadow(5, Color.rgb(0, 0, 0, 0.3)));

                leftDeskArea.getChildren().addAll(paper1, paper2, pencil, pencilTip, pen, eraser, leftPanel);

                // --- 2. THE INSTAGRAM FEED ---
                VBox feed = new VBox();
                feed.setSpacing(0); // Set to 0 so posts stack perfectly for the snap scroll
                feed.setStyle("-fx-background-color: black;");

                VBox firstPost = createPathosPost("The System",
                                "We are losing our generation to the screen.",
                                "And we don't even realize it's happening.",
                                "Are we connected, or just lonely?");

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
                        igTitle.setStyle(
                                        "-fx-text-fill: white; -fx-font-size: 28px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold;");
                }

                HBox titleBox = new HBox(igTitle);
                titleBox.setAlignment(Pos.CENTER);
                titleBox.setPadding(new Insets(40, 0, 10, 0)); // Extra top padding to clear the notch and status bar
                titleBox.setStyle("-fx-background-color: black;");

                VBox topArea = new VBox(titleBox, storiesScroll);
                firstPost.getChildren().add(0, topArea); // Add at the top

                // Add the Unessay sections
                feed.getChildren().add(firstPost);

                List<String> imageFiles = new ArrayList<>();
                try {
                        java.net.URL url = getClass().getResource("/org/example/project/");
                        if (url != null) {
                                java.io.File dir = new java.io.File(url.toURI());
                                if (dir.exists() && dir.isDirectory()) {
                                        for (java.io.File file : dir.listFiles()) {
                                                String name = file.getName().toLowerCase();
                                                if (name.endsWith(".png") || name.endsWith(".jpg")
                                                                || name.endsWith(".jpeg")) {
                                                        imageFiles.add(file.getName());
                                                }
                                        }
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }

                if (imageFiles.isEmpty()) {
                        imageFiles.addAll(Arrays.asList("IMG_8083.jpeg", "IMG_8101.jpeg", "IMG_8104.jpeg",
                                        "IMG_8295.jpeg", "IMG_8602.jpeg"));
                }

                int imgIndex = 0;

                if (imgIndex < imageFiles.size())
                        feed.getChildren().add(createImagePost("/org/example/project/" + imageFiles.get(imgIndex++)));

                // --- Mental Health (Michael) ---
                feed.getChildren().add(createStatPost("Child Mind Institute", "+13%",
                        "According to the Child Mind Institute, the risk for depression increases by 13% per additional hour of daily social media use.",
                        "Compounding interest on your mental health."));

                if (imgIndex < imageFiles.size()) feed.getChildren().add(createImagePost("/org/example/project/" + imageFiles.get(imgIndex++)));

                feed.getChildren().add(createPollPost("michael_mind",
                        "Do you feel like social media pushes unrealistic body standards?", "Yes, absolutely.", "Not really.", 92, 8,
                        "The Ideal Image",
                        "According to the National Center for Health Research, 1 in 3 teen girls feel worse about their bodies due to Instagram.",
                        "Prolonged exposure to edited images leads to severe concerns about body image."));

                feed.getChildren().add(createStatPost("Clinical Psychological Science", "33%",
                        "A 2017 study found the number of teens exhibiting high levels of depressive symptoms increased by 33% between 2010 and 2015 alongside smartphone adoption.",
                        "Does that number surprise you?"));

                if (imgIndex < imageFiles.size()) feed.getChildren().add(createImagePost("/org/example/project/" + imageFiles.get(imgIndex++)));

                // --- Physical Health (Terry) ---
                feed.getChildren().add(createStatPost("Cropink", "91%",
                        "According to Cropink statistics, 91% of teens use social media daily for more than 3 hours on average.",
                        "The average user spends over 6 years of their life scrolling."));

                feed.getChildren().add(createPollPost("terry_physical",
                        "73% of young adults believe social media negatively affects their mental health. Do you agree?", "Yes.", "No.", 73, 27,
                        "The Physical Toll",
                        "According to the 2023 SHARP Survey, only 38.4% of youth get 8+ hours of sleep. Screen time is strongly linked to this.",
                        "Sleep loss is a risk factor for depression and excessive weight gain."));

                if (imgIndex < imageFiles.size()) feed.getChildren().add(createImagePost("/org/example/project/" + imageFiles.get(imgIndex++)));

                feed.getChildren().add(createQuotePost("Dr. Michelle Hofmann", "Utah DHHS",
                        "Bright screens keep people alert and are damaging to sleep cycles and sleep hygiene.",
                        "Yet 25% of 16-17yo TikTok users are active between midnight and 5am."));

                feed.getChildren().add(createStatPost("The Vision Council", "65%",
                        "A report by The Vision Council states 65 percent of Americans experience digital eye strain symptoms from gazing at screens.",
                        "You can feel it right now, can't you?"));

                // --- Time & Productivity (Antaney) ---
                if (imgIndex < imageFiles.size()) feed.getChildren().add(createImagePost("/org/example/project/" + imageFiles.get(imgIndex++)));

                feed.getChildren().add(createPollPost("antaney_time",
                        "Have you ever found yourself 'zombie scrolling'?", "Yes, frequently.", "Rarely.", 85, 15,
                        "The Flow State",
                        "A KU study finds students with lower self-control use short-form video to escape, increasing procrastination.",
                        "Your cognitive resources are being drained passively."));

                feed.getChildren().add(createPathosPost("daily_reality",
                        "Research by Starvaggi et al. revealed that 52% of the information in the most viewed TikTok videos consists of misinformation.",
                        "But you keep scrolling anyway.",
                        "Diminished productivity and cognitive decline."));

                // --- NEW POLL: MEMORY & ATTENTION ---
                feed.getChildren().add(createPollPost("the_algorithm",
                        "Do you remember exactly what the last 5 posts were?", "Yes, I think so.", "No, not really.", 12, 88,
                        "Amnesia",
                        "The constant flow of information bypasses short-term memory encoding.",
                        "You're just consuming to consume."));

                feed.getChildren().add(createPathosPost("brain_rot",
                        "Doomscrolling severely fractures your attention span.",
                        "You are training your brain to need a new stimulus every 3 seconds.",
                        "No wonder you can't focus on your homework."));

                if (imgIndex < imageFiles.size()) feed.getChildren().add(createImagePost("/org/example/project/" + imageFiles.get(imgIndex++)));

                feed.getChildren().add(createStatPost("Harvard Health", "Doomscrolling",
                        "According to Harvard experts, physical effects of doomscrolling include nausea, headaches, muscle tension, and elevated blood pressure.",
                        "Your body thinks you are in danger."));

                // Add remaining images at the end
                int nameIdx = 0;
                while (imgIndex < imageFiles.size()) {
                        feed.getChildren().add(createImagePost("/org/example/project/" + imageFiles.get(imgIndex++)));
                        nameIdx++;
                        if (nameIdx % 3 == 0) {
                                feed.getChildren().add(createPathosPost("the_algorithm", "Keep scrolling.",
                                                "We need your attention.", "Don't look away."));
                        }
                }

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

                        javafx.scene.Node currentPost = feed.getChildren().get(currentPostIndex);
                        if (Boolean.TRUE.equals(currentPost.getProperties().get("isPoll")) &&
                                        !Boolean.TRUE.equals(currentPost.getProperties().get("pollResponded"))) {
                                shakePost(currentPost);
                                return;
                        }
                        if (Boolean.TRUE.equals(currentPost.getProperties().get("isAd")) &&
                                        !Boolean.TRUE.equals(currentPost.getProperties().get("adFinished"))) {
                                shakePost(currentPost);
                                return;
                        }

                        if (event.getDeltaY() < 0) {
                                // Scrolled Down
                                if (currentPostIndex < totalPosts - 1) {
                                        currentPostIndex++;
                                        snapToPost(scrollPane, currentPostIndex, feed, totalPosts);
                                }
                        } else if (event.getDeltaY() > 0) {
                                // Scrolled Up
                                if (currentPostIndex > 0) {
                                        currentPostIndex--;
                                        snapToPost(scrollPane, currentPostIndex, feed, totalPosts);
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

                // --- STATUS BAR ---
                HBox statusBar = new HBox();
                statusBar.setPadding(new Insets(12, 25, 0, 25));
                statusBar.setAlignment(Pos.CENTER);
                statusBar.setMaxHeight(Region.USE_PREF_SIZE);
                statusBar.setMouseTransparent(true);

                Label timeLabel = new Label("9:41");
                timeLabel.setStyle(
                                "-fx-text-fill: white; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-font-size: 14px;");

                batteryLabel = new Label("100%");
                batteryLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-font-size: 12px;");

                HBox batteryBox = new HBox(4);
                batteryBox.setAlignment(Pos.CENTER);
                
                Rectangle batBody = new Rectangle(20, 10);
                batBody.setFill(Color.TRANSPARENT);
                batBody.setStroke(Color.WHITE);
                batBody.setStrokeWidth(1.5);
                batBody.setArcWidth(3); batBody.setArcHeight(3);
                
                batteryFill = new Rectangle(18, 8, Color.WHITE);
                batteryFill.setArcWidth(2); batteryFill.setArcHeight(2);
                
                Rectangle batTip = new Rectangle(1.5, 4, Color.WHITE);
                batTip.setArcWidth(1); batTip.setArcHeight(1);
                
                StackPane batteryIcon = new StackPane(batBody, batteryFill);
                StackPane.setAlignment(batteryFill, Pos.CENTER_LEFT);
                StackPane.setMargin(batteryFill, new Insets(0, 0, 0, 1)); // offset slightly
                
                batteryBox.getChildren().addAll(batteryLabel, batteryIcon, batTip);

                Region statusSpacer = new Region();
                HBox.setHgrow(statusSpacer, Priority.ALWAYS);

                statusBar.getChildren().addAll(timeLabel, statusSpacer, batteryBox);
                StackPane.setAlignment(statusBar, Pos.TOP_CENTER);

                // --- NOTIFICATIONS ---
                notificationContainer = new VBox(10);
                notificationContainer.setPadding(new Insets(50, 15, 20, 15)); // Below notch
                notificationContainer.setMouseTransparent(true);
                StackPane.setAlignment(notificationContainer, Pos.TOP_CENTER);

                // Notifications timer
                String[][] notifData = {
                                { "Calendar", "English Unessay Due",
                                                "Don't forget your English Unessay is due tomorrow! Better start writing." },
                                { "Reminders", "Group Project",
                                                "Meet with the team at 9 AM to discuss the final presentation." },
                                { "Messages", "Mom", "Are you studying? Remember you have that big test coming up." },
                                { "Calendar", "Calculus Exam",
                                                "Calculus III Final Exam in 2 days. Make sure to review chapter 7." },
                                { "Reminders", "Read Chapter 4",
                                                "You need to finish reading chapter 4 for History class." },
                                { "Messages", "Alex", "Hey, are you ready for tomorrow's quiz? I haven't started studying." },
                                { "Messages", "Project Group", "Can you finish your part of the project tonight? It's due tomorrow." },
                                { "Messages", "Dad", "Did you finish your homework? You've been on your phone for hours." },
                                { "Canvas", "Assignment Graded", "Your recent exam was graded: 62% (D-)" },
                                { "Email", "Professor Smith", "Reminder: Missing Assignments" }
                };

                Timeline notifTimeline = new Timeline(new KeyFrame(Duration.seconds(10), ev -> {
                        int idx = (int) (Math.random() * notifData.length);
                        showNotification(notifData[idx][0], notifData[idx][1], notifData[idx][2]);
                }));
                notifTimeline.setCycleCount(Timeline.INDEFINITE);
                notifTimeline.play();

                // --- BATTERY TIMER ---
                Timeline batteryTimer = new Timeline(new KeyFrame(Duration.seconds(3), ev -> {
                        currentBattery -= 0.5;
                        updateBatteryUI();
                }));
                batteryTimer.setCycleCount(Timeline.INDEFINITE);
                batteryTimer.play();

                phoneFrame = new StackPane(scrollPane, statusBar, notificationContainer);
                // Force phone frame size to prevent "iPad" widening effect
                phoneFrame.setMinSize(380, 750);
                phoneFrame.setMaxSize(380, 750);
                phoneFrame.getStyleClass().add("phone-frame");
                phoneFrame.setStyle(
                                "-fx-background-color: black; " +
                                                "-fx-border-color: #222222; " +
                                                "-fx-border-width: 12; " +
                                                "-fx-border-radius: 40; " +
                                                "-fx-background-radius: 40; " +
                                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 30, 0, 0, 15);");

                Rectangle notch = new Rectangle(130, 30, Color.BLACK);
                notch.setArcWidth(25);
                notch.setArcHeight(25);
                StackPane.setAlignment(notch, Pos.TOP_CENTER);
                StackPane.setMargin(notch, new Insets(-2, 0, 0, 0)); // Slightly overlap the top border

                phoneFrame.getChildren().add(notch);
                statusBar.toFront();

                // --- 5. MAIN LAYOUT ---
                root = new BorderPane();
                root.setLeft(leftDeskArea);
                BorderPane.setMargin(leftDeskArea, new Insets(0, 0, 0, 50));
                root.setCenter(phoneFrame);

                // RIGHT PANEL: Open Book with Self reflection text
                StackPane rightDeskArea = new StackPane();

                HBox openBook = new HBox(0);
                openBook.setAlignment(Pos.CENTER);
                openBook.setMaxSize(400, 300);

                Region leftPage = new Region();
                leftPage.setStyle(
                                "-fx-background-color: linear-gradient(to right, #e8e8e8, #fdfdfd); -fx-background-radius: 10 0 0 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, -5, 5);");
                leftPage.setPrefSize(200, 300);

                Region rightPage = new Region();
                rightPage.setStyle(
                                "-fx-background-color: linear-gradient(to left, #e8e8e8, #fdfdfd); -fx-background-radius: 0 10 10 0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 5, 5);");
                rightPage.setPrefSize(200, 300);

                openBook.getChildren().addAll(leftPage, rightPage);

                // Add a spine shadow
                Region spine = new Region();
                spine.setStyle("-fx-background-color: linear-gradient(to right, transparent, rgba(0,0,0,0.15) 50%, transparent);");
                spine.setMaxSize(30, 300);

                StackPane bookContainer = new StackPane(openBook, spine);
                bookContainer.setRotate(3); // slight rotation on the desk

                VBox rightPanel = new VBox();
                rightPanel.setMaxWidth(300); // Fit within the book
                rightPanel.setAlignment(Pos.CENTER);
                BorderPane.setMargin(rightDeskArea, new Insets(0, 50, 0, 0));

                reflectionText = new Label("Why are you still scrolling?");
                reflectionText.setStyle(
                                "-fx-text-fill: #333333; -fx-font-size: 24px; -fx-font-family: 'Georgia', serif; -fx-font-style: italic;");
                reflectionText.setWrapText(true);
                reflectionText.setTextAlignment(TextAlignment.CENTER);
                reflectionText.setOpacity(0); // Start faded out
                rightPanel.getChildren().add(reflectionText);

                // Slight rotation to text to match the book
                rightPanel.setRotate(3);

                rightDeskArea.getChildren().addAll(bookContainer, rightPanel);

                root.setRight(rightDeskArea);

                root.getStyleClass().add("root"); // For the black background CSS
                root.setStyle("-fx-background-color: linear-gradient(to bottom right, #3A2318, #1A0E08, #25160F);");

                // Set up the reflection timeline
                String[] reflections = {
                                "Why are you still scrolling?",
                                "What were you doing before you opened this app?",
                                "Are you actually enjoying this, or just avoiding reality?",
                                "Is this bringing you closer to your goals?",
                                "You've been here before.",
                                "Nothing new is waiting at the bottom."
                };

                reflectionTimeline = new Timeline(
                                new KeyFrame(Duration.seconds(0), e -> {
                                        reflectionText.setText(reflections[(int) (Math.random() * reflections.length)]);
                                }),
                                new KeyFrame(Duration.seconds(1),
                                                new KeyValue(reflectionText.opacityProperty(), 1,
                                                                Interpolator.EASE_BOTH)),
                                new KeyFrame(Duration.seconds(4),
                                                new KeyValue(reflectionText.opacityProperty(), 1,
                                                                Interpolator.EASE_BOTH)),
                                new KeyFrame(Duration.seconds(5),
                                                new KeyValue(reflectionText.opacityProperty(), 0,
                                                                Interpolator.EASE_BOTH)));
                reflectionTimeline.setCycleCount(Timeline.INDEFINITE);
                reflectionTimeline.play();

                Scene scene = new Scene(root, 1000, 850);

                // Uncomment this once you create your styles.css file!
                // scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

                primaryStage.setTitle("The Scroll of Exhaustion");
                primaryStage.setScene(scene);
                primaryStage.setFullScreen(true);
                primaryStage.show();
        }

        // --- POLL HIGHLIGHT EVENT ---
        private void triggerPollHighlight(String responseText) {
                // Pause the normal fading reflection questions
                if (reflectionTimeline != null)
                        reflectionTimeline.pause();

                // Show the judgment text
                reflectionText.setOpacity(0);
                reflectionText.setText(responseText);
                reflectionText.setStyle(
                                "-fx-text-fill: #ff3366; -fx-font-size: 28px; -fx-font-family: 'Georgia', serif; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(255,51,102,0.8), 15, 0, 0, 0);");

                // Dim background and left panel, highlight phone
                Timeline highlightOn = new Timeline(
                                new KeyFrame(Duration.millis(500),
                                                new KeyValue(root.getLeft().opacityProperty(), 0.1,
                                                                Interpolator.EASE_BOTH),
                                                new KeyValue(reflectionText.opacityProperty(), 1.0,
                                                                Interpolator.EASE_BOTH),
                                                new KeyValue(phoneFrame.effectProperty(),
                                                                new javafx.scene.effect.DropShadow(50,
                                                                                Color.web("#ff3366")),
                                                                Interpolator.EASE_BOTH)));
                highlightOn.play();

                // Revert after 5 seconds
                Timeline highlightOff = new Timeline(
                                new KeyFrame(Duration.millis(1000),
                                                new KeyValue(root.getLeft().opacityProperty(), 1.0,
                                                                Interpolator.EASE_BOTH),
                                                new KeyValue(reflectionText.opacityProperty(), 0.0,
                                                                Interpolator.EASE_BOTH),
                                                new KeyValue(phoneFrame.effectProperty(), null,
                                                                Interpolator.EASE_BOTH)));
                highlightOff.setDelay(Duration.seconds(4));
                highlightOff.setOnFinished(ev -> {
                        // Restore normal text style and resume timeline
                        reflectionText.setStyle(
                                        "-fx-text-fill: #333333; -fx-font-size: 24px; -fx-font-family: 'Georgia', serif; -fx-font-style: italic;");
                        if (reflectionTimeline != null)
                                reflectionTimeline.play();
                });
                highlightOff.play();
        }

        // --- ANIMATION HELPER ---
        private void snapToPost(ScrollPane scrollPane, int targetIndex, VBox feed, int totalPosts) {
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

                if (targetVvalue > 1.0)
                        targetVvalue = 1.0;
                if (targetVvalue < 0.0)
                        targetVvalue = 0.0;

                Timeline timeline = new Timeline(
                                new KeyFrame(Duration.millis(400),
                                                new KeyValue(scrollPane.vvalueProperty(), targetVvalue,
                                                                Interpolator.EASE_BOTH)));

                timeline.setOnFinished(e -> {
                        isAnimating = false;
                        javafx.scene.Node targetPostNode = feed.getChildren().get(targetIndex);
                        if (Boolean.TRUE.equals(targetPostNode.getProperties().get("isAd")) &&
                                        !Boolean.TRUE.equals(targetPostNode.getProperties().get("adFinished"))) {

                                if (!Boolean.TRUE.equals(targetPostNode.getProperties().get("adTimerStarted"))) {
                                        targetPostNode.getProperties().put("adTimerStarted", true);
                                        Button skipBtn = (Button) targetPostNode.getProperties().get("skipBtn");
                                        Timeline adTimer = new Timeline(
                                                        new KeyFrame(Duration.seconds(1),
                                                                        ev -> skipBtn.setText("Skip in 2s")),
                                                        new KeyFrame(Duration.seconds(2),
                                                                        ev -> skipBtn.setText("Skip in 1s")),
                                                        new KeyFrame(Duration.seconds(3), ev -> {
                                                                skipBtn.setText("Skip Ad");
                                                                skipBtn.setStyle(
                                                                                "-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 16; -fx-cursor: hand;");
                                                                skipBtn.setDisable(false);
                                                        }));
                                        skipBtn.setOnAction(ev -> {
                                                targetPostNode.getProperties().put("adFinished", true);
                                                skipBtn.setText("Ad Skipped");
                                                skipBtn.setDisable(true);
                                                skipBtn.setStyle(
                                                                "-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: rgba(255,255,255,0.4); -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 16;");
                                        });
                                        adTimer.play();
                                }
                        }
                });
                timeline.play();

                // Update fatigue stats based on the new post index
                double progress = (double) targetIndex / (totalPosts - 1);
                fatigueBar.setProgress(progress);
                hoursLabel.setText(String.format("Hours Wasted: %.1f", progress * 6));

                dopamineBar.setProgress(1.0 - (progress * 0.9));
                dopamineLabel.setText(String.format("Dopamine: %.0f%%", (1.0 - (progress * 0.9)) * 100));

                attentionBar.setProgress(1.0 - (progress * 0.8));
                attentionLabel.setText(String.format("Attention Span: %.1fs", 8.0 - (progress * 6.0)));

                // Update battery based on scroll
                currentBattery -= 0.8;
                updateBatteryUI();
        }

        private void updateBatteryUI() {
                if (currentBattery < 1.0) currentBattery = 1.0;
                batteryLabel.setText(String.format("%.0f%%", currentBattery));
                batteryFill.setWidth(18.0 * (currentBattery / 100.0));
                if (currentBattery <= 20) {
                        batteryFill.setFill(Color.RED);
                } else if (currentBattery <= 50) {
                        batteryFill.setFill(Color.YELLOW);
                } else {
                        batteryFill.setFill(Color.WHITE);
                }
        }

        // --- NOTIFICATION HELPER ---
        private void showNotification(String app, String title, String msg) {
                VBox notif = new VBox(5);
                notif.setPadding(new Insets(12));
                notif.setStyle(
                                "-fx-background-color: rgba(30, 30, 30, 0.85); " +
                                                "-fx-background-radius: 20; " +
                                                "-fx-border-radius: 20; " +
                                                "-fx-border-width: 1; " +
                                                "-fx-border-color: rgba(255,255,255,0.15); " +
                                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 5);");

                HBox header = new HBox(8);
                header.setAlignment(Pos.CENTER_LEFT);
                Rectangle icon = new Rectangle(18, 18);
                icon.setArcWidth(6);
                icon.setArcHeight(6);
                if (app.equals("Calendar"))
                        icon.setFill(Color.web("#ff3b30")); // iOS Red
                else if (app.equals("Messages"))
                        icon.setFill(Color.web("#34c759")); // iOS Green
                else
                        icon.setFill(Color.web("#007aff")); // iOS Blue

                Label appLabel = new Label(app.toUpperCase());
                appLabel.setStyle(
                                "-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 11px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");
                header.getChildren().addAll(icon, appLabel);

                Label titleLabel = new Label(title);
                titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

                Label msgLabel = new Label(msg);
                msgLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 13px;");
                msgLabel.setWrapText(true);

                notif.getChildren().addAll(header, titleLabel, msgLabel);

                notif.setTranslateY(-100);
                notif.setOpacity(0);
                notificationContainer.getChildren().add(0, notif);

                Timeline appear = new Timeline(
                                new KeyFrame(Duration.millis(400),
                                                new KeyValue(notif.translateYProperty(), 0, Interpolator.EASE_OUT),
                                                new KeyValue(notif.opacityProperty(), 1, Interpolator.EASE_OUT)));

                Timeline disappear = new Timeline(
                                new KeyFrame(Duration.millis(400),
                                                new KeyValue(notif.translateYProperty(), -100, Interpolator.EASE_IN),
                                                new KeyValue(notif.opacityProperty(), 0, Interpolator.EASE_IN)));
                disappear.setOnFinished(e -> notificationContainer.getChildren().remove(notif));

                appear.setOnFinished(e -> {
                        Timeline stay = new Timeline(new KeyFrame(Duration.seconds(4), ev -> disappear.play()));
                        stay.play();
                });

                appear.play();
        }

        private Object[] createActionBar() {
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
                return new Object[] { actions, likeBtn };
        }

        private void addDoubleTapToLike(StackPane imageArea, Button likeBtn) {
                imageArea.setOnMouseClicked(e -> {
                        if (e.getClickCount() == 2) {
                                if (!likeBtn.getText().equals("♥")) {
                                        likeBtn.fire();
                                }
                                Label bigHeart = new Label("♥");
                                bigHeart.setStyle("-fx-text-fill: rgba(255, 51, 102, 0.8); -fx-font-size: 120px;");
                                imageArea.getChildren().add(bigHeart);

                                Timeline heartAnim = new Timeline(
                                                new KeyFrame(Duration.ZERO,
                                                                new KeyValue(bigHeart.scaleXProperty(), 0),
                                                                new KeyValue(bigHeart.scaleYProperty(), 0),
                                                                new KeyValue(bigHeart.opacityProperty(), 1)),
                                                new KeyFrame(Duration.millis(300),
                                                                new KeyValue(bigHeart.scaleXProperty(), 1.2,
                                                                                Interpolator.EASE_OUT),
                                                                new KeyValue(bigHeart.scaleYProperty(), 1.2,
                                                                                Interpolator.EASE_OUT)),
                                                new KeyFrame(Duration.millis(500),
                                                                new KeyValue(bigHeart.scaleXProperty(), 1,
                                                                                Interpolator.EASE_IN),
                                                                new KeyValue(bigHeart.scaleYProperty(), 1,
                                                                                Interpolator.EASE_IN)),
                                                new KeyFrame(Duration.millis(800),
                                                                new KeyValue(bigHeart.opacityProperty(), 1)),
                                                new KeyFrame(Duration.millis(1200),
                                                                new KeyValue(bigHeart.opacityProperty(), 0),
                                                                new KeyValue(bigHeart.scaleXProperty(), 1.5),
                                                                new KeyValue(bigHeart.scaleYProperty(), 1.5)));
                                heartAnim.setOnFinished(ev -> imageArea.getChildren().remove(bigHeart));
                                heartAnim.play();
                        }
                });
        }

        private void shakePost(javafx.scene.Node node) {
                Timeline shake = new Timeline(
                                new KeyFrame(Duration.ZERO, new KeyValue(node.translateXProperty(), 0)),
                                new KeyFrame(Duration.millis(50), new KeyValue(node.translateXProperty(), -10)),
                                new KeyFrame(Duration.millis(100), new KeyValue(node.translateXProperty(), 10)),
                                new KeyFrame(Duration.millis(150), new KeyValue(node.translateXProperty(), -10)),
                                new KeyFrame(Duration.millis(200), new KeyValue(node.translateXProperty(), 10)),
                                new KeyFrame(Duration.millis(250), new KeyValue(node.translateXProperty(), 0)));
                shake.play();
        }

        // --- INTERACTIVE SLIDER POST ---
        private VBox createSliderPost(String username, String question, String caption) {
                VBox post = new VBox();
                post.getStyleClass().add("post-container");
                post.setMinHeight(750);
                post.setStyle("-fx-background-color: black;");
                post.getProperties().put("isPoll", true); // Treat like poll to block scroll
                post.getProperties().put("pollResponded", false);

                HBox header = new HBox(10);
                header.setAlignment(Pos.CENTER_LEFT);
                header.setPadding(new Insets(15, 15, 10, 15));
                Circle avatar = new Circle(16, Color.web("#8a2387"));
                Label userLabel = new Label(username);
                userLabel.setStyle(
                                "-fx-text-fill: white; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-font-size: 14px;");
                header.getChildren().addAll(avatar, userLabel);

                StackPane imageArea = new StackPane();
                imageArea.setStyle(
                                "-fx-background-color: linear-gradient(to bottom right, #8a2387, #e94057, #f27121); -fx-background-radius: 15;");
                VBox.setMargin(imageArea, new Insets(0, 10, 0, 10));
                imageArea.setMinHeight(400);

                VBox contentBox = new VBox(30);
                contentBox.setAlignment(Pos.CENTER);
                contentBox.setPadding(new Insets(30));

                Label questionLabel = new Label(question);
                questionLabel.setStyle(
                                "-fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold;");
                questionLabel.setWrapText(true);
                questionLabel.setTextAlignment(TextAlignment.CENTER);

                Slider slider = new Slider(0, 100, 50);
                slider.setStyle("-fx-control-inner-background: #ffffff; -fx-accent: #34c759;");

                Button submitBtn = new Button("Confirm");
                submitBtn.setStyle(
                                "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20; -fx-cursor: hand;");

                submitBtn.setOnAction(e -> {
                        post.getProperties().put("pollResponded", true);
                        submitBtn.setText("Submitted: " + (int) slider.getValue());
                        submitBtn.setStyle(
                                        "-fx-background-color: #34c759; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 20;");
                        submitBtn.setDisable(true);
                        slider.setDisable(true);
                        triggerPollHighlight("Measuring your distress. The algorithm notes your response.");
                });

                contentBox.getChildren().addAll(questionLabel, slider, submitBtn);
                imageArea.getChildren().add(contentBox);

                Object[] actionRes = createActionBar();
                HBox actions = (HBox) actionRes[0];
                addDoubleTapToLike(imageArea, (Button) actionRes[1]);

                Label captionLabel = new Label(username + " " + caption);
                captionLabel.setStyle(
                                "-fx-text-fill: #dddddd; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px; -fx-padding: 5 15 20 15;");
                captionLabel.setWrapText(true);

                post.getChildren().addAll(header, imageArea, actions, captionLabel);
                return post;
        }

        // --- AD POST ---
        private VBox createAdPost(String brand, String adTitle, String caption) {
                VBox post = new VBox();
                post.getStyleClass().add("post-container");
                post.setMinHeight(750);
                post.setStyle("-fx-background-color: black;");
                post.getProperties().put("isAd", true);
                post.getProperties().put("adFinished", false);

                HBox header = new HBox(10);
                header.setAlignment(Pos.CENTER_LEFT);
                header.setPadding(new Insets(15, 15, 10, 15));
                Circle avatar = new Circle(16, Color.GOLD);
                Label userLabel = new Label("Sponsored");
                userLabel.setStyle(
                                "-fx-text-fill: rgba(255,255,255,0.7); -fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 12px;");
                Label brandLabel = new Label(brand);
                brandLabel.setStyle(
                                "-fx-text-fill: white; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-font-size: 14px;");
                header.getChildren().addAll(avatar, brandLabel, new Region(), userLabel);
                HBox.setHgrow(header.getChildren().get(2), Priority.ALWAYS); // Push "Sponsored" to right

                StackPane imageArea = new StackPane();
                imageArea.setStyle(
                                "-fx-background-color: linear-gradient(to bottom right, #333333, #111111); -fx-background-radius: 15;");
                VBox.setMargin(imageArea, new Insets(0, 10, 0, 10));
                imageArea.setMinHeight(400);

                VBox contentBox = new VBox(20);
                contentBox.setAlignment(Pos.CENTER);
                contentBox.setPadding(new Insets(30));

                Label titleLabel = new Label(adTitle);
                titleLabel.setStyle(
                                "-fx-text-fill: gold; -fx-font-size: 28px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold;");
                titleLabel.setWrapText(true);
                titleLabel.setTextAlignment(TextAlignment.CENTER);

                Button skipBtn = new Button("Skip in 3s");
                skipBtn.setStyle(
                                "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 16;");
                skipBtn.setDisable(true);

                contentBox.getChildren().addAll(titleLabel, skipBtn);
                imageArea.getChildren().add(contentBox);

                Object[] actionRes = createActionBar();
                HBox actions = (HBox) actionRes[0];
                addDoubleTapToLike(imageArea, (Button) actionRes[1]);

                Label captionLabel = new Label(brand + " " + caption);
                captionLabel.setStyle(
                                "-fx-text-fill: #dddddd; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px; -fx-padding: 5 15 20 15;");
                captionLabel.setWrapText(true);

                post.getChildren().addAll(header, imageArea, actions, captionLabel);
                post.getProperties().put("skipBtn", skipBtn);
                return post;
        }

        // --- POLL POST BUILDER ---
        private VBox createPollPost(String username, String question, String option1, String option2, int opt1Pct,
                        int opt2Pct, String caption, String response1, String response2) {
                VBox post = new VBox();
                post.getStyleClass().add("post-container");
                post.setMinHeight(750);
                post.setStyle("-fx-background-color: black;");
                post.getProperties().put("isPoll", true);
                post.getProperties().put("pollResponded", false);

                // Header
                HBox header = new HBox(10);
                header.setAlignment(Pos.CENTER_LEFT);
                header.setPadding(new Insets(15, 15, 10, 15));
                Circle avatar = new Circle(16, Color.GRAY);
                Label userLabel = new Label(username);
                userLabel.setStyle(
                                "-fx-text-fill: white; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-font-size: 14px;");
                header.getChildren().addAll(avatar, userLabel);

                // Content Area
                StackPane imageArea = new StackPane();
                imageArea.setStyle(
                                "-fx-background-color: linear-gradient(to bottom right, #2c1a3b, #1a1a2e); -fx-background-radius: 15;");
                VBox.setMargin(imageArea, new Insets(0, 10, 0, 10));
                imageArea.setMinHeight(400);

                VBox pollBox = new VBox(20);
                pollBox.setAlignment(Pos.CENTER);
                pollBox.setPadding(new Insets(30));

                Label questionLabel = new Label(question);
                questionLabel.setStyle(
                                "-fx-text-fill: white; -fx-font-size: 22px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold;");
                questionLabel.setWrapText(true);
                questionLabel.setTextAlignment(TextAlignment.CENTER);

                VBox buttonsBox = new VBox(15);
                buttonsBox.setAlignment(Pos.CENTER);

                Button btn1 = new Button(option1);
                Button btn2 = new Button(option2);

                String btnStyle = "-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 15 20; -fx-cursor: hand;";
                btn1.setStyle(btnStyle);
                btn2.setStyle(btnStyle);
                btn1.setMaxWidth(Double.MAX_VALUE);
                btn2.setMaxWidth(Double.MAX_VALUE);

                btn1.setOnAction(e -> {
                        post.getProperties().put("pollResponded", true);
                        btn1.setText(option1 + "   " + opt1Pct + "%");
                        btn2.setText(option2 + "   " + opt2Pct + "%");
                        btn1.setStyle(
                                        "-fx-background-color: #34c759; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 15 20;");
                        btn2.setStyle(
                                        "-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: #aaaaaa; -fx-font-size: 16px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 15 20;");
                        btn1.setDisable(true);
                        btn2.setDisable(true);
                        triggerPollHighlight(response1);
                });

                btn2.setOnAction(e -> {
                        post.getProperties().put("pollResponded", true);
                        btn1.setText(option1 + "   " + opt1Pct + "%");
                        btn2.setText(option2 + "   " + opt2Pct + "%");
                        btn2.setStyle(
                                        "-fx-background-color: #34c759; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 15 20;");
                        btn1.setStyle(
                                        "-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: #aaaaaa; -fx-font-size: 16px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 15 20;");
                        btn1.setDisable(true);
                        btn2.setDisable(true);
                        triggerPollHighlight(response2);
                });

                buttonsBox.getChildren().addAll(btn1, btn2);
                pollBox.getChildren().addAll(questionLabel, buttonsBox);
                imageArea.getChildren().add(pollBox);

                Object[] actionRes = createActionBar();
                HBox actions = (HBox) actionRes[0];
                addDoubleTapToLike(imageArea, (Button) actionRes[1]);

                Label captionLabel = new Label(username + " " + caption);
                captionLabel.setStyle(
                                "-fx-text-fill: #dddddd; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px; -fx-padding: 5 15 20 15;");
                captionLabel.setWrapText(true);

                post.getChildren().addAll(header, imageArea, actions, captionLabel);
                return post;
        }

        // --- ETHOS: EXPERT TESTIMONY POST ---
        private VBox createQuotePost(String expertName, String credentials, String quote, String caption) {
                VBox post = new VBox();
                post.getStyleClass().add("post-container");
                post.setMinHeight(750);
                post.setStyle("-fx-background-color: black;");

                HBox header = new HBox(10);
                header.setAlignment(Pos.CENTER_LEFT);
                header.setPadding(new Insets(15, 15, 10, 15));
                Circle avatar = new Circle(16, Color.WHITE);
                Label userLabel = new Label("Expert Insight");
                userLabel.setStyle(
                                "-fx-text-fill: white; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-font-size: 14px;");
                header.getChildren().addAll(avatar, userLabel);

                StackPane imageArea = new StackPane();
                imageArea.setStyle(
                                "-fx-background-color: linear-gradient(to bottom right, #1f4037, #99f2c8); -fx-background-radius: 15;");
                VBox.setMargin(imageArea, new Insets(0, 10, 0, 10));
                imageArea.setMinHeight(400);

                VBox contentBox = new VBox(20);
                contentBox.setAlignment(Pos.CENTER);
                contentBox.setPadding(new Insets(30));

                Label quoteMark = new Label("“");
                quoteMark.setStyle(
                                "-fx-text-fill: rgba(255,255,255,0.4); -fx-font-size: 80px; -fx-font-family: 'Georgia', serif; -fx-font-weight: bold;");
                quoteMark.setPadding(new Insets(-20, 0, -40, 0));

                Label quoteLabel = new Label(quote);
                quoteLabel.setStyle(
                                "-fx-text-fill: white; -fx-font-size: 22px; -fx-font-family: 'Georgia', serif; -fx-font-style: italic;");
                quoteLabel.setWrapText(true);
                quoteLabel.setTextAlignment(TextAlignment.CENTER);

                VBox authorBox = new VBox(5);
                authorBox.setAlignment(Pos.CENTER);
                Label nameLabel = new Label("- " + expertName);
                nameLabel.setStyle(
                                "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold;");
                Label credLabel = new Label(credentials);
                credLabel.setStyle(
                                "-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 14px; -fx-font-family: 'Segoe UI', sans-serif;");
                authorBox.getChildren().addAll(nameLabel, credLabel);

                contentBox.getChildren().addAll(quoteMark, quoteLabel, authorBox);
                imageArea.getChildren().add(contentBox);

                Object[] actionRes = createActionBar();
                HBox actions = (HBox) actionRes[0];
                addDoubleTapToLike(imageArea, (Button) actionRes[1]);
                Label captionLabel = new Label("Ethos \u2022 " + caption);
                captionLabel.setStyle(
                                "-fx-text-fill: #dddddd; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px; -fx-padding: 5 15 20 15;");
                captionLabel.setWrapText(true);

                post.getChildren().addAll(header, imageArea, actions, captionLabel);
                return post;
        }

        // --- LOGOS: STATISTICS POST ---
        private VBox createStatPost(String source, String bigNumber, String statDesc, String caption) {
                VBox post = new VBox();
                post.getStyleClass().add("post-container");
                post.setMinHeight(750);
                post.setStyle("-fx-background-color: black;");

                HBox header = new HBox(10);
                header.setAlignment(Pos.CENTER_LEFT);
                header.setPadding(new Insets(15, 15, 10, 15));
                Circle avatar = new Circle(16, Color.web("#4facfe"));
                Label userLabel = new Label("Data & Research");
                userLabel.setStyle(
                                "-fx-text-fill: white; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-font-size: 14px;");
                header.getChildren().addAll(avatar, userLabel);

                StackPane imageArea = new StackPane();
                imageArea.setStyle(
                                "-fx-background-color: linear-gradient(to bottom right, #000428, #004e92); -fx-background-radius: 15;");
                VBox.setMargin(imageArea, new Insets(0, 10, 0, 10));
                imageArea.setMinHeight(400);

                VBox contentBox = new VBox(15);
                contentBox.setAlignment(Pos.CENTER);
                contentBox.setPadding(new Insets(30));

                Label numberLabel = new Label(bigNumber);
                numberLabel.setStyle(
                                "-fx-text-fill: #00f2fe; -fx-font-size: 72px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,242,254,0.5), 10, 0, 0, 0);");

                Label descLabel = new Label(statDesc);
                descLabel.setStyle(
                                "-fx-text-fill: white; -fx-font-size: 20px; -fx-font-family: 'Segoe UI', sans-serif;");
                descLabel.setWrapText(true);
                descLabel.setTextAlignment(TextAlignment.CENTER);

                Label sourceLabel = new Label("Source: " + source);
                sourceLabel.setStyle(
                                "-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 12px; -fx-font-family: 'Segoe UI', sans-serif;");

                contentBox.getChildren().addAll(numberLabel, descLabel, sourceLabel);
                imageArea.getChildren().add(contentBox);

                Object[] actionRes = createActionBar();
                HBox actions = (HBox) actionRes[0];
                addDoubleTapToLike(imageArea, (Button) actionRes[1]);
                Label captionLabel = new Label("Logos \u2022 " + caption);
                captionLabel.setStyle(
                                "-fx-text-fill: #dddddd; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px; -fx-padding: 5 15 20 15;");
                captionLabel.setWrapText(true);

                post.getChildren().addAll(header, imageArea, actions, captionLabel);
                return post;
        }

        // --- PATHOS: EMOTIONAL APPEAL POST ---
        private VBox createPathosPost(String username, String boldStatement, String subStatement, String caption) {
                VBox post = new VBox();
                post.getStyleClass().add("post-container");
                post.setMinHeight(750);
                post.setStyle("-fx-background-color: black;");

                HBox header = new HBox(10);
                header.setAlignment(Pos.CENTER_LEFT);
                header.setPadding(new Insets(15, 15, 10, 15));
                Circle avatar = new Circle(16, Color.web("#ff416c"));
                Label userLabel = new Label(username);
                userLabel.setStyle(
                                "-fx-text-fill: white; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-font-size: 14px;");
                header.getChildren().addAll(avatar, userLabel);

                StackPane imageArea = new StackPane();
                imageArea.setStyle(
                                "-fx-background-color: linear-gradient(to bottom right, #ff4b2b, #ff416c); -fx-background-radius: 15;");
                VBox.setMargin(imageArea, new Insets(0, 10, 0, 10));
                imageArea.setMinHeight(400);

                VBox contentBox = new VBox(20);
                contentBox.setAlignment(Pos.CENTER);
                contentBox.setPadding(new Insets(40));

                Label boldLabel = new Label(boldStatement);
                boldLabel.setStyle(
                                "-fx-text-fill: white; -fx-font-size: 28px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold;");
                boldLabel.setWrapText(true);
                boldLabel.setTextAlignment(TextAlignment.CENTER);

                Label subLabel = new Label(subStatement);
                subLabel.setStyle(
                                "-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 18px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-style: italic;");
                subLabel.setWrapText(true);
                subLabel.setTextAlignment(TextAlignment.CENTER);

                contentBox.getChildren().addAll(boldLabel, subLabel);
                imageArea.getChildren().add(contentBox);

                Object[] actionRes = createActionBar();
                HBox actions = (HBox) actionRes[0];
                addDoubleTapToLike(imageArea, (Button) actionRes[1]);
                Label captionLabel = new Label("Pathos \u2022 " + caption);
                captionLabel.setStyle(
                                "-fx-text-fill: #dddddd; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px; -fx-padding: 5 15 20 15;");
                captionLabel.setWrapText(true);

                post.getChildren().addAll(header, imageArea, actions, captionLabel);
                return post;
        }

        // --- IMAGE METADATA ---
        private static class ImageMetadata {
                String username;
                String caption;
                double rotation;

                ImageMetadata(String u, String c, double r) {
                        this.username = u;
                        this.caption = c;
                        this.rotation = r;
                }
        }

        private ImageMetadata getMetadataForImage(String filename) {
                if (filename.equals("IMG_8083.jpeg"))
                        return new ImageMetadata("sarah.explores", "Made a new friend today!", 90);
                if (filename.equals("IMG_8101.jpeg"))
                        return new ImageMetadata("jake.golfs",
                                        "Perfect day for a round of golf. That view of the mountains is unbeatable. \u26f3\ufe0f\u26f0\ufe0f",
                                        90);
                if (filename.equals("IMG_8104.jpeg"))
                        return new ImageMetadata("emily.designs",
                                        "Stunning estate and gardens. The hydrangeas are in full bloom! \ud83c\udf38\ud83c\udfdb\ufe0f",
                                        0);
                if (filename.equals("IMG_8295.jpeg"))
                        return new ImageMetadata("cafe.hopper.dan",
                                        "A proper toasted sandwich and fresh juice to start the morning right. \ud83e\udd6a\ud83e\uddc3",
                                        90);
                if (filename.equals("IMG_8602.jpeg"))
                        return new ImageMetadata("plant.mom.chloe",
                                        "Lost inside this beautiful glass conservatory. Look at those giant banana leaves! \ud83c\udf3f\ud83c\udf31",
                                        90);

                return new ImageMetadata("unknown_user", "Just another post in the void...", 0);
        }

        // --- IMAGE POST ---
        private VBox createImagePost(String imagePath) {
                String filename = imagePath.substring(imagePath.lastIndexOf('/') + 1);
                ImageMetadata meta = getMetadataForImage(filename);
                String username = meta.username;
                String caption = meta.caption;
                double rotation = meta.rotation;

                VBox post = new VBox();
                post.getStyleClass().add("post-container");
                post.setMinHeight(750);
                post.setStyle("-fx-background-color: black;");

                HBox header = new HBox(10);
                header.setAlignment(Pos.CENTER_LEFT);
                header.setPadding(new Insets(15, 15, 10, 15));

                // Random color generation logic
                int hash = username.hashCode();
                String colorHex = String.format("#%06x", (hash & 0xFFFFFF) | 0x444444);
                Circle avatar = new Circle(16, Color.web(colorHex));

                Label userLabel = new Label(username);
                userLabel.setStyle(
                                "-fx-text-fill: white; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-font-size: 14px;");
                header.getChildren().addAll(avatar, userLabel);

                StackPane imageArea = new StackPane();
                imageArea.setStyle(
                                "-fx-background-color: #111111; -fx-background-radius: 15;");
                VBox.setMargin(imageArea, new Insets(0, 10, 0, 10));
                imageArea.setMinHeight(450);
                imageArea.setMaxHeight(450);
                imageArea.setMinWidth(360);
                imageArea.setMaxWidth(360);

                try {
                        java.io.InputStream is = getClass().getResourceAsStream(imagePath);
                        if (is != null) {
                                javafx.scene.image.Image img = new javafx.scene.image.Image(is);
                                javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(img);

                                double targetWidth = 360;
                                double targetHeight = 450;

                                double preRotWidth = targetWidth;
                                double preRotHeight = targetHeight;
                                if (rotation == 90 || rotation == -90 || rotation == 270) {
                                        preRotWidth = targetHeight;
                                        preRotHeight = targetWidth;
                                }

                                double imgW = img.getWidth();
                                double imgH = img.getHeight();

                                double targetRatio = preRotWidth / preRotHeight;
                                double imgRatio = imgW / imgH;

                                double cropW, cropH, cropX, cropY;

                                if (imgRatio > targetRatio) {
                                        cropH = imgH;
                                        cropW = imgH * targetRatio;
                                        cropX = (imgW - cropW) / 2;
                                        cropY = 0;
                                } else {
                                        cropW = imgW;
                                        cropH = imgW / targetRatio;
                                        cropX = 0;
                                        cropY = (imgH - cropH) / 2;
                                }

                                imageView.setViewport(new javafx.geometry.Rectangle2D(cropX, cropY, cropW, cropH));
                                imageView.setFitWidth(preRotWidth);
                                imageView.setFitHeight(preRotHeight);
                                imageView.setPreserveRatio(true);
                                imageView.setRotate(rotation);

                                // Clip to rounded rectangle
                                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(preRotWidth,
                                                preRotHeight);
                                clip.setArcWidth(30);
                                clip.setArcHeight(30);
                                imageView.setClip(clip);

                                imageArea.getChildren().add(imageView);
                        } else {
                                Label error = new Label("Image not found: " + imagePath);
                                error.setTextFill(Color.WHITE);
                                imageArea.getChildren().add(error);
                        }
                } catch (Exception e) {
                        Label error = new Label("Error loading image");
                        error.setTextFill(Color.WHITE);
                        imageArea.getChildren().add(error);
                }

                Object[] actionRes = createActionBar();
                HBox actions = (HBox) actionRes[0];
                addDoubleTapToLike(imageArea, (Button) actionRes[1]);

                javafx.scene.text.Text userText = new javafx.scene.text.Text(username + " ");
                userText.setStyle(
                                "-fx-fill: white; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px; -fx-font-weight: bold;");

                javafx.scene.text.Text captionTextObj = new javafx.scene.text.Text(caption);
                captionTextObj.setStyle(
                                "-fx-fill: #dddddd; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px;");

                javafx.scene.text.TextFlow captionFlow = new javafx.scene.text.TextFlow(userText, captionTextObj);
                captionFlow.setPadding(new Insets(5, 15, 20, 15));

                post.getChildren().addAll(header, imageArea, actions, captionFlow);
                return post;
        }

        public static void main(String[] args) {
                launch(args);
        }
}