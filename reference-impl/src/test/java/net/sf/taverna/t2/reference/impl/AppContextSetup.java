package net.sf.taverna.t2.reference.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppContextSetup {
	public static List<ApplicationContext> contextList;

	public static void setup() throws Exception {
		if (contextList == null){
			
			contextList = new ArrayList<ApplicationContext>();
			//Add all three contexts for storing referenced data
			contextList = new ArrayList<ApplicationContext>();
			ApplicationContext context = null;
			context = new ClassPathXmlApplicationContext(
					"vanillaHibernateAppContext.xml"); // hibernate context
			contextList.add(context);
			context = new ClassPathXmlApplicationContext(
			"vanillaInMemoryAppContext.xml");
			contextList.add(context); // in memory
			context = new ClassPathXmlApplicationContext(
			"vanillaHibernateTransactionalAppContext.xml");
			contextList.add(context);	 // transactional hibernate context
			
		}
	}
}
