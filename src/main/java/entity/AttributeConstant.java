package entity;
public class AttributeConstant {
    private String relationName;
    private String attributeName;
    public AttributeConstant(String relationName, String attributeName) {
        this.relationName = "relation." + relationName;
        this.attributeName = this.relationName + ".attribute." + attributeName;
    }

    public String getRelationName() {
        return this.relationName.split("[.]")[1];
    }
    public String getAttributeName() {
        return this.attributeName.split("[.]")[3];
    }

    public String getAttributeType() {
        return this.attributeName + ".type";
    }
    public String getAttributeNum() {
        return this.relationName + ".attributeNum";
    }

    public String getIsExitSite(String site) {
        return this.attributeName + ".site." + site + ".isExist";
    }

    public String getIsHorizon() {
        return this.attributeName + ".isHorizon";
    }
    public String getIsVertical() {
        return this.attributeName + ".isVertical";
    }

    public String getFragmentInfo(String site) {
        return this.attributeName + ".site." + site + ".fragment";
    }

    public String relationKey() {
        return this.relationName +".key";
    }
    /*
重写hashCode()方法，定义内容相同的哈希码相同，内容不同的哈希码不同
*/
    @Override
    public int hashCode() {
        return this.relationName.hashCode() +this.attributeName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj)
            return true;
        if (obj==null)
            return false;
        if (this.getClass()!=obj.getClass())
            return false;
        AttributeConstant attr = (AttributeConstant)obj;

        if (this.relationName.equals(attr.relationName) && this.attributeName.equals(attr.attributeName)) {
            return true;
        }
        return false;
    }

}