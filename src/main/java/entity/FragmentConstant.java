package entity;

public class FragmentConstant {
    public FragmentConstant(String s) {
        fragment = "fragment." + s;
    }
    private String fragment;

    public String getFragments() {
        return "fragments";
    } 

    public String getSite() {
        return fragment + ".site";
    }
    public String getConditions() {
        return fragment + ".conditions";
    }

    public String getAttributes() {
        return fragment + ".attributes";
    }
    public String getIsHorizontal() {
        return fragment + ".isHorizontal";
    }

    public String getIsVertical() {
        return fragment + ".isVertical";
    }
    
}