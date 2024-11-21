package ua.lavms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    @DisplayName("Illegal Arguments")
    void save_shouldThrowException_whenDocumentIsNull() {
        assertThrows(IllegalArgumentException.class, () -> documentManager.save(null));
    }

    @Test
    @DisplayName("Generate id for document without id")
    void save_shouldGenerateId_whenIdIsNull() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Sample Title")
                .content("Sample Content")
                .author(new DocumentManager.Author(UUID.randomUUID().toString(), "Author Name"))
                .created(Instant.now())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId());
        assertEquals(document.getTitle(), savedDocument.getTitle());
    }

    @Test
    @DisplayName("Update document by existing id")
    void save_shouldUpdateExistingDocument_whenIdExists() {
        String documentId = UUID.randomUUID().toString();
        DocumentManager.Document document = DocumentManager.Document.builder()
                .id(documentId)
                .title("Old Title")
                .content("Old Content")
                .author(new DocumentManager.Author(UUID.randomUUID().toString(), "Author Name"))
                .created(Instant.now())
                .build();

        documentManager.save(document);

        DocumentManager.Document updatedDocument = DocumentManager.Document.builder()
                .id(documentId)
                .title("New Title")
                .content("New Content")
                .author(document.getAuthor())
                .created(document.getCreated())
                .build();

        DocumentManager.Document result = documentManager.save(updatedDocument);

        assertEquals("New Title", result.getTitle());
        assertEquals("New Content", result.getContent());
    }

    @Test
    @DisplayName("Find by existing id")
    void findById_shouldReturnDocument_whenIdExists() {
        String documentId = UUID.randomUUID().toString();
        DocumentManager.Document document = DocumentManager.Document.builder()
                .id(documentId)
                .title("Sample Title")
                .content("Sample Content")
                .author(new DocumentManager.Author(UUID.randomUUID().toString(), "Author Name"))
                .created(Instant.now())
                .build();

        documentManager.save(document);

        Optional<DocumentManager.Document> result = documentManager.findById(documentId);

        assertTrue(result.isPresent());
        assertEquals(documentId, result.get().getId());
    }

    @Test
    @DisplayName("Find by non existing id")
    void findById_shouldReturnEmptyOptional_whenIdDoesNotExist() {
        Optional<DocumentManager.Document> result = documentManager.findById(UUID.randomUUID().toString());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Search all documents (request is null)")
    void search_shouldReturnAllDocuments_whenRequestIsNull() {
        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .id(UUID.randomUUID().toString())
                .title("Title1")
                .content("Content1")
                .author(new DocumentManager.Author(UUID.randomUUID().toString(), "Author1"))
                .created(Instant.now())
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .id(UUID.randomUUID().toString())
                .title("Title2")
                .content("Content2")
                .author(new DocumentManager.Author(UUID.randomUUID().toString(), "Author2"))
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        List<DocumentManager.Document> result = documentManager.search(null);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Search by title prefix")
    void search_shouldFilterByTitlePrefix() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .id(UUID.randomUUID().toString())
                .title("Unique Title")
                .content("Sample Content")
                .author(new DocumentManager.Author(UUID.randomUUID().toString(), "Author Name"))
                .created(Instant.now())
                .build();

        documentManager.save(document);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Unique"))
                .build();

        List<DocumentManager.Document> result = documentManager.search(request);

        assertEquals(1, result.size());
        assertEquals("Unique Title", result.get(0).getTitle());
    }

    @Test
    @DisplayName("Search by creation date")
    void search_shouldFilterByCreationDate() {
        Instant now = Instant.now();

        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .id(UUID.randomUUID().toString())
                .title("Title1")
                .content("Content1")
                .author(new DocumentManager.Author(UUID.randomUUID().toString(), "Author1"))
                .created(now.minusSeconds(3600))
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .id(UUID.randomUUID().toString())
                .title("Title2")
                .content("Content2")
                .author(new DocumentManager.Author(UUID.randomUUID().toString(), "Author2"))
                .created(now)
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .createdFrom(now.minusSeconds(1800))
                .build();

        List<DocumentManager.Document> result = documentManager.search(request);

        assertEquals(1, result.size());
        assertEquals("Title2", result.getFirst().getTitle());
    }
}
