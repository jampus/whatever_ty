/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.ga.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.thinkgem.ga.entity.IdNoInfo;



/**
 * 主键查询DAO接口
 * @author liuwsh
 * @version 2017-02-17
 */
public class IdNoInfoDao {
	public static IdNoInfo select(IdNoInfo idNoInfo, String type) throws IOException {
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		SqlSession session = sqlSessionFactory.openSession();
		IdNoInfo i;
		if (type.equals("card")) {
			i = session.selectOne("com.thinkgem.ga.entity.IdNoInfo.selectCard", idNoInfo);
		} else {
			i = session.selectOne("com.thinkgem.ga.entity.IdNoInfo.selectGate", idNoInfo);
		}
		return i;
	}

	public static void main(String args[]) {
		IdNoInfo idNoInfo = new IdNoInfo();
		idNoInfo.setNo("200");
		try {
			System.out.println(select(idNoInfo, "gate").getId());;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}