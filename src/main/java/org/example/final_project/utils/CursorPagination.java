package org.example.final_project.utils;

import org.example.final_project.dtos.CursorPage;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;

/** Timestamp plus ID forms a stable boundary even when timestamps are equal. */
public final class CursorPagination {
    private CursorPagination() {}

    public record Boundary(boolean firstPage, LocalDateTime time, long id, boolean nullTime) {}

    public static Boundary parse(int size, String cursor) {
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("size must be between 1 and 100");
        }
        if (cursor == null) {
            return new Boundary(true, LocalDateTime.of(1970, 1, 1, 0, 0), 0, false);
        }
        try {
            if (cursor.isBlank() || cursor.length() > 160) throw new IllegalArgumentException();
            String[] parts = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8)
                    .split("\\|", -1);
            if (parts.length != 3 || !parts[0].equals("v1")) throw new IllegalArgumentException();
            long id = Long.parseLong(parts[1]);
            if (id <= 0) throw new IllegalArgumentException();
            boolean nullTime = parts[2].equals("null");
            LocalDateTime time = nullTime ? LocalDateTime.of(1970, 1, 1, 0, 0)
                    : LocalDateTime.parse(parts[2]);
            if (time.getYear() < 1 || time.getYear() > 9999) throw new IllegalArgumentException();
            return new Boundary(false, time, id, nullTime);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Invalid cursor");
        }
    }

    /** Query one extra row to determine hasMore without counting the entire result set. */
    public static <T, R> CursorPage<R> page(List<T> rows, int size, Function<T, LocalDateTime> timestamp,
                                           Function<T, Long> id, Function<T, R> mapper) {
        boolean hasMore = rows.size() > size;
        List<T> visible = rows.subList(0, Math.min(size, rows.size()));
        String nextCursor = null;
        if (hasMore) {
            T last = visible.get(visible.size() - 1);
            String value = "v1|" + id.apply(last) + "|" + timestamp.apply(last);
            nextCursor = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(value.getBytes(StandardCharsets.UTF_8));
        }
        return new CursorPage<>(visible.stream().map(mapper).toList(), hasMore, nextCursor);
    }
}
