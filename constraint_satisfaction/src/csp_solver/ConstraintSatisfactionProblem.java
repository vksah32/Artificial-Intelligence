package csp_solver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javafx.util.Pair;

/**
 * ConstraintSatisfactionProblem.java
 * Author: Vivek Sah
 * Date: 20th feb 2016
 */
public class ConstraintSatisfactionProblem {
    private int nodesExplored;
    private int constraintsChecked;

    private Map<Integer, Set<Integer>> variables= new HashMap<>();
    private HashMap<Pair<Integer,Integer>, Set<Pair<Integer,Integer>>> constraints = new HashMap<Pair<Integer, Integer>, Set<Pair<Integer, Integer>>>();
    private HashMap<Integer, ArrayList<Integer>> Neighbors = new HashMap<>();
    private boolean useMRV = false;
    private boolean useLCV = false;
    private boolean useShuffle = false;

    /**
     * Solve for the CSP problem
     * @return the mapping from variables to values
     */
    public Map<Integer, Integer> solve() {
        System.out.println("calling solve");
        resetStats();
        long before = System.currentTimeMillis();
        if (!enforceConsistency()) {
            System.out.println("no consistency");
            return null;
        }
        Map<Integer, Integer> solution = backtracking(new HashMap<>());
        double duration = (System.currentTimeMillis() - before) / 1000.0;
        printStats();
        System.out.println(String.format("Search time is %.2f second", duration));
        return solution;
    }
    
    private void resetStats() {
        nodesExplored = 0;
        constraintsChecked = 0;
    }
    
    private void incrementNodeCount() {
        ++nodesExplored;
    }
    
    private void incrementConstraintCheck() {
        ++constraintsChecked;
    }
    
    public int getNodeCount() {
        return nodesExplored;
    }
    
    public int getConstraintCheck() {
        return constraintsChecked;
    }
    
    protected void printStats() {
        System.out.println("Nodes explored during last search:  " + nodesExplored);
        System.out.println("Constraints checked during last search " + constraintsChecked);
    }

    /**
     * Add a variable with its domain
     * @param id      the identifier of the variable
     * @param domain  the domain of the variable
     */
    public void addVariable(Integer id, Set<Integer> domain) {
        Set<Integer> varDomain = new HashSet<>(domain);

//        System.out.println("id is "+ id + "domain is " + varDomain);
        variables.put(id,varDomain);
    }
    
    /**
     * Add a binary constraint
     * @param id1         the identifier of the first variable
     * @param id2         the identifier of the second variable
     * @param constraint  the constraint
     */
    public void addConstraint(Integer id1, Integer id2, Set<Pair<Integer, Integer>> constraint) {
        Pair<Integer, Integer> key = new Pair<>(id1,id2);
        constraints.put(key, constraint);
        Pair<Integer, Integer> key2 = new Pair<>(id2,id1);
        Set<Pair<Integer,Integer>>revConstraint = new HashSet<>(); //make a reverse constraint set
        for(Pair<Integer,Integer> tempConstraint : constraint){ //copy everything from constraint but reverse it
            revConstraint.add(new Pair<>(tempConstraint.getValue(), tempConstraint.getKey()));
        }
        constraints.put(key2, revConstraint);
        if(Neighbors.containsKey(id1) && Neighbors.containsKey(id2)){
            Neighbors.get(id1).add(id2);
            Neighbors.get(id2).add(id1);
        }
        else{
            ArrayList<Integer> tempListId1 = new ArrayList<Integer>();
            ArrayList<Integer> tempListId2 = new ArrayList<Integer>();
            tempListId1.add(id2);
            tempListId2.add(id1);
            Neighbors.put(id1, tempListId1);
            Neighbors.put(id2, tempListId2);
        }
    }



    /**
     * Enforce consistency by AC-3, PC-3.
     */
    private boolean enforceConsistency() {
        return  ac3();
    }

