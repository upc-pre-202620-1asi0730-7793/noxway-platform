package com.noctiva.noxway.shared.interfaces.rest.resources;

import java.util.List;

/**
 * Paginated response resource wrapper.
 *
 * @param items page content items
 * @param page zero-based page index
 * @param size page size
 * @param total total matching element count
 * @param <T> item resource type
 */
public record PagedResource<T>(List<T> items, int page, int size, long total) {}
