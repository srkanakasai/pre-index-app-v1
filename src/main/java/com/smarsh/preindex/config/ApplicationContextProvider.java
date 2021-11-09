package com.smarsh.preindex.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware{

	private static ApplicationContext applicationContext = null;
	private static Boolean isOutlierEnabled = false;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ApplicationContextProvider.applicationContext = applicationContext;
		ApplicationContextProvider.isOutlierEnabled = applicationContext.getBean(PreIndexMetaConfigs.class).getEnableOutliers();
	}
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	public static Boolean isOutlierEnabled() {
		return isOutlierEnabled;
	}
}