    /**
     * revise the map to remove redundancies, this version is for use with mac3
     * @param id1
     * @param id2
     * @param removed
     * @return
     */
    private boolean revise(Integer id1, Integer id2, Map<Integer, Set<Integer>> removed) {
        boolean revised = false;
        for(Iterator<Integer> iterator = variables.get(id1).iterator(); iterator.hasNext();) {  //return iterator to modify hashmap concurrently
            int k = iterator.next();
            boolean satisfied = false;
            for (int l : variables.get(id2)) {
                Pair<Integer, Integer> tempArray = new Pair<Integer, Integer>(id1, id2);
                Pair<Integer, Integer> tempPair = new Pair<Integer, Integer>(k, l);
                incrementConstraintCheck();
                //Check if (k, l) satisfy the constraints in k, l
                if (constraints.keySet().contains(tempArray)) {
                    if (constraints.get(tempArray).contains(tempPair))
                        satisfied = true;
                }
            }
            //If no value in Dy allows to satisfy the constraint.
            if (!satisfied) {
                iterator.remove();
                revised = true;
            }
        }
        return revised;
    }

    /**
     * same revise as above but just for ac3 no recording required
     * @param id1
     * @param id2
     * @return
     */
    private boolean revise(Integer id1, Integer id2) {
        boolean revised = false;
        for(Iterator<Integer> iterator = variables.get(id1).iterator(); iterator.hasNext();){
            int count=0;
            int x = iterator.next();
            for(int y:variables.get(id2)){
                incrementConstraintCheck();
                if(constraints.keySet().contains(new Pair<Integer,Integer>(id1,id2))) {
                    if (constraints.get(new Pair<Integer, Integer>(id1, id2)).contains(new Pair<Integer, Integer>(x, y))) {
                        count++;
                    }
                }
            }
            if (count == 0){

                iterator.remove();
                revised=true;
            }
        }

        return revised;
    }

    /**
     * Backtracking algorithm
     * @param partialSolution  a partial solution
     * @return a solution if found, null otherwise.
     */
    private Map<Integer, Integer> backtracking(Map<Integer, Integer> partialSolution) {
//        System.out.println("calling backtracking when partial sol is " + partialSolution.keySet().size() +" "+ variables.keySet().size() );
        incrementNodeCount();
        if(partialSolution.keySet().size() == variables.keySet().size()){
            return partialSolution;
        }

        int variable = useMRV ? selectUnassignedVariableMRV(partialSolution) : selectUnassignedVariable(partialSolution);
        Iterable<Integer> orderedDomainValues = useLCV ? orderDomainValuesLCV(variable,partialSolution) : orderDomainValues(variable,partialSolution);
        for(int value : orderedDomainValues){

                Map<Integer,Set<Integer>> removed = new HashMap<>();

                if(isConsistent(value,variable,partialSolution)){
//                System.out.println("is consistent variable: "+  variable + "value:" + value  );
                    partialSolution.put(variable,value);
                    if (inference(variable,value,partialSolution,removed)) {
                        Map<Integer, Integer> result = backtracking(partialSolution);
                        if (result != null) {
                            return result;
                        }
                    }
                }

                //if inference did not work, revert back the changes
                for(int removedVariable: removed.keySet()){
                    for(int remVal: removed.get(removedVariable)){
                        variables.get(removedVariable).add(remVal);
                    }
                }
                partialSolution.remove(variable);
                removed.clear();

            }
//        }
        return null;


    }

    private boolean isConsistent(int value, int variable, Map<Integer, Integer> partialSolution) {
        for(int otherVariable:partialSolution.keySet()){
            //make a pair of variable and each variable which is already assigned  and c
            // check if the pair current value and the value corresponding to the otherVariable assigned checks the consistency
                incrementConstraintCheck();
                Pair<Integer, Integer> currentVariables = new Pair<>(variable, otherVariable);
                if (!constraints.containsKey(currentVariables)) {

//                    System.out.println(currentVariables);
//                    System.out.println("constraint doesn't contain this pair");
                    continue;
                } else {
                    Pair<Integer, Integer> values = new Pair<>( value, partialSolution.get(otherVariable));
                    if (!constraints.get(currentVariables).contains(values)) {
                        return false;
                    }

                }


        }
        return true;
    }

