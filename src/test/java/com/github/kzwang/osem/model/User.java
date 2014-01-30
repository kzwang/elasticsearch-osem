package com.github.kzwang.osem.model;

import com.github.kzwang.osem.annotations.IndexEnum;
import com.github.kzwang.osem.annotations.IndexableProperties;
import com.github.kzwang.osem.annotations.IndexableProperty;

public class User {

    @IndexableProperty(index = IndexEnum.NOT_ANALYZED)
    private String userName;

    @IndexableProperties(properties = {
            @IndexableProperty(name = "description", index = IndexEnum.ANALYZED),
            @IndexableProperty(name = "untouched", index = IndexEnum.NOT_ANALYZED)
    })
    private String description;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (description != null ? !description.equals(user.description) : user.description != null) return false;
        if (userName != null ? !userName.equals(user.userName) : user.userName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
