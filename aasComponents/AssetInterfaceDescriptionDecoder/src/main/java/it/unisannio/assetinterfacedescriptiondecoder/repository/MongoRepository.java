package it.unisannio.assetinterfacedescriptiondecoder.repository;


import lombok.RequiredArgsConstructor;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

@Component
@RequiredArgsConstructor
public class MongoRepository {
    private final MongoTemplate mongoTemplate;
    private final String COLLECTION_NAME="submodel-repo";

    public List<Submodel> findAllBySemanticId(String semanticId){
        Query query = new Query(Criteria.where("semanticId.keys.value").is(semanticId));
        return mongoTemplate.find(query, Submodel.class,COLLECTION_NAME);
    }

    public Optional<Submodel> findBySubmodelId(String submodelId){
        Query query = new Query(Criteria.where("_id").is(submodelId));
        return Optional.ofNullable(mongoTemplate.findOne(query, Submodel.class, COLLECTION_NAME));
    }

}
