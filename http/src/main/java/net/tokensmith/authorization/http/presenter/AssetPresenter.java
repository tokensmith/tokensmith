package net.tokensmith.authorization.http.presenter;

public class AssetPresenter {
    private String globalCssPath;

    public AssetPresenter() {
    }

    public AssetPresenter(String globalCssPath) {
        this.globalCssPath = globalCssPath;
    }

    public String getGlobalCssPath() {
        return globalCssPath;
    }

    public void setGlobalCssPath(String globalCssPath) {
        this.globalCssPath = globalCssPath;
    }
}
