package uga.csx370.mydbimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uga.csx370.mydb.Predicate;
import uga.csx370.mydb.RA;
import uga.csx370.mydb.Relation;
import uga.csx370.mydb.RelationBuilder;
import uga.csx370.mydb.Cell;
import uga.csx370.mydb.Type;

public class RAImpl implements RA {

    @Override
    public Relation select(Relation rel, Predicate p) {
        Relation selectedRel = new RelationBuilder()
                .attributeNames(rel.getAttrs()).attributeTypes(rel.getTypes()).build();
        for (int i = 0; i < rel.getSize(); i++) {
            List<Cell> row = rel.getRow(i);
            if (p.check(row)) {
                selectedRel.insert(row);
            }
        }
        return selectedRel;
    }

    @Override
    public Relation project(Relation rel, List<String> attrs) {
        List<String> inputAttrs = rel.getAttrs();
        List<Type> inputTypes = rel.getTypes();
        List<Integer> indices = new ArrayList<>();
        List<Type> projectedTypes = new ArrayList<>();
        for (String attr : attrs) {
            int index = inputAttrs.indexOf(attr);
            if (index == -1) {
                throw new IllegalArgumentException("Attribute does not exist in the relation.");
            }
            indices.add(index);
            projectedTypes.add(inputTypes.get(index));
        }
        Relation projectedRel = new RelationBuilder()
                .attributeNames(new ArrayList<>(attrs)).attributeTypes(projectedTypes).build();
        Set<List<Cell>> seenRows = new HashSet<>();
        for (int i = 0; i < rel.getSize(); i++) {
            List<Cell> row = rel.getRow(i); 
            List<Cell> projectedRow = new ArrayList<>();
            for (int index : indices) {
                projectedRow.add(row.get(index));
            }
            if (seenRows.add(projectedRow)) {
                projectedRel.insert(projectedRow);
            }
        }       
        return projectedRel;
    }

