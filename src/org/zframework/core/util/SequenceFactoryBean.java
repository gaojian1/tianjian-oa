package org.zframework.core.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;
/**
 * 生成订单号
 * @author zhumin
 *
 */
@Component
public class SequenceFactoryBean implements FactoryBean<String> {
	 private static long counter = 0;
     
	    public synchronized String getObject() throws Exception {
	        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	        String sequ = new DecimalFormat("000").format(counter ++);
	        return date + sequ;
	    }
	 
	    public Class<String> getObjectType() {
	        return String.class;
	    }
	 
	    public boolean isSingleton() {
	        return false;
	    }
	 
	    public static void reset() {
	        SequenceFactoryBean.counter = 0;
	    }
}
