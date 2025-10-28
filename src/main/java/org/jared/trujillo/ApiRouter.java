package org.jared.trujillo;

import org.jared.trujillo.controller.ItemController;
import org.jared.trujillo.controller.UserController;
import org.jared.trujillo.dto.HttpSimpleResponse;
import org.jared.trujillo.dto.HttpStatus;
import org.jared.trujillo.interfaces.JsonConverter;
import org.jared.trujillo.repository.PostgresItemRepository;
import org.jared.trujillo.repository.PostgresUserRepository;
import org.jared.trujillo.service.ItemService;
import org.jared.trujillo.service.UserService;
import org.jared.trujillo.utils.GsonConverter;

import static spark.Spark.after;
import static spark.Spark.path;
import static spark.Spark.get;
import static spark.Spark.exception;


public class ApiRouter {

    private final ItemController itemController;
    private final UserController userController;

    private final JsonConverter json;

    public ApiRouter() {
        // Dependency setup
        this.json = new GsonConverter();

        // Service setup
        ItemService itemService = new ItemService(new PostgresItemRepository());
        UserService userService = new UserService(new PostgresUserRepository());

        // Controller setup
        this.itemController = new ItemController(itemService, this.json);
        this.userController = new UserController(userService, this.json);
    }

    public void startRoutes() {
        after((req, res) -> res.type("application/json"));

        get("/", (req, res) -> {
            res.type("text/plain");
            return "Server running successfully";
        });

        path("/api/v1", () -> {
            path("/items", this.itemController::registerRoutes);
            path("/users", this.userController::registerRoutes);
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
