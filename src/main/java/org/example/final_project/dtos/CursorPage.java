package org.example.final_project.dtos;

import java.util.List;

public record CursorPage<T>(List<T> items, boolean hasMore, String nextCursor) {}
