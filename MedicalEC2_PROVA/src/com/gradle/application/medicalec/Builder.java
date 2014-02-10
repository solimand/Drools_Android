/**
 * 
 */
package com.gradle.application.medicalec;

//import org.drools.compiler.kie.builder.impl.KieRepositoryImpl;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.kie.builder.impl.KieRepositoryImpl;
import org.drools.core.ClockType;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

import android.util.Log;

public class Builder {


//private static String RULE1 ="package com.gradle.application.medicalec \n\n"
//	+ 	"import android.util.Log;\n\n"
//		+ 	"rule \"Test\" \n when \n then \n Log.i(\"RULE_TAG\", \"I'm alive!\"); \n end \n"
//			+ 	"rule \"Test2\" \n when \n then \n Log.i(\"RULE_TAG\", \"I'm alive22!\"); \n end";
	
	private static String RULE1 ="package com.gradle.application.medicalec \n dialect \"mvel\"; \n"
			+ 	"rule \"Test\" \n when \n then System.err.println(\"Alive!\"); \n  \n end \n";
 
	
	private static final String PACKAGE = "com.gradle.application.medicalec";

	private static final String TAG = "BuilderDrools";

	public Builder() {}
	
	public Session build() {
		KieServices ks = KieServices.Factory.get();
 		Log.i(TAG, "\ni must be a singleton [Class "+TAG+" ---> ks: " +ks.hashCode()+"]\n");
		KieFileSystem kfs = ks.newKieFileSystem();
		Resource src = ks.getResources()
				.newByteArrayResource(RULE1.getBytes())
				.setResourceType( ResourceType.DRL )
//				.setSourcePath( "medicalec/full.drl" );
				.setSourcePath( "medicalecTEST.drl" )
		;
 		kfs.write( src );
 		
 		ReleaseId rid = ks.newReleaseId( PACKAGE, "medicalec", "1.0" );
 		kfs.generateAndWritePomXML(rid);
 		
// 		KieBuilder kieBuilder =null;
// 				
// 		try{
//	 		kieBuilder = ks.newKieBuilder( kfs );	 		 		
//	 		kieBuilder.buildAll();
// 		}
// 		catch(RuntimeException t) {
//             Log.e(TAG, t.toString());
// 		}
 		
 		KieBuilder kieBuilder = ks.newKieBuilder(kfs);
 		kieBuilder.buildAll();
 		
  		if (kieBuilder.getResults().hasMessages(Level.ERROR)) {
 		    throw new RuntimeException( "Build Errors:\n" + kieBuilder.getResults().getMessages( Level.ERROR ).toString() );
 		}		 		
  		
  		KieRepository repo = ks.getRepository();
  		((KieRepositoryImpl) repo).addKieModule( kieBuilder.getKieModule() );
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
 	
 		
 		Log.i(TAG, "Kbase creata = " + kBase + "\n" +
 				"Ksess creata = " + kSession + "\n" +
// 				"rule = " + kBase.getRule("rules/com/gradle/application/medicalec", "Test") + "\n" +		//!!!return NULL
 				"rule = " + kBase.getRule(PACKAGE, "Test") + "\n" +		//!!!return NULL
 				"package= " + kBase.getKiePackage("PACKAGE") + "\n" +		//!!!return NULL
 				"package= " + kBase.getKiePackages().size() + "\n" 
 				);
 		
		Session result = new Session( kBase, kSession/*, sessConfig*/);
		return result;
	}

	private static String getRule() {
        String s = "" +
                   "package com.gradle.application.medicalec \n\n" +
//					"import android.util.Log;\n\n" +
                   "rule \"Test\" when \n" +
                   "then \n" +
//                   "	Log.i(\"RULE_TAG\", \"I'm alive!\"); \n" +
//                   "    System.err.println( \" ciao \" );  \n" +
                   "    System.out.println(\"I'm alive!\");  \n" +
                   "end \n" ;
        
        						
        						/*+
                   "rule \"rule 2\" when \n" +
                   "    Message( text == \"Hello, HAL. Do you read me, HAL?\" ) \n" +
                   "then \n" +
                   "    insert( new Message(\"HAL\", \"Dave. I read you.\" ) ); \n" +
                   "end";*/

        return s;
    }

}
