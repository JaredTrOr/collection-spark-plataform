package org.jared.trujillo.controller;

import org.jared.trujillo.dto.Page;
import org.jared.trujillo.model.Item;
import org.jared.trujillo.service.ItemService;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;

public class ItemWebController {

    private final ItemService itemService;

    public ItemWebController(ItemService itemService) {
        this.itemService = itemService;
    }

    public void registerRoutes() {

        get("", (req, res) -> {

            String searchQuery = req.queryParamOrDefault("search", "");
            String pageParam = req.queryParamOrDefault("page", "1");

            int page;
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }

            Page<Item> itemPage = this.itemService.getPaginatedItems(page, 20);

            Map<String, Object> model = new HashMap<>();
            model.put("items", itemPage.getData());
            model.put("page", itemPage.getPage());
            model.put("totalPages", itemPage.getTotalPages());
            model.put("searchQuery", searchQuery);

            if (itemPage.getPage() > 1) {
                model.put("hasPrevious", true);
                model.put("prevPage", itemPage.getPage() - 1);
            }
            if (itemPage.getPage() < itemPage.getTotalPages()) {
                model.put("hasNext", true);
                model.put("nextPage", itemPage.getPage() + 1);
            }

            return new ModelAndView(model, "home.mustache");

        }, new MustacheTemplateEngine());

    }

}
