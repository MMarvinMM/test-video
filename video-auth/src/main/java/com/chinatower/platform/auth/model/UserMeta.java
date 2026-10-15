
package com.chinatower.platform.auth.model;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("all")
public class UserMeta implements Serializable {
    private ArrayList<String> roleList = new ArrayList();
    private ArrayList<String> authorities = new ArrayList();

    public UserMeta() {
    }

    public ArrayList<String> getRoleList() {
        return this.roleList;
    }

    public ArrayList<String> getAuthorities() {
        return this.authorities;
    }

    public void setRoleList(final ArrayList<String> roleList) {
        this.roleList = roleList;
    }

    public void setAuthorities(final ArrayList<String> authorities) {
        this.authorities = authorities;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof UserMeta)) {
            return false;
        } else {
            UserMeta other = (UserMeta)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$roleList = this.getRoleList();
                Object other$roleList = other.getRoleList();
                if (this$roleList == null) {
                    if (other$roleList != null) {
                        return false;
                    }
                } else if (!this$roleList.equals(other$roleList)) {
                    return false;
                }

                Object this$authorities = this.getAuthorities();
                Object other$authorities = other.getAuthorities();
                if (this$authorities == null) {
                    if (other$authorities != null) {
                        return false;
                    }
                } else if (!this$authorities.equals(other$authorities)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof UserMeta;
    }



    public String toString() {
        return "UserMeta(roleList=" + this.getRoleList() + ", authorities=" + this.getAuthorities() + ")";
    }
}
