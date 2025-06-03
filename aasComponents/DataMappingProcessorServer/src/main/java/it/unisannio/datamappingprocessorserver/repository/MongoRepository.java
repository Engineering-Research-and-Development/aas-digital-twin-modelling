package it.unisannio.datamappingprocessorserver.repository;


import lombok.RequiredArgsConstructor;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
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

    public void updateValueByIdShort(String submodelId, String idShortPath, String value) {
        StringTokenizer tokenizer = new StringTokenizer(idShortPath, ".");
        StringBuilder filterBuilder = new StringBuilder();
        Update update = new Update();
        filterBuilder.append("submodelElements");
        while(tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken();
            filterBuilder.append(".$[")
                    .append(token)
                    .append("].")
                    .append("value");
            update = update.filterArray(token+".idShort",token );
        }
        update.set(filterBuilder.toString(),value);

        Query query = new Query(Criteria.where("_id").is(submodelId));

        mongoTemplate.updateFirst(query, update, this.COLLECTION_NAME);

    }
}
