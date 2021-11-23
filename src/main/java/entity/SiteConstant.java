package entity;
public class SiteConstant {
    private String siteName;
    public SiteConstant(String siteName) {
        this.siteName = "site." + siteName;
    }

    public String getSites() {
        return "sites";
    }

    public String getSiteNo() {
        return siteName + ".no";
    }

    public String getSiteIp() {
        return siteName + ".ip";
    }
    public String getFragments() {
        return siteName + ".fragments";
    }

}



