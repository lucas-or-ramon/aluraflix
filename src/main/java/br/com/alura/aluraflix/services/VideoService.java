package br.com.alura.aluraflix.services;

import br.com.alura.aluraflix.models.Video;
import br.com.alura.aluraflix.repository.VideoRepository;
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
public class VideoService implements VideoRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Page<Video> findVideos(Pageable pageable, String search, String username) {
        try {
            if (search == null) {
                Query query = getQueryWithUserCriteria(username).with(pageable);
                return getPageVideo(query, pageable);
            }

            Query query = getQueryForSearch(search, pageable, username);
            return getPageVideo(query, pageable);
        } catch (Exception e) {
            return Page.empty();
        }
    }

    @Override
    public Page<Video> findFreeVideos(Pageable pageable, Integer categoryId) {
        try {
            Query query = new Query().addCriteria(Criteria.where("categoryId").is(categoryId));
            return getPageVideo(query, pageable);
        } catch (Exception e) {
            return Page.empty();
        }
    }

    @Override
    public Optional<Video> findVideoById(final Integer id, String username) {
        try {
            return Optional.ofNullable(mongoTemplate.findOne(getQueryById(id, username), Video.class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Boolean existsById(Integer id, String username) {
        try {
            return mongoTemplate.exists(getQueryById(id, username), Video.class);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean insertOrUpdateVideo(final Video video) {
        try {
            mongoTemplate.save(video);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean deleteVideo(final Integer id, String username) {
        try {
            mongoTemplate.remove(getQueryById(id, username), Video.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Page<Video> findVideosByCategory(Pageable pageable, Integer categoryId, String username) {
        try {
            Query query = getQueryWithUserCriteria(username);
            query.addCriteria(Criteria.where("categoryId").is(categoryId)).with(pageable);
            return getPageVideo(query, pageable);
        } catch (Exception e) {
            return Page.empty();
        }
    }

    public Query getQueryById(Integer id, String username) {
        Query query = getQueryWithUserCriteria(username);
        return query.addCriteria(Criteria.where("id").is(id));
    }

    public Page<Video> getPageVideo(Query query, Pageable pageable) {
        List<Video> videoList = mongoTemplate.find(query, Video.class);
        long count = mongoTemplate.count(query.skip(-1).limit(-1), Video.class);
        return new PageImpl<>(videoList, pageable, count);
    }

    public Query getQueryWithUserCriteria(String username) {
        return Query.query(Criteria.where("user").is(username));
    }

    public Query getQueryForSearch(String search, Pageable pageable, String username) {
        Query query = getQueryWithUserCriteria(username);

        Criteria criteria1 = Criteria.where("title").regex(search, "i");
        Criteria criteria2 = Criteria.where("description").regex(search, "i");

        query.addCriteria(new Criteria().orOperator(criteria1, criteria2)).with(pageable);
        return query;
    }
}
