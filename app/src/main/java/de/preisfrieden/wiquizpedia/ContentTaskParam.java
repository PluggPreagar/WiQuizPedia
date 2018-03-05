package de.preisfrieden.wiquizpedia;

/**
 * Created by peter on 05.03.2018.
 */

public class ContentTaskParam {

    public Content getContent() {
        return content;
    }

    public String getNewTitle() {
        return newTitle;
    }

    private Content content;
    private String newTitle;

    ContentTaskParam( Content content, String newTitle) {
        this.content = content;
        this.newTitle = newTitle;
    }


}
