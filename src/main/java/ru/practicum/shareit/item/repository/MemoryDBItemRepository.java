package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class MemoryDBItemRepository implements  ItemRepository {
    long id = 0;
    private Map<Long, List<Item>> itemDBTemp = new HashMap<>();

    @Override
    public Item addItem(long userId, Item item) {
        if (item == null) throw new ValidationException("Нет данных для добавления");
        List<Item> itemsAdd = new ArrayList<>();
        item.setId(generateId());
        if (itemDBTemp.containsKey(userId)){
            List<Item> items = itemDBTemp.get(userId);
            items.add(item);
            itemDBTemp.put(userId, items);
        } else {
            itemsAdd.add(item);
            itemDBTemp.put(userId, itemsAdd);
        }
        return item;
    }

    @Override
    public List<Item> searchItem(String text) {
        String searchText = text.toLowerCase();
        List<Item> items = itemDBTemp.values().stream()
                .flatMap(Collection::stream)
                .filter(o -> o.getAvailable()
                        && (o.getName().toLowerCase().contains(searchText)
                        || o.getDescription().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());
        return items;
    }

    @Override
    public Item changeItem(long userId, long itemId, Item item) {
        if (itemDBTemp.containsKey(userId) && item != null){
            return itemDBTemp.values().stream()
                    .flatMap(Collection::stream)
                    .filter(o -> o.getId() == itemId)
                    .map(o -> o = item)
                    .findAny().orElseThrow(() -> new NotFoundException("Вешь с таким id отсутствует"));
        } else {
            throw new NotFoundException("Проверьте правильность данных");
        }
    }

    @Override
    public Item findItemById(long itemId) {
        Item item = itemDBTemp.values().stream()
                .flatMap(Collection::stream)
                .filter(o -> o.getId() == itemId)
                .findAny().orElseThrow(() -> new NotFoundException("Вешь с таким id отсутствует"));
        return item;
    }

    @Override
    public List<Item> findByUserId(long userId) {
        if (itemDBTemp.containsKey(userId)){
            return itemDBTemp.get(userId);
        } else {
            throw new NotFoundException("Пользователь отсутствует");
        }
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        if (itemDBTemp.containsKey(userId)){
            Item deleteItem = itemDBTemp.get(userId).stream()
                    .filter(o -> o.getId() == itemId)
                    .findFirst().orElseThrow(() -> new NotFoundException("Вешь с таким id отсутствует"));
            itemDBTemp.values().remove(deleteItem);
        } else {
            throw new NotFoundException("Пользователь отсутствует");
        }
    }

    private long generateId() {
        return id += 1;
    }
}
