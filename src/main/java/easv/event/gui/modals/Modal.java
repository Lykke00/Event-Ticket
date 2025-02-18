package easv.event.gui.modals;

public enum Modal {
    ADDNEWEVENT("/fxml/modals/AddNewEvent.fxml"),
    EDITEVENT("/fxml/modals/EditEvent.fxml"),
    USERS("/fxml/pages/UserPage.fxml");

    private final String path;

    Modal(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
