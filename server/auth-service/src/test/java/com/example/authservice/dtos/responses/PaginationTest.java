package com.example.authservice.dtos.responses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaginationTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        Pagination pagination = new Pagination();
        int currentPage = 1;
        int totalPages = 10;
        long totalItems = 100L;
        int pageSize = 10;

        // Act
        pagination.setCurrentPage(currentPage);
        pagination.setTotalPages(totalPages);
        pagination.setTotalItems(totalItems);
        pagination.setPageSize(pageSize);

        // Assert
        assertEquals(currentPage, pagination.getCurrentPage());
        assertEquals(totalPages, pagination.getTotalPages());
        assertEquals(totalItems, pagination.getTotalItems());
        assertEquals(pageSize, pagination.getPageSize());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        Pagination pagination = new Pagination();

        // Assert
        assertNotNull(pagination);
        assertEquals(0, pagination.getCurrentPage());
        assertEquals(0, pagination.getTotalPages());
        assertEquals(0L, pagination.getTotalItems());
    }

    @Test
    void testAllArgsConstructor() {
        // Act
        // constructor order: (totalItems, totalPages, currentPage, pageSize)
        Pagination pagination = new Pagination(100L, 10, 1, 10);

        // Assert
        assertNotNull(pagination);
        assertEquals(1, pagination.getCurrentPage());
        assertEquals(10, pagination.getTotalPages());
        assertEquals(100L, pagination.getTotalItems());
        assertEquals(10, pagination.getPageSize());
    }

    @Test
    void testBuilder() {
        // Act
        Pagination pagination = Pagination.builder()
                .currentPage(1)
                .totalPages(10)
                .totalItems(100L)
                .pageSize(10)
                .build();

        // Assert
        assertNotNull(pagination);
        assertEquals(1, pagination.getCurrentPage());
        assertEquals(10, pagination.getTotalPages());
        assertEquals(100L, pagination.getTotalItems());
        assertEquals(10, pagination.getPageSize());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Pagination pagination1 = Pagination.builder()
                .currentPage(1)
                .totalPages(10)
                .totalItems(100L)
                .pageSize(10)
                .build();

        Pagination pagination2 = Pagination.builder()
                .currentPage(1)
                .totalPages(10)
                .totalItems(100L)
                .pageSize(10)
                .build();

        Pagination pagination3 = Pagination.builder()
            .currentPage(2)
            .totalPages(20)
            .totalItems(200L)
            .pageSize(10)
            .build();

        // Assert
        assertEquals(pagination1, pagination2);
        assertNotEquals(pagination1, pagination3);
        assertEquals(pagination1.hashCode(), pagination2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        Pagination pagination = Pagination.builder()
                .currentPage(1)
                .totalPages(10)
                .totalItems(100L)
                .pageSize(10)
                .build();

        // Act
        String toString = pagination.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("10"));
        assertTrue(toString.contains("100"));
    }

    @Test
    void testCalculatedFields() {
        // Arrange & Act
        Pagination pagination = Pagination.builder()
                .currentPage(5)
                .totalPages(10)
                .totalItems(100L)
                .pageSize(10)
                .build();

        // Assert - verify middle page
        assertEquals(5, pagination.getCurrentPage());
        assertTrue(pagination.getCurrentPage() > 0);
        assertTrue(pagination.getCurrentPage() <= pagination.getTotalPages());
    }

    @Test
    void testFirstPage() {
        // Arrange & Act
        Pagination pagination = Pagination.builder()
                .currentPage(0)
                .totalPages(10)
                .totalItems(100L)
                .pageSize(10)
                .build();

        // Assert
        assertEquals(0, pagination.getCurrentPage());
    }

    @Test
    void testLastPage() {
        // Arrange & Act
        Pagination pagination = Pagination.builder()
                .currentPage(9)
                .totalPages(10)
                .totalItems(100L)
                .pageSize(10)
                .build();

        // Assert
        assertEquals(9, pagination.getCurrentPage());
        assertEquals(10, pagination.getTotalPages());
    }
}