    /**
     * Inference for backtracking
     * Implement FC and MAC3
     * @param var              the new assigned variable
     * @param value            the new assigned value
     * @param partialSolution  the partialSolution
     * @param removed          the values removed from other variables' domains
     * @return true if the partial solution may lead to a solution, false otherwise.
     */
    private boolean inference(Integer var, Integer value, Map<Integer, Integer> partialSolution, Map<Integer, Set<Integer>> removed) {
        return mac3(var,value, partialSolution, removed);
    }
 
    /**
     * Look-ahead value ordering
     * Pick the least constraining value (min-conflicts)
     * @param var              the variable to be assigned
     * @param partialSolution  the partial solution
     * @return an order of values in var's domain
     */
    private Iterable<Integer> orderDomainValues(Integer var, Map<Integer, Integer> partialSolution) {
        ArrayList<Integer> domainValues = new ArrayList<Integer>(variables.get(var));
        if(useShuffle)
            Collections.shuffle(domainValues);
        return  domainValues;

    }

    private Iterable<Integer> orderDomainValuesLCV(Integer var, Map<Integer, Integer> partialSolution) {
//        HashMap<Integer,Integer> domainScores = new HashMap<>();
        ArrayList<domainNode> domainScoreNodes = new ArrayList<>();
        ArrayList<Integer> domainValues = new ArrayList<>();
        //no neighbors for the variable => return the domain as it is
        if (Neighbors.get(var).size() == 0){
            return Neighbors.get(var);
        }

        for (int domain: variables.get(var)){
            domainScoreNodes.add(new domainNode(domain));
        }

        for (domainNode domain: domainScoreNodes){
            for(int neighbor: Neighbors.get(var)){
                for(int neighborDomain: variables.get(neighbor)){
//                    if(constraints.get)
                    Pair<Integer,Integer> keypair = new Pair<>(var, neighbor);
                    Pair<Integer,Integer> domainpair = new Pair<>(domain.getDomain(), neighborDomain);
                    if(constraints.keySet().contains(keypair)){
                        if(constraints.get(keypair).contains(domainpair)){
                            domain.setScore(domain.getScore()+1);
                        }
                    }

                }
            }
        }
        Collections.sort(domainScoreNodes);
        ArrayList<Integer> orderedDomains = new ArrayList<>();
        for(domainNode doms: domainScoreNodes){
            orderedDomains.add(doms.getDomain());
        }

        return orderedDomains;
    }

    /**
     * Dynamic variable ordering
     * Pick the variable with the minimum remaining values or the variable with the max degree.
     * Or pick the variable with the minimum ratio of remaining values to degree.
     * @param partialSolution  the partial solution
     * @return one unassigned variable
     */
    private Integer selectUnassignedVariable(Map<Integer, Integer> partialSolution) {
        for (int k: variables.keySet()){
//            System.out.println("getting unassigned " + k);
            if(!partialSolution.keySet().contains(k)){
                return k;
            }
        }
        return  null;
    }

    private Integer selectUnassignedVariableMRV(Map<Integer, Integer> partialSolution) {
        int minVar = 0;
        int minSize = Integer.MAX_VALUE;
        for (int k: variables.keySet()){
            if(!partialSolution.keySet().contains(k)){
                if(variables.get(k).size() < minSize){
                    minSize = variables.get(k).size();
                    minVar = k;
                }
            }
        }

        return minVar;
    }

    public Map<Integer, Set<Integer>> getVariables() {
        return variables;
    }

    /**
     * Backjumping
     * Conflict-directed-backjumping
     * @param partialSolution
     */
    private void jumpBack(Map<Integer, Integer> partialSolution) {
    }


