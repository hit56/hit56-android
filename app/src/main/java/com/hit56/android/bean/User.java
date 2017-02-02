package com.hit56.android.bean;

import java.util.ArrayList;
import java.util.Vector;

/**
 * @author yiw
 * @ClassName: User
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2015-12-28 下午3:45:04
 */
public class User {
	private String id;
	private String name;
	private String passwd;
	private String headUrl;
	ArrayList<String> goods = new ArrayList<String>();
	ArrayList<String> cars = new ArrayList<String>();

	public User(){

	}
	public User(String id, String name, String headUrl) {
		this.id = id;
		this.name = name;
		this.headUrl = headUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadUrl() {
		return headUrl;
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String qty) {
		this.passwd = qty;
	}

	public ArrayList<String> getGoods() {
		return goods;
	}

	public void setGoods(ArrayList<String> goods) {
		this.goods = goods;
	}

	public ArrayList<String> getCars() {
		return cars;
	}

	public void setCars(ArrayList<String> cars) {
		this.cars = cars;
	}

	@Override
	public String toString() {
		return "id = " + id
				+ "; name = " + name
				+ "; headUrl = " + headUrl;
	}
}