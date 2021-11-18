package entity;
public class RelationConstant {
    private String relationName;
    public RelationConstant(String relationName) {
        this.relationName = "relation." + relationName;
    }
    public String getRelations() {
        return "relations";
    }

    public String getAttributes() {
        return this.relationName + ".attributes";
    }

    public String getRelationName() {
        return relationName;
    }

    public String getAttributeNum() {
        return relationName + ".attributeNum";
    }

    public String getAttributeName(String attrName) {
        return relationName + ".attribute." + attrName;
    }


}