    @Override
    public Relation union(Relation rel1, Relation rel2) {
        if (!rel1.getTypes().equals(rel2.getTypes())) {
            throw new IllegalArgumentException("Relations are not compatible.");
        }
        Relation result = new RelationBuilder()
                .attributeNames(rel1.getAttrs()).attributeTypes(rel1.getTypes()).build();
        Set<List<Cell>> seen = new HashSet<>();
        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row = rel1.getRow(i);
            if (seen.add(row)) {
                result.insert(row);
            }
        }
        for (int i = 0; i < rel2.getSize(); i++) {
            List<Cell> row = rel2.getRow(i);
            if (seen.add(row)) {
                result.insert(row);
            }
        }
        return result;
    }

    @Override
    public Relation intersect(Relation rel1, Relation rel2) {
        if (!rel1.getTypes().equals(rel2.getTypes())) {
            throw new IllegalArgumentException("Relations are not compatible.");
        }
        Relation result = new RelationBuilder()
                .attributeNames(rel1.getAttrs()).attributeTypes(rel1.getTypes()).build();
        Set<List<Cell>> rel2Rows = new HashSet<>();
        for (int i = 0; i < rel2.getSize(); i++) {
            rel2Rows.add(rel2.getRow(i));
        }
        Set<List<Cell>> seen = new HashSet<>();
        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row = rel1.getRow(i);
            if (rel2Rows.contains(row) && seen.add(row)) {
                result.insert(row);
            }
        }
        return result;
    }

    @Override
    public Relation diff(Relation rel1, Relation rel2) {
        if (!rel1.getTypes().equals(rel2.getTypes())) {
            throw new IllegalArgumentException("Relations are not compatible.");
        }
        Relation result = new RelationBuilder()
                .attributeNames(rel1.getAttrs()).attributeTypes(rel1.getTypes()).build();
        Set<List<Cell>> rel2Rows = new HashSet<>();
        for (int i = 0; i < rel2.getSize(); i++) {
            rel2Rows.add(rel2.getRow(i));
        }
        Set<List<Cell>> seen = new HashSet<>();
        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row = rel1.getRow(i);
            if (!rel2Rows.contains(row) && seen.add(row)) {
                result.insert(row);
            }
        }
        return result;
    }

    @Override
    public Relation rename(Relation rel, List<String> origAttr, List<String> renamedAttr) {
        if (origAttr.size() != renamedAttr.size()) {
            throw new IllegalArgumentException("origAttr and renamedAttr do not have matching argument counts.");
        }
        List<String> inputAttrs = rel.getAttrs();
        List<String> newAttrs = new ArrayList<>(inputAttrs);
        for (int i = 0; i < origAttr.size(); i++) {
            int index = inputAttrs.indexOf(origAttr.get(i));
            if (index == -1) {
                throw new IllegalArgumentException("Attribute does not exist in the relation: " + origAttr.get(i));
            }
            newAttrs.set(index, renamedAttr.get(i));
        }
        Relation result = new RelationBuilder()
                .attributeNames(newAttrs).attributeTypes(rel.getTypes()).build();
        for (int i = 0; i < rel.getSize(); i++) {
            result.insert(rel.getRow(i));
        }
        return result;
    }

    @Override
    public Relation cartesianProduct(Relation rel1, Relation rel2) {
        for (String attr : rel1.getAttrs()) {
            if (rel2.hasAttr(attr)) {
                throw new IllegalArgumentException("rel1 and rel2 have common attributes: " + attr);
            }
        }
        List<String> resultAttrs = new ArrayList<>();
        List<Type> resultTypes = new ArrayList<>();

        resultAttrs.addAll(rel1.getAttrs());
        resultAttrs.addAll(rel2.getAttrs());
        resultTypes.addAll(rel1.getTypes());
        resultTypes.addAll(rel2.getTypes());

        Relation result = new RelationBuilder()
                .attributeNames(resultAttrs).attributeTypes(resultTypes).build();

        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row1 = rel1.getRow(i);
            for (int j = 0; j < rel2.getSize(); j++) {
                List<Cell> row2 = rel2.getRow(j);

                List<Cell> combinedRow = new ArrayList<>();
                combinedRow.addAll(row1);
                combinedRow.addAll(row2);

                result.insert(combinedRow);
            }
        }

        return result;
    }

    @Override
    public Relation join(Relation rel1, Relation rel2) {
        List<String> rel1Attrs = rel1.getAttrs();
        List<String> rel2Attrs = rel2.getAttrs();

        List<String> commonAttrs = new ArrayList<>();
        for (String attr : rel1Attrs) {
            if (rel2.hasAttr(attr)) {
                commonAttrs.add(attr);
            }
        }

        List<Integer> rel1CommonIndices = new ArrayList<>();
        List<Integer> rel2CommonIndices = new ArrayList<>();
        for (String attr : commonAttrs) {
            rel1CommonIndices.add(rel1.getAttrIndex(attr));
            rel2CommonIndices.add(rel2.getAttrIndex(attr));
        }

        List<Integer> rel2UniqueIndices = new ArrayList<>();
        for (int i = 0; i < rel2Attrs.size(); i++) {
            if (!commonAttrs.contains(rel2Attrs.get(i))) {
                rel2UniqueIndices.add(i);
            }
        }

        List<String> resultAttrs = new ArrayList<>(rel1Attrs);
        List<Type> resultTypes = new ArrayList<>(rel1.getTypes());
        for (int index : rel2UniqueIndices) {
            resultAttrs.add(rel2Attrs.get(index));
            resultTypes.add(rel2.getTypes().get(index));
        }

        Relation result = new RelationBuilder()
                .attributeNames(resultAttrs).attributeTypes(resultTypes).build();

        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row1 = rel1.getRow(i);
            for (int j = 0; j < rel2.getSize(); j++) {
                List<Cell> row2 = rel2.getRow(j);

                boolean matches = true;
                for (int k = 0; k < commonAttrs.size(); k++) {
                    Cell v1 = row1.get(rel1CommonIndices.get(k));
                    Cell v2 = row2.get(rel2CommonIndices.get(k));
                    if (!v1.equals(v2)) {
                        matches = false;
                        break;
                    }
                }

                if (matches) {
                    List<Cell> combinedRow = new ArrayList<>(row1);
                    for (int index : rel2UniqueIndices) {
                        combinedRow.add(row2.get(index));
                    }
                    result.insert(combinedRow);
                }
            }
        }

        return result;
    }

    @Override
    public Relation join(Relation rel1, Relation rel2, Predicate p) {
        for (String attr : rel1.getAttrs()) {
            if (rel2.hasAttr(attr)) {
                throw new IllegalArgumentException("rel1 and rel2 have common attributes: " + attr);
            }
        }
    List<String> resultAttrs = new ArrayList<>();
    List<Type> resultTypes = new ArrayList<>();
    
    resultAttrs.addAll(rel1.getAttrs());
    resultAttrs.addAll(rel2.getAttrs());
    resultTypes.addAll(rel1.getTypes());
    resultTypes.addAll(rel2.getTypes());
    
    Relation result = new RelationBuilder()
            .attributeNames(resultAttrs).attributeTypes(resultTypes).build();
    
    for (int i = 0; i < rel1.getSize(); i++) {
        List<Cell> row1 = rel1.getRow(i);
        for (int j = 0; j < rel2.getSize(); j++) {
            List<Cell> row2 = rel2.getRow(j);
            
            List<Cell> combinedRow = new ArrayList<>();
            combinedRow.addAll(row1);
            combinedRow.addAll(row2);
            
            if (p.check(combinedRow)) {
                result.insert(combinedRow);
            }
        }
    }
    
    return result;
}

}
