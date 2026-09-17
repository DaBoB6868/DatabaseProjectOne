/**
 * Copyright (c) 2025 Sami Menik, PhD. All rights reserved.
 * 
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * This software is provided "as is," without warranty of any kind.
 */
package uga.csx370.mydb;

import java.util.List;

/**
 * Interface for the relational algebra operators.
 * 
 * Note: The implementing classes should not modify the relations
 * that are passed as parameters to the methods. Instead new relations
 * should be initialized and returned as the result.
 */
public interface RA {

    /**
     * Performs the select operation on the relation rel
     * by applying the predicate p.
     * 
     * @return The resulting relation after applying the select operation.
     */
    public Relation select(Relation rel, Predicate p);

    /**
     * Performs the project operation on the relation rel
     * given the attributes list attrs.
     * 
     * @return The resulting relation after applying the project operation.
     * 
     * @throws IllegalArgumentException If attributes in attrs are not
     *                                  present in rel.
     */
    public Relation project(Relation rel, List<String> attrs);

    /**
     * Performs the union operation on the relations rel1 and rel2.
     * The resulting relation should not have any duplicate rows.
     * 
     * @return The resulting relation after applying the union operation.
     * 
     * @throws IllegalArgumentException If rel1 and rel2 are not compatible.
     */
    public Relation union(Relation rel1, Relation rel2);

    /**
     * Performs the set intersection operation on the relations rel1 and rel2.
     * The resulting relation should not have any duplicate rows.
     * 
     * @return The resulting relation after applying the set intersection operation.
     * 
     * @throws IllegalArgumentException If rel1 and rel2 are not compatible.
     */
    public Relation intersect(Relation rel1, Relation rel2);

    /**
     * Performs the set difference operation on the relations rel1 and rel2.
     * The resulting relation should not have any duplicate rows.
     * 
     * @return The resulting relation after applying the set difference operation.
     * 
     * @throws IllegalArgumentException If rel1 and rel2 are not compatible.
     */
    public Relation diff(Relation rel1, Relation rel2);

    /**
     * Renames the attributes in origAttr of relation rel to corresponding
     * names in renamedAttr.
     * 
     * @return The resulting relation after renaming the attributes.
     * 
     * @throws IllegalArgumentException If attributes in origAttr are not present in
     *                                  rel or origAttr and renamedAttr do not have
     *                                  matching argument counts.
     */
    public Relation rename(Relation rel, List<String> origAttr, List<String> renamedAttr);

    /**
     * Performs cartesian product on relations rel1 and rel2.
     * 
     * @return The resulting relation after applying cartesian product. The
     *         resulting relation should have rel1 attributes before rel2
     *         attributes. In the output relation, rel1 attributes should appear in
     *         the order they appear in rel1. Same applies to rel2.
     * 
     * @throws IllegalArgumentException if rel1 and rel2 have common attributes.
     */
    public Relation cartesianProduct(Relation rel1, Relation rel2);

    /**
     * Performs natural join on relations rel1 and rel2.
     * 
     * @return The resulting relation after applying natural join. The resulting
     *         relation should have rel1 attributes before rel2 attributes. The
     *         resulting relation should have rel1 attributes should appear in the
     *         order they appear in rel1. Same applies to rel2. Note that this
     *         operation merges corresponding common attributes from the two tables
     *         in the resulting table.
     */
    public Relation join(Relation rel1, Relation rel2);

    /**
     * Performs theta join on relations rel1 and rel2 with predicate p.
     * 
     * @return The resulting relation after applying theta join. The resulting
     *         relation should have the attributes of both rel1 and rel2. The
     *         attributes of rel1 should appear in the the order they appear in rel1
     *         but before the attributes of rel2. Attributes of rel2 as well should
     *         appear in the order they appear in rel2.
     * 
     * @throws IllegalArgumentException if rel1 and rel2 have common attributes.
     */
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
    
    Relation result = new RelationImpl(resultTypes, resultAttrs);
    
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
