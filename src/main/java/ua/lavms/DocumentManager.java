package ua.lavms;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

public class DocumentManager {

    private final Map<String, Document> documentStore = new HashMap<>();

    public Document save(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        if (document.getId() == null || document.getId().isBlank()) {
            document.setId(UUID.randomUUID().toString());
        }
        documentStore.put(document.getId(), document);

        return document;
    }

    public List<Document> search(SearchRequest request) {
        if (request == null) {
            return new ArrayList<>(documentStore.values());
        }

        return documentStore
                .values()
                .stream()
                .filter(doc -> filterByTitlePrefixes(doc, request.getTitlePrefixes()))
                .filter(doc -> filterByContents(doc, request.getContainsContents()))
                .filter(doc -> filterByAuthorIds(doc, request.getAuthorIds()))
                .filter(doc -> filterByCreationDate(doc, request.getCreatedFrom(), request.getCreatedTo()))
                .toList();
    }

    public List<Document> search(){
        return search(null);
    }


    public Optional<Document> findById(String id) {
        if (id == null || id.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(documentStore.get(id));
    }


    private boolean filterByTitlePrefixes(Document document, List<String> prefixes) {
        if (prefixes == null || prefixes.isEmpty()) {
            return true;
        }
        return prefixes.stream().anyMatch(prefix -> document.getTitle().startsWith(prefix));
    }

    private boolean filterByContents(Document document, List<String> contents) {
        if (contents == null || contents.isEmpty()) {
            return true;
        }
        return contents.stream().anyMatch(content -> document.getContent().contains(content));
    }

    private boolean filterByAuthorIds(Document document, List<String> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return true;
        }
        return authorIds.contains(document.getAuthor().getId());
    }

    private boolean filterByCreationDate(Document document, Instant from, Instant to) {
        if (from == null && to == null) {
            return true;
        }
        if (from != null && document.getCreated().isBefore(from)) {
            return false;
        }
        return to == null || !document.getCreated().isAfter(to);
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}
