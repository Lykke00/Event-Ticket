package easv.event.gui.utils;

import javafx.concurrent.Task;
import javafx.beans.property.BooleanProperty;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BackgroundTaskExecutor {

    /**
     * Runs a database operation in the background and updates the UI thread when finished.
     * @param backgroundTask The operation to execute (e.g., database call).
     * @param onSuccess The function to execute on the UI thread when successful.
     * @param onFailure The function to execute if an error occurs.
     * @param <T> The type of data returned by the task.
     */
    public static <T> void execute(Supplier<T> backgroundTask, Consumer<T> onSuccess, Consumer<Exception> onFailure) {
        execute(backgroundTask, onSuccess, onFailure, null); // Default: no onLoading
    }

    /**
     * Runs a database operation in the background and updates the UI thread when finished.
     * @param backgroundTask The operation to execute (e.g., database call).
     * @param onSuccess The function to execute on the UI thread when successful.
     * @param onFailure The function to execute if an error occurs.
     * @param onLoading The function to handle the loading state (optional).
     * @param <T> The type of data returned by the task.
     */
    public static <T> void execute(Supplier<T> backgroundTask, Consumer<T> onSuccess, Consumer<Exception> onFailure, Consumer<Boolean> onLoading) {
        if (onLoading != null) {
            onLoading.accept(true); // Indicate loading started
        }

        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                try {
                    return backgroundTask.get(); // Run the background task
                } catch (Exception e) {
                    // Propagate the exception to the failure handler
                    throw e;
                }
            }
        };

        // Success handling
        task.setOnSucceeded(event -> {
            T result = task.getValue();
            if (result != null) {
                onSuccess.accept(result); // If successful, call the onSuccess handler
            } else {
                onFailure.accept(new Exception("No data returned")); // Handle no result case
            }

            // End loading
            if (onLoading != null) {
                onLoading.accept(false); // Indicate loading finished
            }
        });

        // Failure handling (ensure Exception type)
        task.setOnFailed(event -> {
            Throwable error = task.getException();
            if (error instanceof Exception exception) {
                onFailure.accept(exception); // Pass the exception to onFailure
            } else {
                onFailure.accept(new Exception("Unknown error occurred", error)); // Wrap error in exception
            }

            // End loading
            if (onLoading != null) {
                onLoading.accept(false); // Indicate loading finished
            }
        });

        // Start the task in a new thread
        new Thread(task).start();
    }
}