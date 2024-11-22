package org.example.controllers;
import org.example.models.Category;
import org.example.utilities.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/places/categories")
public class CategoryController {
    private final Storage<Category> storage;
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    public CategoryController(Storage<Category> storage) {
        this.storage = storage;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        logger.info("Method 'getAllCategories' is started");
        List<Category> categories = storage.getAll();
        logger.info("Method 'getAllCategories' is finished");
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        logger.info("Method 'getCategoryById' is started");
        Category category = storage.getById(id);
        if (category == null) {
            logger.warn("Method 'getCategoryById': not found element");
            return ResponseEntity.badRequest().build();
        }
        logger.info("Method 'getCategoryById' is finished");
        return ResponseEntity.ok(category);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Category> postCreateCategory(@PathVariable Long id, @RequestBody Category category) {
        logger.info("Method 'postCreateCategory' is started");
        Category elem = storage.save(id, category);
        logger.info("Method 'postCreateCategory' is finished");
        return ResponseEntity.ok(elem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> putUpdateCategory(@PathVariable Long id, @RequestBody Category category) {
        logger.info("Method 'putUpdateCategory' is started");
        boolean result = storage.update(id, category);
        if (!result) {
            logger.warn("Method 'putUpdateCategory': could not update element");
            return ResponseEntity.badRequest().build();
        }
        logger.info("Method 'putUpdateCategory' is finished");
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Category> deleteCategory(@PathVariable Long id) {
        logger.info("Method 'deleteCategory' is started");
        Category deletingElem = storage.getById(id);
        boolean isDelete = storage.delete(id);
        if (!isDelete) {
            logger.warn("Method 'deleteCategory': could not delete element");
            return ResponseEntity.badRequest().build();
        }
        logger.info("Method 'deleteCategory' is finished");
        return ResponseEntity.ok(deletingElem);
    }
}