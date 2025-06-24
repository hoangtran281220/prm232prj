package com.example.prm232rj.ui.adapter.section;

import com.example.prm232rj.data.dto.ComicDtoBanner;
import com.example.prm232rj.data.interfaces.IComicPreview;

import java.util.List;

public class HomeSectionItem {
    public int viewType;
    public List<ComicDtoBanner> banners;
    public List<IComicPreview> comics;
    public String sectionTitle;
    public String sectionTag;

    public HomeSectionItem(int viewType) {
        this.viewType = viewType;
    }
}
