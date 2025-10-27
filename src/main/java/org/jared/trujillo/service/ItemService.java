package org.jared.trujillo.service;

import org.jared.trujillo.exceptions.ResourceNotFoundException;
import org.jared.trujillo.model.Item;
import org.jared.trujillo.interfaces.ItemRepository;

import java.util.List;
import java.util.UUID;

public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemsRepository) {
        this.itemRepository = itemsRepository;
    }

    public Item createItem(Item item) {
        return this.itemRepository.create(item);
    }

    public List<Item> getAllItems() {
        return this.itemRepository.findAll();
    }

    public Item getItemById(UUID id) {
        return this.itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: "+id));
    }

    public Item updateItem(UUID id, Item itemDetails) {
        getItemById(id);
        return itemRepository.update(id, itemDetails)
                .orElseThrow(() -> new RuntimeException("Error updating item"));
    }

    public void deleteItem(UUID id) {
        boolean wasDeleted = itemRepository.delete(id);

        if (!wasDeleted) {
            throw new ResourceNotFoundException("Item not found with id: " + id);
        }
    }

}
