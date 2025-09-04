package dias.heimy.dto.response;

import java.util.List;

public record PageResponse<T>(int pageNumber, int pageSize, long elements, boolean hasNext, List<T> content) {

    public static <T> PageResponse<T> of(int pageNumber, int pageSize, long totalElements, List<T> content) {

        boolean hasNext = (long) (pageNumber + 1) * pageSize < totalElements;

        return new PageResponse<>(pageNumber, pageSize, totalElements, hasNext, content);
    }

    public static <T> PageResponse<T> empty(int pageNumber, int pageSize) {
        return new PageResponse<>(pageNumber, pageSize, 0L, false, List.of());
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    public int size() {
        return content.size();
    }

    public boolean isFirst() {
        return pageNumber == 0;
    }

    public boolean isLast() {
        return !hasNext;
    }

    public int getTotalPages() {
        return elements == 0 ? 0 : (int) Math.ceil((double) elements / pageSize);
    }
}
