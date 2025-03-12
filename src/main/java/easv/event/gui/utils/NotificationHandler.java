package easv.event.gui.utils;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

import java.util.ArrayList;
import java.util.List;

public class NotificationHandler {
    private static NotificationHandler instance;
    private final StackPane notificationContainer;
    private final List<Notification> notifications;

    private static final int MAX_NOTIFICATIONS = 4;
    private static final double NOTIFICATION_MARGIN = 10; // Margin between notifications

    // Private constructor for singleton pattern
    private NotificationHandler(StackPane notificationContainer) {
        this.notificationContainer = notificationContainer;
        this.notifications = new ArrayList<>();
    }

    // Static method to get the singleton instance
    public static void initialize(StackPane notificationContainer) {
        if (instance == null) {
            instance = new NotificationHandler(notificationContainer);
        }
    }

    public static NotificationHandler getInstance() {
        if (instance == null) {
            throw new IllegalStateException("NotificationManager is not initialized. Call initialize() first.");
        }
        return instance;
    }

    public enum NotificationType {
        INFO,
        SUCCESS,
        WARNING,
        DANGER
    }

    public void showNotification(String message, NotificationType type) {
        if (notifications.size() >= MAX_NOTIFICATIONS) {
            removeOldestNotification();
        }

        final var notification = new Notification(
                message,
                new FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        );
        notification.getStyleClass().add(Styles.ELEVATED_1);
        switch (type) {
            case INFO:
                notification.getStyleClass().add(Styles.ACCENT);
                break;
            case SUCCESS:
                notification.getStyleClass().add(Styles.SUCCESS);
                break;
            case WARNING:
                notification.getStyleClass().add(Styles.WARNING);
                break;
            case DANGER:
                notification.getStyleClass().add(Styles.DANGER);
                break;
        }

        notification.setPrefHeight(Region.USE_PREF_SIZE);
        notification.setMaxHeight(Region.USE_PREF_SIZE);
        double totalHeight = notifications.stream()
                .mapToDouble(n -> n.getHeight() + NOTIFICATION_MARGIN)
                .sum();
        StackPane.setAlignment(notification, Pos.TOP_RIGHT);
        StackPane.setMargin(notification, new Insets(totalHeight + NOTIFICATION_MARGIN, 10, 0, 0));

        // Add the notification to the container and animate it
        notificationContainer.getChildren().add(notification);
        notifications.add(notification);
        playNotificationAnimation(notification);

        // Set up the close action for the notification
        notification.setOnClose(e -> removeNotification(notification));

        // Remove the notification after 5 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(event -> removeNotification(notification));
        pause.play();
    }

    private void removeOldestNotification() {
        if (!notifications.isEmpty()) {
            Notification oldestNotification = notifications.get(0);
            removeNotification(oldestNotification);
        }
    }

    private void removeNotification(Notification notification) {
        // Animate the closing of the notification
        var out = Animations.slideOutRight(notification, Duration.millis(250));
        out.setOnFinished(f -> {
            notificationContainer.getChildren().remove(notification);
            notifications.remove(notification);

            animateRemainingNotifications();
        });
        out.playFromStart();
    }

    private void animateRemainingNotifications() {
        double totalHeight = 0;
        for (Notification notification : notifications) {
            // Update the margin to move the notification up in the layout
            StackPane.setMargin(notification, new Insets(totalHeight + NOTIFICATION_MARGIN, 10, 0, 0));

            // Create an animation to slide the notification in from above
            var moveUpAnimation = Animations.slideInUp(notification, Duration.millis(250));
            moveUpAnimation.play();

            // Increase totalHeight for the next notification
            totalHeight += notification.getHeight() + NOTIFICATION_MARGIN;
        }
    }

    private void playNotificationAnimation(Notification notification) {
        // Slide in animation for the notification
        var in = Animations.slideInDown(notification, Duration.millis(250));
        in.playFromStart();
    }

}
