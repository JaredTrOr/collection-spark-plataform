package org.jared.trujillo.controller;
import org.jared.trujillo.dto.HttpSimpleResponse;
import org.jared.trujillo.dto.HttpStatus;
import org.jared.trujillo.dto.Page;
import org.jared.trujillo.exceptions.ResourceNotFoundException;
import org.jared.trujillo.interfaces.JsonConverter;
import org.jared.trujillo.model.Item;
import org.jared.trujillo.service.ItemService;

import java.util.UUID;

import static spark.Spark.*;

public class ItemController {

    private final ItemService itemService;
    private final JsonConverter json;

    public ItemController(ItemService itemService, JsonConverter jsonConverter) {
        this.itemService = itemService;
        this.json = jsonConverter;
    }

    public void registerRoutes() {

        after((req, res) -> res.type("application/json"));

        get("", (req, res) -> {
           String pageParam = req.queryParamOrDefault("page", "1");
           String limitParam = req.queryParamOrDefault("limit", "20");

           int page;
           int limit;
           try {
               page = Integer.parseInt(pageParam);
               limit = Integer.parseInt(limitParam);
           } catch (NumberFormatException e) {
               throw new IllegalArgumentException("Query parameters 'page' and 'limit' must be numbers.");
           }

            Page<Item> itemPage = this.itemService.getPaginatedItems(page, limit);

            res.status(HttpStatus.OK.getStatusCode());
            return json.toJson(HttpSimpleResponse.success(itemPage));
        });

        get("/:id", (req, res) -> {
            String idString = req.params(":id");
            UUID id = UUID.fromString(idString);
            Item item = this.itemService.getItemById(id);
            res.status(HttpStatus.OK.getStatusCode());
           return json.toJson(HttpSimpleResponse.success(item));
        });

        post("", (req,res) -> {
            Item item = this.json.fromJson(req.body(), Item.class);
            Item createdItem = this.itemService.createItem(item);
            res.status(HttpStatus.CREATED.getStatusCode());
            return json.toJson(HttpSimpleResponse.success(createdItem, "Item created successfully"));
        });

        put("/:id", (req,res) -> {
            String idString = req.params(":id");
            UUID id = UUID.fromString(idString);
            Item item = this.json.fromJson(req.body(), Item.class);

            Item updatedItem = this.itemService.updateItem(id, item);
            res.status(HttpStatus.OK.getStatusCode());
            return json.toJson(HttpSimpleResponse.success(updatedItem, "Item updated successfully"));
        });

        delete("/:id", (req,res) -> {

            String idString = req.params(":id");
            UUID id = UUID.fromString(idString);
            this.itemService.deleteItem(id);

            res.status(HttpStatus.OK.getStatusCode());
            return json.toJson(HttpSimpleResponse.success("Item deleted successfully"));
        });

        exception(ResourceNotFoundException.class, (exception, req, res) -> {
            res.status(HttpStatus.NOT_FOUND.getStatusCode());
            res.body(json.toJson(HttpSimpleResponse.error(exception.getMessage())));
        });

        exception(IllegalArgumentException.class, (exception, req, res) -> {
            res.status(HttpStatus.BAD_REQUEST.getStatusCode());
            res.body(json.toJson(HttpSimpleResponse.error(exception.getMessage())));
        });
    }

}
