package br.com.siecola.gae_exemplo1.controller;

import br.com.siecola.gae_exemplo1.model.Product;
import com.google.appengine.api.datastore.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/products")
public class ProductController {

    private static final String PRODUCT_KIND = "Products";

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        Key productKey = KeyFactory.createKey(PRODUCT_KIND, "productKey");
        Entity productEntity = new Entity(PRODUCT_KIND, productKey);
        this.productToEntity(product, productEntity);
        datastore.put(productEntity);
        product.setId(productEntity.getKey().getId());
        return new ResponseEntity<Product>(product, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = new ArrayList<>();
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        Query query = new Query(PRODUCT_KIND).addSort("Code",
                Query.SortDirection.ASCENDING);
        List<Entity> productsEntities = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults());
        for (Entity productEntity : productsEntities) {
            Product product = entityToProduct(productEntity);
            products.add(product);
        }
        return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
    }


    private void productToEntity(Product product, Entity productEntity) {
        productEntity.setProperty("ProductID", product.getProductID());
        productEntity.setProperty("Name", product.getName());
        productEntity.setProperty("Code", product.getCode());
        productEntity.setProperty("Model", product.getModel());
        productEntity.setProperty("Price", product.getPrice());
    }

    private Product entityToProduct(Entity productEntity) {
        Product product = new Product();
        product.setId(productEntity.getKey().getId());
        product.setProductID((String) productEntity.getProperty("ProductID"));
        product.setName((String) productEntity.getProperty("Name"));
        product.setCode(Integer.parseInt(productEntity.getProperty("Code").toString()));
        product.setModel((String) productEntity.getProperty("Model"));
        product.setPrice(Float.parseFloat(productEntity.getProperty("Price").toString()));
        return product;
    }
}
