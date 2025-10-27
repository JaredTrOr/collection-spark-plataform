package org.jared.trujillo;

import org.jared.trujillo.controller.ItemController;
import org.jared.trujillo.dto.HttpSimpleResponse;
import org.jared.trujillo.dto.HttpStatus;
import org.jared.trujillo.interfaces.ItemRepository;
import org.jared.trujillo.interfaces.JsonConverter;
import org.jared.trujillo.repository.PostgresItemRepository;
import org.jared.trujillo.service.ItemService;
import org.jared.trujillo.utils.GsonConverter;

import static spark.Spark.after;
import static spark.Spark.path;
import static spark.Spark.get;
import static spark.Spark.exception;


public class ApiRouter {

    private final ItemController itemController;
    private final JsonConverter json;

    public ApiRouter() {
        // Dependency setup
        this.json = new GsonConverter();

        // Repository setup
        ItemRepository itemRepository = new PostgresItemRepository();

        // Service setup
        ItemService itemService = new ItemService(itemRepository);

        // Controller setup
        this.itemController = new ItemController(itemService, this.json);
    }

    public void startRoutes() {
        after((req, res) -> res.type("application/json"));

        get("/", (req, res) -> {
            res.type("text/plain");
            return "Server running successfully";
        });

        path("/api/v1", () -> {
            path("/items", this.itemController::registerRoutes);
        });

        exception(Exception.class, (exception, req, res) -> {
            exception.printStackTrace();
            res.status(HttpStatus.INTERNAL_SERVER_ERROR.getStatusCode());

            res.body(this.json.toJson(
                    HttpSimpleResponse.error("An unexpected server error occurred.")
            ));
        });
    }

}
