/**
 * 
 */


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.drools.core.ClockType;
import org.drools.core.RuleBaseConfiguration.AssertBehaviour;
import org.drools.core.io.impl.ClassPathResource;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

public class Builder {

	
//private static String RULE1 ="package com.gradle.application.medicalec \n\n"
//		+ 	"rule \"Test\" \n when \n then \n  \n end \n"
//			+ 	"rule \"Test2\" \n when \n then \n \n end"; 
 
	private static String RULE1 = "package com.gradle.application.medicalec; \n dialect \"mvel\"; \n"
	+ 	"rule \"Test\" \n when \n then System.err.println(\"Alive!\"); \n  \n end \n";
	
	private static final String PACKAGE = "com.gradle.application.medicalec";

	private static final String TAG = "BuilderDrools";

	public Builder() {}
	
	public void build() {
		KieServices ks = KieServices.Factory.get();
 		
		KieFileSystem kfs = ks.newKieFileSystem();
		Resource src = ks.getResources()
				.newByteArrayResource(RULE1.getBytes())
				.setResourceType( ResourceType.DRL )
				.setSourcePath( "medicalecTEST" ) //setta il nome della risorsa
				;
				
 		kfs.write( src );
 		
 		ReleaseId rid = ks.newReleaseId( PACKAGE, "medicalec", "1.0" );
 		kfs.generateAndWritePomXML(rid);
 		
 		KieBuilder kieBuilder =null;
 				
 		try{
 			kieBuilder = ks.newKieBuilder( kfs );	 		 		
 			kieBuilder.buildAll();
 		}
 		catch(RuntimeException t) {
             System.err.println(t.toString());
 		}
 		
  		if (kieBuilder.getResults().hasMessages(Level.ERROR)) {
 		    throw new RuntimeException( "Build Errors:\n" + kieBuilder.getResults().getMessages( Level.ERROR ).toString() );
 		}		 		
 	
  		KieModule mod = ks.getRepository().getKieModule(rid);
  		assert( mod != null );
  		
  		
 		KieContainer kContainer = ks.newKieContainer( kieBuilder.getKieModule().getReleaseId() );
 		
		KieBaseConfiguration baseCfg = KieServices.Factory.get().newKieBaseConfiguration();
		baseCfg.setOption( EventProcessingOption.STREAM );
		baseCfg.setOption( EqualityBehaviorOption.EQUALITY );
 		KieBase kBase = kContainer.newKieBase( baseCfg );
 				
 		KieSessionConfiguration sessConfig = KieServices.Factory.get().newKieSessionConfiguration();
 		sessConfig.setOption( ClockTypeOption.get( ClockType.REALTIME_CLOCK.getId() ) );

 		KieSession kSession = kBase.newKieSession( sessConfig, null );
 		
 		System.out.println("Kbase creata = " + kBase + "\n" +
 				"Ksess creata = " + kSession + "\n" +
 				"rule = " + kBase.getRule("com.gradle.application.medicalec", "Test") + "\n" +
 				"rule = " + kBase.getRule(PACKAGE, "Test2") + "\n" +
 				"package= " + kBase.getKiePackage("com.gradle.application.medicalec") + "\n" +
 				"package= " + kBase.getKiePackages().size() + "\n" 
 				);
	}

	public static void main( String... args ) {
		new Builder().build();
	}
}

//private static String getRule() {
//String s = "" +
//         "package com.gradle.application.medicalec \n\n" +
//         "rule \"Test\" when \n" +
//         "then \n" +
//         "    System.out.println(\"I'm alive!\");  \n" +
//         "end \n" ;
//return s;
//}
