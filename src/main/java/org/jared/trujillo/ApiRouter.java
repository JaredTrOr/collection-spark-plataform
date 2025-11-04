package org.jared.trujillo;

import org.jared.trujillo.controller.*;
import org.jared.trujillo.dto.HttpSimpleResponse;
import org.jared.trujillo.dto.HttpStatus;
import org.jared.trujillo.interfaces.JsonConverter;
import org.jared.trujillo.repository.PostgresItemRepository;
import org.jared.trujillo.repository.PostgresUserRepository;
import org.jared.trujillo.service.ItemService;
import org.jared.trujillo.service.UserService;
import org.jared.trujillo.utils.GsonConverter;

import static spark.Spark.*;


public class ApiRouter {

    // Api controllers
    private final ItemController itemController;
    private final UserController userController;

    // Views
    private final ItemWebController itemWebController;
    private final ClientWebController clientWebController;

    private final JsonConverter json;

    public ApiRouter() {
        // Dependency setup
        this.json = new GsonConverter();

        // Service setup
        ItemService itemService = new ItemService(new PostgresItemRepository());
        UserService userService = new UserService(new PostgresUserRepository());

        // Controller API setup
        this.itemController = new ItemController(itemService, this.json);
        this.userController = new UserController(userService, this.json);

        // Views controller setup
        this.itemWebController = new ItemWebController(itemService);
        this.clientWebController = new ClientWebController(itemService);

    }

    public void startRoutes() {

        staticFiles.location("/public");

        webSocket("/price-updates", PriceUpdateSocketHandler.class);

        get("/", (req, res) -> {
            res.type("text/plain");
            return "Server running successfully";
        });

        path("/api/v1", () -> {
            after((req, res) -> res.type("application/json"));
            path("/items", this.itemController::registerRoutes);
            path("/users", this.userController::registerRoutes);
        });

        path("/admin-home", this.itemWebController::registerRoutes);
        path("/client-home", this.clientWebController::registerRoutes);

        exception(Exception.class, (exception, req, res) -> {
            exception.printStackTrace();
            res.status(HttpStatus.INTERNAL_SERVER_ERROR.getStatusCode());

            res.body(this.json.toJson(
                    HttpSimpleResponse.error("An unexpected server error occurred.")
            ));
        });
    }

}
