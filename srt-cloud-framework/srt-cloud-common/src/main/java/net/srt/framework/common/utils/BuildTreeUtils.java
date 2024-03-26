package net.srt.framework.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName BuildTreeUtils
 * @Author zrx
 * @Date 2022/11/14 14:26
 */
public class BuildTreeUtils {


	/**
	 * 构建结构树
	 *
	 * @param nodeVos
	 * @return
	 */
	public static List<TreeNodeVo> buildTree(List<TreeNodeVo> nodeVos) {
		List<TreeNodeVo> resultVos = new ArrayList<>(10);
		for (TreeNodeVo node : nodeVos) {
			// 一级菜单parentId为0
			if (node.getParentId() == 0) {
				resultVos.add(node);
			}
		}
		// 为一级菜单设置子菜单，getChild是递归调用的
		for (TreeNodeVo node : resultVos) {
			node.setChildren(getChild(node.getId(), nodeVos));
		}
		return resultVos;
	}


	private static List<TreeNodeVo> getChild(Long id, List<TreeNodeVo> nodeVos) {
		// 子菜单
		List<TreeNodeVo> childList = new ArrayList<>(10);
		for (TreeNodeVo node : nodeVos) {
			// 遍历所有节点，将父菜单id与传过来的id比较
			if (node.getParentId() != 0) {
				if (node.getParentId().equals(id)) {
					childList.add(node);
				}
			}
		}
		// 把子菜单的子菜单再循环一遍
		for (TreeNodeVo node : childList) {
			node.setChildren(getChild(node.getId(), nodeVos));
		}
		return childList;
	}

}
