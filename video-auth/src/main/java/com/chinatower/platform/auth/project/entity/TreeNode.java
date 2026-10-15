package com.chinatower.platform.auth.project.entity;


import java.util.List;

/**
 * 树节点父类
 *
 * @param <T> 节点id类型
 * @author huangke
 */
public interface TreeNode<T, E> {
    /**
     * 获取节点id
     *
     * @return 树节点id
     */
    T getId();

    /**
     * 获取该节点的父节点id
     *
     * @return 父节点id
     */
    T getPid();

    /**
     * 是否是根节点
     *
     * @return true：根节点
     */
    Boolean root();

    /**
     * 设置节点的子节点列表
     *
     * @param children 子节点
     */
    void setChildren(List<E> children);

    /**
     * 获取所有子节点
     *
     * @return 子节点列表
     */
    List<E> getChildren();
}
