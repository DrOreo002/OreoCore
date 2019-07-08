package me.droreo002.oreocore.utils.inventory;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A paginator class, made by Septogeddon useful for making
 * paginated GUI or text message
 *
 * @param <T> object def
 */
public class Paginator<T> {

    @Getter
    private List<T> list;
    @Getter
    private int tot;

    public Paginator(List<T> lists, int totalItem) {
        list = lists;
        tot = totalItem;
    }

    public Paginator(List<T> lists) {
        list = lists;
        tot = lists.size();
    }

    /**
     * Paginate the list
     *
     * @param itemPerPage Item per page
     * @return the List of paginated pages
     */
    public List<List<T>> paginates(int itemPerPage) {
        if (itemPerPage <= 0) throw new IllegalArgumentException("Invalid item per page : " + itemPerPage);
        ArrayList<List<T>> paged = new ArrayList<>();
        int totalPage = 0;
        for (int i = 0; i < list.size(); i += itemPerPage)
            totalPage++;
        int page = 0;
        while (page < totalPage) {
            paged.add(paginate(page, itemPerPage));
            page++;
        }
        return paged;
    }

    /**
     * Get the total page will be
     *
     * @param itemPerPage Item per page
     * @return the total page
     */
    public int totalPage(int itemPerPage) {
        if (itemPerPage < 0) return 0;
        int totalPage = 1;
        for (int i = 0; i < list.size(); i += itemPerPage)
            totalPage++;
        return totalPage;
    }

    /**
     * Paginate the list
     *
     * @param page Page to get
     * @return the List of resulted pagination
     */
    public List<T> paginate(int page) {
        if (tot < 0 || page < 0) {
            return new ArrayList<>();
        }

        int fromIndex = (page) * tot;
        if (list == null || list.size() < fromIndex) {
            return new ArrayList<>();
        }

        return list.subList(fromIndex, Math.min(fromIndex + tot, list.size()));
    }

    /**
     * Paginate the list
     *
     * @param page Page to get
     * @param itemPerPage Item per page
     * @return the List of resulted pagination
     */
    private List<T> paginate(int page, int itemPerPage) {
        if (itemPerPage < 0 || page < 0) {
            return new ArrayList<>();
        }

        int fromIndex = (page) * itemPerPage;
        if (list == null || list.size() < fromIndex) {
            return Collections.emptyList();
        }
        return list.subList(fromIndex, Math.min(fromIndex + itemPerPage, list.size()));
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
