package entity;
public class AttributeConstant {
    private String relationName;
    private String attributeName;
    public AttributeConstant(String relationName) {
        this.attributeName = "attribute." + relationName;
    }

    public String getRelationName() {
        return relationName;
    }

    public String getAttributeType() {
        return this.attributeName + "type";
    }
    public String getAttributeNum() {
        return relationName + ".attributeNum";
    }


}