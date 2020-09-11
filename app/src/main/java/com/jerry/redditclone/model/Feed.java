package com.jerry.redditclone.model;

import com.jerry.redditclone.model.entry.Entrys;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.List;

@Root(name = "feed", strict = false)
public class Feed implements Serializable {


    @Element(name = "icon")
    private String icon;

    @Element(name = "id")
    private String id;

    @Element(name = "logo")
    private String logo;

    @Element(name = "title")
    private String title;

    @Element(name = "updated")
    private String updated;

    @Element(name = "subtitle")
    private String subtitle;

    @ElementList(inline = true,name = "entry")
    private List<Entrys> entry;


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<Entrys> getEntry() {
        return entry;
    }

    public void setEntry(List<Entrys> entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "icon='" + icon + '\'' +
                ", id='" + id + '\'' +
                ", logo='" + logo + '\'' +
                ", title='" + title + '\'' +
                ", updated='" + updated + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", entry=" + entry +
                '}';
    }
}
