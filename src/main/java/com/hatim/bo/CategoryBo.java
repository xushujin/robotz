package com.hatim.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * 分组.
 *
 * @author ScienJus
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @date 15/12/19.
 */
public class CategoryBo {

    private int index;

    private int sort;

    private String name;

    private List<FriendBo> friends = new ArrayList<>();

    public void addFriend(FriendBo friend) {
        this.friends.add(friend);
    }

    @Override
    public String toString() {
        return "CategoryBo{"
                + "index=" + index
                + ", sort=" + sort
                + ", name='" + name + '\''
                + ", friends=" + friends
                + '}';
    }

    public static CategoryBo defaultCategory() {
        CategoryBo category = new CategoryBo();
        category.setIndex(0);
        category.setSort(0);
        category.setName("我的好友");
        return category;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FriendBo> getFriends() {
        return friends;
    }

    public void setFriends(List<FriendBo> friends) {
        this.friends = friends;
    }

}
