package me.droreo002.oreocore.utils.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Paginator<GUIButton> {

    private List<GUIButton> list;
    private int tot;

    public Paginator(List<GUIButton> lists, int totalitem) {
        list = lists;
        tot = totalitem;
    }

    public String toString() {
        return list.toString();
    }

    public Paginator(List<GUIButton> lists) {
        list = lists;
        tot = lists.size();
    }

    public List<List<GUIButton>> paginates() {
        return paginates(tot);
    }

    public List<List<GUIButton>> paginates(int itemPerPage) {
        if (itemPerPage <= 0) throw new IllegalArgumentException("Invalid item per page : " + itemPerPage);
        ArrayList<List<GUIButton>> paged = new ArrayList<>();
        int totalPage = 0;
        for (int i = 0; i < list.size(); i += itemPerPage)
            totalPage++;
        int page = 0;
        while (page < totalPage) {
            paged.add(paginate(page,itemPerPage));
            page++;
        }
        return paged;
    }

    public int totalPage() {
        return totalPage(tot);
    }

    public int totalPage(int itemPerPage) {
        if (itemPerPage < 0) return 0;
        int totalPage = 1;
        for (int i = 0; i < list.size(); i += itemPerPage)
            totalPage++;
        return totalPage;
    }

    public List<GUIButton> paginate(int page) {
        if (tot < 0 || page < 0) {
            return new ArrayList<>();
        }

        int fromIndex = (page) * tot;
        if (list == null || list.size() < fromIndex) {
            return new ArrayList<>();
        }

        return list.subList(fromIndex, Math.min(fromIndex + tot, list.size()));
    }

    public List<GUIButton> paginate(int page, int itemperpage) {
        if (itemperpage < 0 || page < 0) {
            return new ArrayList<>();
        }

        int fromIndex = (page) * itemperpage;
        if (list == null || list.size() < fromIndex) {
            return Collections.emptyList();
        }
        return list.subList(fromIndex, Math.min(fromIndex + itemperpage, list.size()));
    }
}
