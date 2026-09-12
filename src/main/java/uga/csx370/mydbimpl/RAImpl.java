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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'union'");
    }

    @Override
    public Relation intersect(Relation rel1, Relation rel2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'intersect'");
    }

    @Override
    public Relation diff(Relation rel1, Relation rel2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'diff'");
    }

    @Override
    public Relation rename(Relation rel, List<String> origAttr, List<String> renamedAttr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rename'");
    }

    @Override
    public Relation cartesianProduct(Relation rel1, Relation rel2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cartesianProduct'");
    }

    @Override
    public Relation join(Relation rel1, Relation rel2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'join'");
    }

    @Override
    public Relation join(Relation rel1, Relation rel2, Predicate p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'join'");
    }

}
