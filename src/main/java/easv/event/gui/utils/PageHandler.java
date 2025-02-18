package easv.event.gui.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class PageHandler {
    private static PageHandler instance;
    private final static Pages DEFAULT_PAGE = Pages.EVENT;

    private Map<Pages, Node> pageCache = new HashMap<>();

    private Pages currentPage;

    private BorderPane borderPane;
    private StackPane root;

    private PageHandler() {}

    public static PageHandler getInstance() {
        if (instance == null)
            instance = new PageHandler();

        return instance;
    }

    public void setBorderPane(BorderPane borderPane) {
        this.borderPane = borderPane;
        setCurrentPage(DEFAULT_PAGE);
    }

    public void setCurrentPage(Pages page) {
        if (currentPage != null && page.getPath().equals(currentPage.getPath()))
            return;

        FXMLLoader loader;
        Node pageNode;

        if (!pageCache.containsKey(page)) {
            loader = new FXMLLoader(getClass().getResource(page.getPath()));
            try {
                pageNode = loader.load();
                page.setController(loader.getController()); // Store controller in Pages enum
                pageCache.put(page, pageNode);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load page: " + page.getPath(), e);
            }
        } else {
            pageNode = pageCache.get(page);
        }

        currentPage = page;
        this.borderPane.setCenter(pageNode);
    }
}