    private boolean ac3(){
        ArrayList<Pair<Integer,Integer>> arcQueue = new ArrayList<>(constraints.keySet());//constraints.keySet());
        while(arcQueue.size()>0){
            Pair<Integer,Integer> vars = arcQueue.remove(0);
            if (revise(vars.getKey(), vars.getValue() )){
                if ((variables.get(vars.getKey())).size() == 0){
                    System.out.println("I am here");
                    return false;
                }
                for(int neighbor: Neighbors.get(vars.getKey())){
                    if(neighbor != vars.getValue()){
                        arcQueue.add(new Pair<Integer, Integer>(neighbor,vars.getKey()));
                    }
                }
            }
        }
        return true;
    }




    public boolean mac3(int currentVar,int val, Map<Integer, Integer> partialSolution, Map<Integer, Set<Integer>> removed){
        ArrayList<Pair<Integer,Integer>> arcQueue = new ArrayList<>();
        for(int neighbor: Neighbors.get(currentVar)){
            if(!partialSolution.containsKey(neighbor)){
                arcQueue.add(new Pair<>(neighbor,currentVar));
            }
        }

        while(arcQueue.size()>0){
            Pair<Integer,Integer> vars = arcQueue.remove(0);
            if (revise(vars.getKey(), vars.getValue(), removed)){
                if (variables.get(vars.getKey()).size() == 0){
                    return false;
                }
                for(int neighbor: Neighbors.get(vars.getKey())){
                    if(neighbor != vars.getValue()){
                        arcQueue.add(new Pair<Integer, Integer>(neighbor,vars.getKey()));
                    }
                }


            }
        }
        return true;
    }



    public static void main(String[] args){

        //test case australia map
        ConstraintSatisfactionProblem csp = new ConstraintSatisfactionProblem();
        Set<Integer> domain = new HashSet<>();
        domain.add(1); //red
        domain.add(2); //green
        domain.add(3); //blue


        csp.addVariable(11, domain); //WA
        csp.addVariable(12, domain); //NT
        csp.addVariable(13, domain); //Q
        csp.addVariable(14, domain); //NSW
        csp.addVariable(15, domain); //V
        csp.addVariable(16, domain); //SA


        Set<Pair<Integer,Integer>> constraints = new HashSet<>();
        constraints.add(new Pair<Integer, Integer>(1,2)); //red-green
        constraints.add(new Pair<Integer, Integer>(1,3)); //red-blue
        constraints.add(new Pair<Integer, Integer>(2,1)); //green-red
        constraints.add(new Pair<Integer, Integer>(2,3)); //green-blue
        constraints.add(new Pair<Integer, Integer>(3,1)); //blue-red
        constraints.add(new Pair<Integer, Integer>(3,2)); //blue-green

        csp.addConstraint(11,12,constraints);
        csp.addConstraint(12,11, constraints);

        csp.addConstraint(12,13,constraints);
        csp.addConstraint(13,12, constraints);

        csp.addConstraint(13,14,constraints);
        csp.addConstraint(14,13, constraints);

        csp.addConstraint(14,15,constraints);
        csp.addConstraint(15,14, constraints);

        csp.addConstraint(11,12,constraints);
        csp.addConstraint(12,11, constraints);

        csp.addConstraint(15,16,constraints);
        csp.addConstraint(16,15, constraints);

        csp.addConstraint(11,16,constraints);
        csp.addConstraint(16,11, constraints);

        csp.addConstraint(12,16,constraints);
        csp.addConstraint(16,12, constraints);

        csp.addConstraint(13,16,constraints);
        csp.addConstraint(16,13, constraints);

        csp.addConstraint(14,16,constraints);
        csp.addConstraint(16,14, constraints);


        csp.solve();
    }

    //private class domainNode used for LCV
    private class domainNode implements Comparable<domainNode> {
        int score = 0;
        int domain;

        public domainNode(int domain){
            this.domain = domain;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getDomain() {
            return domain;
        }

        public int getScore() {
            return score;
        }

        @Override
        public int compareTo(domainNode o) {
            return (this.score - o.score);
        }
    }

}
