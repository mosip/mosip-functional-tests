package io.mosip.testrig.apirig.utils;

import java.util.*;

public class VariableDependencyMapper {

    private final Map<String, List<String>> variableGenerators;
    private final Map<String, List<String>> variableConsumers;
    private final Map<String, Set<String>> consumerToGenerators = new HashMap<>();
    private final Map<String, Set<String>> generatorToConsumers = new HashMap<>();
    private final Map<String, String> variableToGenerator = new HashMap<>();

    public VariableDependencyMapper(Map<String, List<String>> variableGenerators, Map<String, List<String>> variableConsumers) {
        this.variableGenerators = variableGenerators;
        this.variableConsumers = variableConsumers;
        buildVariableToGeneratorMap();
        buildConsumerToGeneratorsMap();
        buildGeneratorToConsumersMap();
    }

    private void buildVariableToGeneratorMap() {
        for (Map.Entry<String, List<String>> entry : variableGenerators.entrySet()) {
            String generator = entry.getKey();
            for (String variable : entry.getValue()) {
                variableToGenerator.put(variable, generator);
            }
        }
    }

    private void buildConsumerToGeneratorsMap() {
        for (Map.Entry<String, List<String>> entry : variableConsumers.entrySet()) {
            String consumer = entry.getKey();
            Set<String> generatorsUsed = new HashSet<>();
            for (String variable : entry.getValue()) {
                String generator = variableToGenerator.get(variable);
                if (generator != null) {
                    generatorsUsed.add(generator);
                }
            }
            consumerToGenerators.put(consumer, generatorsUsed);
        }
    }

    private void buildGeneratorToConsumersMap() {
        for (Map.Entry<String, Set<String>> entry : consumerToGenerators.entrySet()) {
            String consumer = entry.getKey();
            for (String generator : entry.getValue()) {
                generatorToConsumers.computeIfAbsent(generator, k -> new HashSet<>()).add(consumer);
            }
        }
    }

    public Map<String, Set<String>> getConsumerToGeneratorsMap() {
        return consumerToGenerators;
    }

    public Map<String, Set<String>> getGeneratorToConsumersMap() {
        return generatorToConsumers;
    }

    public String getImpactSummary() {
    	StringBuilder impactSummaryBuilder = new StringBuilder();
        for (Map.Entry<String, Set<String>> entry : generatorToConsumers.entrySet()) {
            String generator = entry.getKey();
            Set<String> affectedConsumers = entry.getValue();
            impactSummaryBuilder.append(generator + " causes failures of " + String.join(", ", affectedConsumers)).append("\n");
        }
        return impactSummaryBuilder.toString();
    }
    
    public String getImpactSummaryBasedOnGenerator() {
        StringBuilder summary = new StringBuilder();
        for (Map.Entry<String, Set<String>> entry : generatorToConsumers.entrySet()) {
            String generator = entry.getKey();
            Set<String> affectedConsumers = entry.getValue();
            int count = affectedConsumers.size();
            summary.append(generator)
                    .append(" causes skip of ")
                    .append(count)
                    .append(" test case(s): ")
                    .append(String.join(", ", affectedConsumers))
                    .append("\n");
        }
        return summary.toString();
    }
    
    public String getImpactSummaryBasedOnConsumer() {
        StringBuilder summary = new StringBuilder();
        for (Map.Entry<String, Set<String>> entry : consumerToGenerators.entrySet()) {
            String consumer = entry.getKey();
            Set<String> affectedGenerators = entry.getValue();
            int count = affectedGenerators.size();
            summary.append(consumer)
                    .append(" causes skip of ")
                    .append(count)
                    .append(" test case(s): ")
                    .append(String.join(", ", affectedGenerators))
                    .append("\n");
        }
        return summary.toString();
    }

    // Demo
    public static void main(String[] args) {
        Map<String, List<String>> generators = new HashMap<>();
        generators.put("TestCaseA", Arrays.asList("Variable1", "Variable2", "Variable3"));
        generators.put("TestCaseB", Arrays.asList("Variable4", "Variable5"));

        Map<String, List<String>> consumers = new HashMap<>();
        consumers.put("TestCase1", Arrays.asList("Variable1"));
        consumers.put("TestCase2", Arrays.asList("Variable1", "Variable2", "Variable3"));
        consumers.put("TestCase3", Arrays.asList("Variable5", "Variable2"));

        VariableDependencyMapper mapper = new VariableDependencyMapper(generators, consumers);

        System.out.println("Consumer to Generators Mapping:");
        mapper.getConsumerToGeneratorsMap().forEach((k, v) -> System.out.println(k + " → " + v));

        System.out.println("\nImpact Summary:");
        System.out.println(mapper.getImpactSummary());
        
        System.out.println(mapper.getImpactSummaryBasedOnGenerator());
        
        System.out.println(mapper.getImpactSummaryBasedOnConsumer());
    }
}