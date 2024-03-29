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

import java.util.Stack;

import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.rule.Package;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.Query;
import org.drools.core.rule.Rule;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.spi.DeclarationScopeResolver;

/**
 * A context for the current build
 */
public class RuleBuildContext extends PackageBuildContext {
	
	private static String TAG = "RuleBuildContext";

    // current rule
    private Rule rule;

    // a stack for the rule building used
    // for declarations resolution
    private Stack<RuleConditionElement> buildStack;

    // current Rule descriptor
    private RuleDescr ruleDescr;

    // available declarationResolver 
    private DeclarationScopeResolver declarationResolver;

    // a simple counter for patterns
    private int patternId = -1;

    private DroolsCompilerComponentFactory compilerFactory;

    private boolean needStreamMode = false;

    private Pattern prefixPattern;

    /**
     * Default constructor
     */
    public RuleBuildContext(final PackageBuilder pkgBuilder,
                            final RuleDescr ruleDescr,
                            final DialectCompiletimeRegistry dialectCompiletimeRegistry,
                            final Package pkg,
                            final Dialect defaultDialect) {
    	
    	System.out.println("\ni'm: "+TAG+" in method: Constructor\n");

        this.buildStack = new Stack<RuleConditionElement>();

        this.declarationResolver = new DeclarationScopeResolver(pkgBuilder.getGlobals(),
                                                                this.buildStack);
        this.declarationResolver.setPackage(pkg);
        this.ruleDescr = ruleDescr;

        if (ruleDescr instanceof QueryDescr) {
            this.rule = new Query(ruleDescr.getName());
        } else {
            this.rule = new Rule(ruleDescr.getName());
        }
        this.rule.setPackage(pkg.getName());
        this.rule.setDialect(ruleDescr.getDialect());
        this.rule.setLoadOrder( ruleDescr.getLoadOrder() );

        init(pkgBuilder, pkg, ruleDescr, dialectCompiletimeRegistry, defaultDialect, this.rule);

        if (this.rule.getDialect() == null) {
            this.rule.setDialect(getDialect().getId());
        }

//        System.out.println("\ni'm: "+TAG+" in method: Constructor::: i set rule.dialect=java!!!\n");
        
//        String TESTdialect = getDialect().getId();
//        this.rule.setDialect(TESTdialect);
//        this.rule.setDialect("java");

        Dialect dialect = getDialect();
        if (dialect != null ) {
            dialect.init( ruleDescr );
        }

        compilerFactory = pkgBuilder.getPackageBuilderConfiguration().getComponentFactory();

    }

    /**
     * Returns the current Rule being built
     * @return
     */
    public Rule getRule() {
        return this.rule;
    }

    /**
     * Returns the current RuleDescriptor
     * @return
     */
    public RuleDescr getRuleDescr() {
        return this.ruleDescr;
    }

    /**
     * Returns the available declarationResolver instance
     * @return
     */
    public DeclarationScopeResolver getDeclarationResolver() {
        return this.declarationResolver;
    }

    /**
     * Sets the available declarationResolver instance
     * @param variables
     */
    public void setDeclarationResolver(final DeclarationScopeResolver variables) {
        this.declarationResolver = variables;
    }

    public int getPatternId() {
        return this.patternId;
    }

    public int getNextPatternId() {
        return ++this.patternId;
    }

    public void setPatternId(final int patternId) {
        this.patternId = patternId;
    }

    public Stack<RuleConditionElement> getBuildStack() {
        return this.buildStack;
    }

    public DroolsCompilerComponentFactory getCompilerFactory() {
        return compilerFactory;
    }

    public void setCompilerFactory(DroolsCompilerComponentFactory compilerFactory) {
        this.compilerFactory = compilerFactory;
    }

    public boolean needsStreamMode() {
        return needStreamMode;
    }

    public void setNeedStreamMode() {
        this.needStreamMode = true;
    }

    public void setPrefixPattern(Pattern prefixPattern) {
        this.prefixPattern = prefixPattern;
    }

    public Pattern getPrefixPattern() {
        return prefixPattern;
    }
}
