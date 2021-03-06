package br.com.alura.aluraflix.services;

import br.com.alura.aluraflix.models.Category;
import br.com.alura.aluraflix.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements CategoryRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Page<Category> findCategories(Pageable pageable, String username) {
        try {
            Query query = getQueryWithUserCriteria(username).with(pageable);
            List<Category> categoryPage = mongoTemplate.find(query, Category.class);
            long count = mongoTemplate.count(query.skip(-1).limit(-1), Category.class);
            return new PageImpl<>(categoryPage, pageable, count);
        } catch (Exception e) {
            return Page.empty();
        }
    }

    @Override
    public Optional<Category> findCategoryById(Integer id, String username) {
        try {
            return Optional.ofNullable(mongoTemplate.findOne(getQueryById(id, username), Category.class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Boolean insertOrUpdateCategory(final Category category) {
        try {
            mongoTemplate.save(category);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean deleteCategory(Integer id, String username) {
        try {
            mongoTemplate.remove(getQueryById(id, username), Category.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean existsById(Integer id, String username) {
        try {
            return mongoTemplate.exists(getQueryById(id, username), Category.class);
        } catch (Exception e) {
            return false;
        }
    }

    public Query getQueryById(Integer id, String username) {
        Query query = getQueryWithUserCriteria(username);
        return query.addCriteria(Criteria.where("id").is(id));
    }

    public Query getQueryWithUserCriteria(String username) {
        return Query.query(Criteria.where("user").is(username));
    }
}
