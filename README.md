# Document Manager

---

In this task my implementation based on instructions:
> For implement this task focus on clear code, and make this solution as simple readable as possible
>
> Don't worry about performance, concurrency, etc
>
> You can use in Memory collection for sore data

So, in some cases, the implementation could have been done differently. For example, in the search method, more flexible
filtering approaches could be created.

```java
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
```
It was not specified what to do if the ```request``` object is ```null```, so I decided to simply return the entire list. However, this could also be adjusted by adding an overloaded method that takes no parameters.
```java

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

public List<Document> search(){ // Also can be named as findAll
    return search(null);
}
```


I would also separate the code into individual files and isolate the data persistence layer and business logic (Service) into separate components. If needed, a presentation layer and user interaction could also be added.

---

The file with the implemented solution can be found in the directory ```src\main\java\ua\lavms```.
Additionally, the tests for this class are written in the directory ```src\test\java\ua\lavms\```.
