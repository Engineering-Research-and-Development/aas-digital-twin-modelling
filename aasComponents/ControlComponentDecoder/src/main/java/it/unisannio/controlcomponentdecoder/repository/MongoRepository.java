package it.unisannio.controlcomponentdecoder.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mongodb.client.model.UpdateOptions;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class MongoRepository {
    private final MongoTemplate mongoTemplate;
    private final String SUBMODEL_COLLECTION_NAME ="submodel-repo";
    private final String AAS_COLLECTION_NAME ="aas-repo";
    


    public Optional<AssetAdministrationShell> findAASBySubmodelID(String submodelId){
        Query query = new Query(Criteria.where("submodels.keys.value").is(submodelId));
        return Optional.ofNullable(mongoTemplate.findOne(query, AssetAdministrationShell.class, AAS_COLLECTION_NAME));
    }



    public List<AssetAdministrationShell> findAASBySubmodelID(List<String> submodelsId){
        Query query = new Query(Criteria.where("submodels.keys.value").in(submodelsId));
        return mongoTemplate.find(query, AssetAdministrationShell.class, AAS_COLLECTION_NAME);
    }

    public List<AssetAdministrationShell> findAll(){
        return mongoTemplate.findAll(AssetAdministrationShell.class, AAS_COLLECTION_NAME);
    }



    public List<Submodel> findAllBySemanticId(String semanticId){
        Query query = new Query(Criteria.where("semanticId.keys.value").is(semanticId));
        return mongoTemplate.find(query, Submodel.class, SUBMODEL_COLLECTION_NAME);
    }

    public Optional<Submodel> findBySubmodelId(String submodelId){
        Query query = new Query(Criteria.where("_id").is(submodelId));
        return Optional.ofNullable(mongoTemplate.findOne(query, Submodel.class, SUBMODEL_COLLECTION_NAME));
    }

    public void updateValueByIdShort(String submodelId, String idShortPath, String value) {
        StringTokenizer tokenizer = new StringTokenizer(idShortPath, ".");
        StringBuilder builder = new StringBuilder();
        String propertyId = null;
        while(tokenizer.hasMoreTokens()){
            if(tokenizer.countTokens()==1){
                builder.append("idShort");
                propertyId = tokenizer.nextToken();
            }else{
                builder.append("id.");
                tokenizer.nextToken();
            }
        }
        String propertyPath = builder.toString();

        Document filter = new Document("_id", submodelId);

        Document update = new Document("$set", new Document("submodelElements.$[elem].value", value));

        Document arrayFilter = new Document("elem." + propertyPath, propertyId);
        UpdateOptions options = new UpdateOptions().arrayFilters(Collections.singletonList(arrayFilter));

        mongoTemplate.getCollection(SUBMODEL_COLLECTION_NAME).updateOne(filter, update, options);
    }

    public void addChildEntityToBomHierarchy(Submodel bomSubmodel, DefaultEntity rootEntity, DefaultEntity entity) {


        Document filter = new Document("_id", bomSubmodel.getId());

        Document update = new Document("$push", new Document("submodelElements.$[elem].statements", this.getEntityDoc(entity)));

        // Combina più condizioni in un solo array filter
        Document arrayFilter = new Document("elem.semanticId.keys.value", rootEntity.getSemanticId().getKeys().get(0).getValue())
                .append("elem.globalAssetId", rootEntity.getGlobalAssetId());


        UpdateOptions options = new UpdateOptions().arrayFilters(List.of(arrayFilter));

        mongoTemplate.getCollection(SUBMODEL_COLLECTION_NAME).updateOne(filter, update, options);
    }

    private Document getEntityDoc(DefaultEntity entity) {
        Document doc = new Document();

        doc.put("entityType", entity.getEntityType().name());
        doc.put("globalAssetId", entity.getGlobalAssetId());
        doc.put("specificAssetIds", entity.getSpecificAssetIds());
        doc.put("statements", entity.getStatements());
        doc.put("embeddedDataSpecifications", entity.getEmbeddedDataSpecifications());
        doc.put("extensions", entity.getExtensions());
        doc.put("qualifiers", entity.getQualifiers());
        doc.put("description", entity.getDescription());
        doc.put("displayName", entity.getDisplayName());
        doc.put("idShort", entity.getIdShort());
        doc.put("_class", DefaultEntity.class.getName());

        // SemanticId
        if (entity.getSemanticId() != null) {
            Document semanticIdDoc = new Document();
            List<Document> keysDocs = new ArrayList<>();
            for (Key key : entity.getSemanticId().getKeys()) {
                Document keyDoc = new Document();
                keyDoc.put("type", key.getType().toString());
                keyDoc.put("value", key.getValue());
                keyDoc.put("_class", DefaultKey.class.getName());
                keysDocs.add(keyDoc);
            }
            semanticIdDoc.put("keys", keysDocs);
            semanticIdDoc.put("type", entity.getSemanticId().getType().toString());
            semanticIdDoc.put("_class", DefaultReference.class.getName());

            doc.put("semanticId", semanticIdDoc);
        }

        if (entity.getSupplementalSemanticIds() != null) {
            List<Document>supplSemanticsIdDocs = new ArrayList<>();
            for(Reference supplementalSemanticId : entity.getSupplementalSemanticIds()) {
                Document semanticIdDoc = new Document();
                List<Document> keysDocs = new ArrayList<>();
                for (Key key : supplementalSemanticId.getKeys()) {
                    Document keyDoc = new Document();
                    keyDoc.put("type", key.getType().toString());
                    keyDoc.put("value", key.getValue());
                    keyDoc.put("_class", DefaultKey.class.getName());
                    keysDocs.add(keyDoc);
                }
                semanticIdDoc.put("keys", keysDocs);
                semanticIdDoc.put("type", entity.getSemanticId().getType().toString());
                semanticIdDoc.put("_class", DefaultReference.class.getName());
                supplSemanticsIdDocs.add(semanticIdDoc);
            }
            doc.put("supplementalSemanticIds", supplSemanticsIdDocs);


        }

        return doc;
    }

    public List<SubmodelElementCollection> getRequiredCapabilities(AssetAdministrationShell aas, String capabilitySemanticId) {
        List<String> aasSubmodelsId = aas.getSubmodels().stream()
                .map(s -> s.getKeys().get(0).getValue())
                .toList();

        Optional<Submodel> optionalSubmodel = this.findAllBySemanticIdAndIdIn(capabilitySemanticId, aasSubmodelsId);

        if (optionalSubmodel.isEmpty()) {
            return List.of();
        }

        Submodel submodel = optionalSubmodel.get();

        return submodel.getSubmodelElements().stream()
                .filter(e -> "CapabilitySet".equals(e.getIdShort()))
                .filter(SubmodelElementCollection.class::isInstance)
                .map(e -> (SubmodelElementCollection) e)
                .flatMap(capabilitySet -> capabilitySet.getValue().stream())
                .filter(e -> e.getSemanticId() != null &&
                        !e.getSemanticId().getKeys().isEmpty() &&
                        "https://admin-shell.io/idta/CapabilityDescription/CapabilityContainer/1/0"
                                .equals(e.getSemanticId().getKeys().get(0).getValue()))
                .filter(SubmodelElementCollection.class::isInstance)
                .map(e -> (SubmodelElementCollection) e)
                .filter(container -> container.getSupplementalSemanticIds().stream()
                        .flatMap(suppSem -> suppSem.getKeys().stream())
                        .anyMatch(semKey -> "http://www.w3id.org/hsu-aut/cask#RequiredCapability".equals(semKey.getValue())))
                .toList();
    }
    public List<SubmodelElementCollection> getProvidedCapabilities(AssetAdministrationShell aas, String capabilitySemanticId) {
        List<String> aasSubmodelsId = aas.getSubmodels().stream()
                .map(s -> s.getKeys().get(0).getValue())
                .toList();

        Optional<Submodel> optionalSubmodel = this.findAllBySemanticIdAndIdIn(capabilitySemanticId, aasSubmodelsId);

        if (optionalSubmodel.isEmpty()) {
            return List.of();
        }

        Submodel submodel = optionalSubmodel.get();

        return submodel.getSubmodelElements().stream()
                .filter(e -> "CapabilitySet".equals(e.getIdShort()))
                .filter(SubmodelElementCollection.class::isInstance)
                .map(e -> (SubmodelElementCollection) e)
                .flatMap(capabilitySet -> capabilitySet.getValue().stream())
                .filter(e -> e.getSemanticId() != null &&
                        !e.getSemanticId().getKeys().isEmpty() &&
                        "https://admin-shell.io/idta/CapabilityDescription/CapabilityContainer/1/0"
                                .equals(e.getSemanticId().getKeys().get(0).getValue()))
                .filter(SubmodelElementCollection.class::isInstance)
                .map(e -> (SubmodelElementCollection) e)
                .filter(container -> container.getSupplementalSemanticIds().stream()
                        .flatMap(suppSem -> suppSem.getKeys().stream())
                        .anyMatch(semKey -> "http://www.w3id.org/hsu-aut/cask#ProvidedCapability".equals(semKey.getValue())))
                .toList();
    }

    public List<DefaultCapability> getCapabilities(AssetAdministrationShell aas, String capabilitySemanticId, String supplementSemanticId) {
        List<String> aasSubmodelsId = aas.getSubmodels().stream().map(s->s.getKeys().get(0).getValue()).toList();

        MatchOperation matchSubmodel = Aggregation.match(
                Criteria.where("semanticId.keys.value").is(capabilitySemanticId)
                        .and("_id").in(aasSubmodelsId)
        );

        UnwindOperation unwindSubmodelElements = Aggregation.unwind("submodelElements");

        MatchOperation matchCapabilitySet = Aggregation.match(
                Criteria.where("submodelElements.idShort").is("CapabilitySet")
        );

        UnwindOperation unwindCapabilitySetValues = Aggregation.unwind("submodelElements.value");

        MatchOperation matchCapabilityContainer = Aggregation.match(
                Criteria.where("submodelElements.value.semanticId.keys.value")
                        .is("https://admin-shell.io/idta/CapabilityDescription/CapabilityContainer/1/0")
                        .and("submodelElements.value.supplementalSemanticIds.keys.value")
                        .is(supplementSemanticId)
        );

// ⚠️ Nuovo unwind sull'array interno "value"
        UnwindOperation unwindInnerValue = Aggregation.unwind("submodelElements.value.value");
        MatchOperation matchCapabilty = Aggregation.match(
                Criteria.where("submodelElements.value.value.semanticId.keys.value")
                        .is("https://admin-shell.io/idta/CapabilityDescription/Capability/1/0")
                        .and("submodelElements.value.supplementalSemanticIds.keys.value")
                        .is(supplementSemanticId)
        );

// 🎯 Proietta direttamente il singolo oggetto che ti serve
        ProjectionOperation projectFinal = Aggregation.project("submodelElements.value.value")
                .andExclude("_id");


        Aggregation aggregation = Aggregation.newAggregation(
                matchSubmodel,
                unwindSubmodelElements,
                matchCapabilitySet,
                unwindCapabilitySetValues,
                matchCapabilityContainer,
                unwindInnerValue,
                matchCapabilty,
                projectFinal
        );

        // Usa Document, non la classe finale
        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation,
                SUBMODEL_COLLECTION_NAME,
                Document.class
        );
        return results.getMappedResults()
                .stream()
                .map(d->((Document)d.get("value")))
                .map(d->(Document)d.get("value"))
                .map(d->d.toJson())
                .map(json-> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                SimpleModule module = new SimpleModule();
                module.addAbstractTypeMapping(Reference.class, DefaultReference.class);
                module.addAbstractTypeMapping(Key.class, DefaultKey.class);
                module.addAbstractTypeMapping(Qualifier.class, DefaultQualifier.class);
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                mapper.registerModule(module);
                return mapper.readValue(json,DefaultCapability.class);
            } catch (JsonProcessingException e) {
                return null;

            }
        }).toList();

    }

    private Optional<Submodel> findAllBySemanticIdAndIdIn(String semanticId, List<String> aasSubmodelsId) {
        Query query = new Query(Criteria.where("semanticId.keys.value").is(semanticId).and("_id").in(aasSubmodelsId));
        return Optional.ofNullable(mongoTemplate.findOne(query, Submodel.class, SUBMODEL_COLLECTION_NAME));
    }

    public Optional<Submodel> findSubmodelByAASIdAndSemanticId(AssetAdministrationShell aas, String semanticId) {
        List<String> aasSubmodelsId = aas.getSubmodels().stream().map(s -> s.getKeys().get(0).getValue()).toList();
        return this.findAllBySemanticIdAndIdIn(semanticId,aasSubmodelsId);
    }

    public void addInterfaceToAssetInterfaceDescription(Submodel interfaces, SubmodelElementCollection anInterface) {
        Document filter = new Document("_id", interfaces.getId());

        Document update = new Document("$set", new Document("submodelElements", anInterface));

        mongoTemplate.getCollection(SUBMODEL_COLLECTION_NAME).updateOne(filter, update);
    }


    public List<DefaultRelationshipElement> getCapabilitiesRealizedBy(String capabilitySemanticId) {

        MatchOperation matchSubmodel = Aggregation.match(
                Criteria.where("semanticId.keys.value").is(capabilitySemanticId)
        );

        UnwindOperation unwindSubmodelElements = Aggregation.unwind("submodelElements");

        MatchOperation matchCapabilitySet = Aggregation.match(
                Criteria.where("submodelElements.idShort").is("CapabilitySet")
        );

        UnwindOperation unwindCapabilitySetValues = Aggregation.unwind("submodelElements.value");

        MatchOperation matchCapabilityContainer = Aggregation.match(
                Criteria.where("submodelElements.value.semanticId.keys.value")
                        .is("https://admin-shell.io/idta/CapabilityDescription/CapabilityContainer/1/0")
        );

        UnwindOperation unwindInnerValue = Aggregation.unwind("submodelElements.value.value");
        MatchOperation matchCapabilty = Aggregation.match(
                Criteria.where("submodelElements.value.value.semanticId.keys.value")
                        .is("https://admin-shell.io/idta/CapabilityDescription/CapabilityRelations/1/0")
        );


        UnwindOperation unwindCapabilityRelationships = Aggregation.unwind("submodelElements.value.value.value");
        MatchOperation matchRealizedBy = Aggregation.match(
                Criteria.where("submodelElements.value.value.value.semanticId.keys.value")
                        .is("https://admin-shell.io/idta/CapabilityDescription/RealizedBy/1/0")
        );

        ProjectionOperation projectFinal = Aggregation.project("submodelElements.value.value.value")
                .andExclude("_id");


        Aggregation aggregation = Aggregation.newAggregation(
                matchSubmodel,
                unwindSubmodelElements,
                matchCapabilitySet,
                unwindCapabilitySetValues,
                matchCapabilityContainer,
                unwindInnerValue,
                matchCapabilty,
                unwindCapabilityRelationships,
                matchRealizedBy,
                projectFinal
        );

        // Usa Document, non la classe finale
        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation,
                SUBMODEL_COLLECTION_NAME,
                Document.class
        );
        return results.getMappedResults()
                .stream()
                .map(d->((Document)d.get("value")))
                .map(d->(Document)d.get("value"))
                .map(d->(Document)d.get("value"))
                .map(Document::toJson)
                .map(json-> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        SimpleModule module = new SimpleModule();
                        module.addAbstractTypeMapping(Reference.class, DefaultReference.class);
                        module.addAbstractTypeMapping(Key.class, DefaultKey.class);
                        module.addAbstractTypeMapping(Qualifier.class, DefaultQualifier.class);
                        module.addAbstractTypeMapping(LangStringTextType.class,DefaultLangStringTextType.class);
                        module.addAbstractTypeMapping(LangStringNameType.class,DefaultLangStringNameType.class);

                        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                        mapper.registerModule(module);
                        return mapper.readValue(json,DefaultRelationshipElement.class);
                    } catch (JsonProcessingException e) {
                        return null;

                    }
                }).toList();

    }
}
