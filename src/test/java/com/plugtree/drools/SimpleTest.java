/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plugtree.drools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import org.junit.BeforeClass;

/**
 *
 * @author esteban
 */
public class SimpleTest {
    
    public final static int NUMBER_OF_RULES = 1000;
    public final static int WARMUP = 100;
    private static String RULES;
    
    public SimpleTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        
        //get a list of classes to make our rules a little bit more complex
        List<String> classNames = ClassListHelper.getClasses();
        
        System.out.println("# of classes: "+classNames.size());
        
        Random random = new Random(System.currentTimeMillis());
        
        StringBuilder builder = new StringBuilder("package com.drools.test;\n\n");
        for (int i = 0; i < NUMBER_OF_RULES; i++) {
            builder.append("rule \"Rule");
            builder.append(i);
            builder.append("\"\n");
            builder.append("when\n");
            builder.append("\t$o: ");
            builder.append(classNames.get(random.nextInt(classNames.size())));
            builder.append("()\n");
            builder.append("\t");
            builder.append(classNames.get(random.nextInt(classNames.size())));
            builder.append("\t(this == $o)\n");
            builder.append("\t$a: ");
            builder.append(classNames.get(random.nextInt(classNames.size())));
            builder.append("()\n");
            builder.append("\t");
            builder.append(classNames.get(random.nextInt(classNames.size())));
            builder.append("\t(this == $a)\n");
            builder.append("then\n");
            builder.append("end\n\n");
        }
        
        RULES = builder.toString();
    }

    
    
    
    @Test
    public void doTest() {
        
        /////// KBUILDER
        
        KnowledgeBuilder kbuilder = null;
        Resource rulesResource = ResourceFactory.newByteArrayResource(RULES.getBytes());
        
        //rules compilation warm up
        long totalRuleCompilationWarmUpTime = 0;
        for (int i = 0; i < WARMUP; i++) {
            long timeBeforeRuleCompilationWarmUp = System.currentTimeMillis();
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(rulesResource , ResourceType.DRL);
            totalRuleCompilationWarmUpTime += System.currentTimeMillis()-timeBeforeRuleCompilationWarmUp;
        }
    
        if (kbuilder.hasErrors()){
            Iterator<KnowledgeBuilderError> errors = kbuilder.getErrors().iterator();
            while (errors.hasNext()) {
                KnowledgeBuilderError knowledgeBuilderError = errors.next();
                System.out.println("Compilation Error: "+knowledgeBuilderError.getMessage());
            }
            throw new IllegalStateException("Compilation Errros!");
        }
        
        //Total time of compilation warm up:
        System.out.println("Total time of compilation warm up: "+(totalRuleCompilationWarmUpTime/1000)+" segs.");
        //average time of compilation warm up:
        System.out.println("Average time of compilation warm up: "+((totalRuleCompilationWarmUpTime/WARMUP)/1000)+" segs. ["+(totalRuleCompilationWarmUpTime/WARMUP)+" ms.]");
        
        //real rules compilation:
        long timeBeforeCompilation = System.currentTimeMillis();
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(rulesResource , ResourceType.DRL);
        long totalRuleCompilationTime = System.currentTimeMillis()-timeBeforeCompilation;
    
        //Real time of compilation:
        System.out.println("Real time of compilation: "+(totalRuleCompilationTime/1000)+" segs.");
        
        
        
        /////// KBASE
        System.out.println("\n\n");
        
        //kbase creation warmup
        KnowledgeBase kbase;
        long totalKbaseCreationWarmUpTime = 0;
        for (int i = 0; i < WARMUP; i++) {
            long timeBeforeKbaseCreationWarmUp = System.currentTimeMillis();
            kbase = KnowledgeBaseFactory.newKnowledgeBase();
            kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
            totalKbaseCreationWarmUpTime += System.currentTimeMillis()-timeBeforeKbaseCreationWarmUp;
        }
        
        //Total time of kbase creation warm up:
        System.out.println("Total time of kbase creation warm up: "+(totalKbaseCreationWarmUpTime/1000)+" segs.");
        //average time of kbase creation warm up:
        System.out.println("Average time of kbase creation warm up: "+((totalKbaseCreationWarmUpTime/WARMUP)/1000)+" segs. ["+(totalKbaseCreationWarmUpTime/WARMUP)+" ms.]");
        
        //real kbase creation
        long timeBeforeKbaseCreation = System.currentTimeMillis();
        kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        long totalKbaseCreationTime = System.currentTimeMillis()-timeBeforeKbaseCreation;
    
        //real kbase creation time:
        System.out.println("Real time of compilation: "+(totalKbaseCreationTime/1000)+" segs.");
     
        
        /////// KSESSION
        System.out.println("\n\n");
        
        //Stateful ksession creation warm up
        StatefulKnowledgeSession ksession;
        long totalKsessionCreationWarmUpTime = 0;
        for (int i = 0; i < WARMUP; i++) {
            long timeBeforeKsessionCreationWarmUp = System.currentTimeMillis();
            ksession = kbase.newStatefulKnowledgeSession();
            totalKsessionCreationWarmUpTime += System.currentTimeMillis()-timeBeforeKsessionCreationWarmUp;
        }
        
        //Total time of kbase creation warm up:
        System.out.println("Total time of ksession creation warm up: "+(totalKsessionCreationWarmUpTime/1000)+" segs.");
        //average time of kbase creation warm up:
        System.out.println("Average time of ksession creation warm up: "+((totalKsessionCreationWarmUpTime/WARMUP)/1000)+" segs. ["+(totalKsessionCreationWarmUpTime/WARMUP)+" ms.]");
        
    }
}
