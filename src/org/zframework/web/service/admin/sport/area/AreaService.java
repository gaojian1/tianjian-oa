package org.zframework.web.service.admin.sport.area;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.criterion.Criterion;
import org.springframework.stereotype.Service;
import org.zframework.orm.query.PageBean;
import org.zframework.web.entity.sport.Area;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;
/**
 * 区域查询service
 * @author zhumin
 *
 */
@Service
public class AreaService extends BaseService<Area> {
	/**
	 * 显示用户权限树
	 * @return
	 */
	public JSONArray toAreaTree() {
		JSONArray jsonTree=new JSONArray();
		List<Area> listArea = this.list("from Area area where area.parent_id is null");
		for(Area root:listArea){
			JSONObject jRes = new JSONObject();
			jRes.element("id", root.getId());
			jRes.element("text", root.getAreaname());
			jRes.element("iconCls", "icon-unit");
			List<Area> secound = this.listForEntity("from Area area where area.parent_id=?",new Object[]{root.getId()} );
			if(secound.size() >0){
				JSONArray childList=new JSONArray();
				for(Area secoundAres:secound){
					JSONObject childUnit=new JSONObject();
					childUnit.element("id", secoundAres.getId());
					childUnit.element("text", secoundAres.getAreaname());
					childUnit.element("iconCls", "icon-unit");
					List<Area> childLists = this.listForEntity("from Area area where area.parent_id=?",new Object[]{secoundAres.getId()} );
					JSONArray thirdList=new JSONArray();
					for(Area child:childLists){
						JSONObject thridUnit=new JSONObject();
						thridUnit.element("id", child.getId());
						thridUnit.element("text", child.getAreaname());
						thridUnit.element("iconCls", "icon-unit");
						thirdList.add(thridUnit);
					}
					childUnit.element("children", thirdList);
					childList.add(childUnit);
					jRes.element("children", childList);
				}
			}
			jsonTree.add(jRes);
		}
		return jsonTree;
	}
	
	
	/**
	 * 用于查询用户区域信息
	 * @param clazz
	 * @param hql
	 * @param pageBean
	 * @param params
	 * @return
	 */
	public List<User> findUserByArea(@SuppressWarnings("rawtypes") Class clazz, String hql, PageBean pageBean, Object...params){
		pageBean.setTotalCount(baseDao.count(Long.class, "select count(*)" +hql.toString(), params));
		return baseDao.listForEntity(clazz, "select user "+hql.toString(), pageBean, params);
	}

}
