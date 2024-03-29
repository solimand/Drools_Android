/*
 * Copyright 2006 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.rule.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.compiler.DroolsWarning;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.compiler.compiler.PackageBuilderConfiguration;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.rule.Dialectable;
import org.drools.core.rule.Package;

/**
 * A context for the current build
 */
public class PackageBuildContext {

	private static String TAG = "PackageBuildContext";
	
    // current package
    private Package                     pkg;

    private PackageBuilder pkgBuilder;

    // the contianer descr
    private BaseDescr                   parentDescr;

    // errors found when building the current context
    private List<DroolsError>           errors;
    private List<DroolsWarning>         warnings;

    // list of generated methods
    private List<String>                methods;

    // map<String invokerClassName, String invokerCode> of generated invokers
    private Map<String, String>         invokers;

    // map<String invokerClassName, ConditionalElement ce> of generated invoker lookups
    private Map                         invokerLookups;

    // map<String invokerClassName, BaseDescr descr> of descriptor lookups
    private Map                         descrLookups;

    // a simple counter for generated names
    private int                         counter;

    private DialectCompiletimeRegistry  dialectRegistry;

    private Dialect                     dialect;
    
    private boolean                     typesafe;

    public PackageBuildContext() {

    }

    /**
     * Default constructor
     */
    public void init(final PackageBuilder pkgBuilder,
                     final Package pkg,
                     final BaseDescr parentDescr,
                     final DialectCompiletimeRegistry dialectRegistry,
                     final Dialect defaultDialect,
                     final Dialectable component) {
    	
    	System.out.println("\ni'm: "+TAG+" in method: INIT\n");
    	
        this.pkgBuilder = pkgBuilder;
        
        this.pkg = pkg;

        this.parentDescr = parentDescr;

        this.methods = new ArrayList();
        this.invokers = new HashMap<String, String>();
        this.invokerLookups = new HashMap();
        this.descrLookups = new HashMap();
        this.errors = new ArrayList<DroolsError>();
        this.warnings = new ArrayList<DroolsWarning>();

        this.dialectRegistry = dialectRegistry;

        this.dialect = (component != null && component.getDialect() != null) ? this.dialectRegistry.getDialect( component.getDialect() ) : defaultDialect;

        this.typesafe = ((MVELDialect) dialectRegistry.getDialect( "mvel" )).isStrictMode();
//        this.typesafe = ((MVELDialect) dialectRegistry.getDialect( "java" )).isStrictMode();

//    	System.out.println("\ni'm: "+TAG+" in method: INIT::: i set typesafe to true!!!\n");
//    	this.typesafe=true;

    	
        if ( dialect == null && (component != null && component.getDialect() != null) ) {
            this.errors.add( new DescrBuildError( null,
                                                  parentDescr,
                                                  component,
                                                  "Unable to load Dialect '" + component.getDialect() + "'" ) );
            // dialect is null, but fall back to default dialect so we can attempt to compile rest of rule.
            this.dialect = defaultDialect;
        }
    }

    public BaseDescr getParentDescr() {
        return this.parentDescr;
    }

    public void setParentDescr(BaseDescr descr) {
        this.parentDescr = descr;
    }

    public Dialect getDialect() {
    	
    	System.out.print("\ni'm "+TAG+" in GetDialect");
        return dialect;
    }

    /**
     * Allows the change of the current dialect in the context
     */
    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public Dialect getDialect(String dialectName) {
        return (Dialect) this.dialectRegistry.getDialect( dialectName );
    }

    public DialectCompiletimeRegistry getDialectRegistry() {
        return this.dialectRegistry;
    }

    /**
     * Returns the list of errors found while building the current context
     * @return
     */
    public List<DroolsError> getErrors() {
        return this.errors;
    }

    public void addError(DroolsError error) {
        errors.add(error);
    }

    public List<DroolsWarning> getWarnings() {
        return warnings;
    }

    public void addWarning( DroolsWarning warning ) {
        this.warnings.add( warning );
    }

    /**
     * Returns the current package being built
     * @return
     */
    public Package getPkg() {
        return this.pkg;
    }

    /**
     * Returns the Map<String invokerClassName, BaseDescr descr> of descriptor lookups
     * @return
     */
    public Map getDescrLookups() {
        return this.descrLookups;
    }

    public void setDescrLookups(final Map descrLookups) {
        this.descrLookups = descrLookups;
    }

    /**
     * Returns the Map<String invokerClassName, ConditionalElement ce> of generated invoker lookups
     * @return
     */
    public Map getInvokerLookups() {
        return this.invokerLookups;
    }

    public void setInvokerLookups(final Map invokerLookups) {
        this.invokerLookups = invokerLookups;
    }

    /**
     * Returns the Map<String invokerClassName, String invokerCode> of generated invokers
     * @return
     */
    public Map<String, String> getInvokers() {
        return this.invokers;
    }

    public void setInvokers(final Map<String, String> invokers) {
        this.invokers = invokers;
    }

    /**
     * Returns the list of generated methods
     * @return
     */
    public List<String> getMethods() {
        return this.methods;
    }

    public void addMethod(String method) {
        this.methods.add(method);
    }

    /**
     * Returns current counter value for generated method names
     * @return
     */
    public int getCurrentId() {
        return this.counter;
    }

    public int getNextId() {
        return this.counter++;
    }

    public PackageBuilderConfiguration getConfiguration() {
        return this.pkgBuilder.getPackageBuilderConfiguration();
    }
    
    public PackageBuilder getPackageBuilder() {
        return this.pkgBuilder;
    }

    public boolean isTypesafe() {
        return typesafe;
    }

    public void setTypesafe(boolean stricttype) {
        this.typesafe = stricttype;
    }

}
