package ru.practicum.shareit.configuration;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exeptions.ValidationException;

public class PageHandlerRequest implements Pageable {
    private final int from;
    private final int size;
    private final Sort sort;

    protected PageHandlerRequest(int from, int size, Sort sort) {
        this.from = from;
        this.size = size;
        this.sort = sort;
    }

    public static Pageable of(int from, int size) {
        if (from < 0 || size < 1) throw new ValidationException("Не корректные данные");
        return new PageHandlerRequest(from, size, Sort.unsorted());
    }

    public static Pageable of(int from, int size, Sort sort) {
        if (from < 0 || size < 1) throw new ValidationException("Не корректные данные");
        return new PageHandlerRequest(from, size, sort);
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public long getOffset() {
        return from;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new PageHandlerRequest(from + size, size, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return new PageHandlerRequest(from - size, size, sort);
    }

    @Override
    public Pageable first() {
        return new PageHandlerRequest(0, size, sort);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new PageHandlerRequest(pageNumber * size + from, size, sort);
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
