import java.util.List;
import java.util.*;

public class HashListAutocomplete implements Autocompletor {
    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap = new HashMap<>();
    private int mySize;

    public HashListAutocomplete(String[] terms, double[] weights) {
        if (terms == null || weights == null) {
            throw new NullPointerException("One or more arguments null");
        }
        if (terms.length != weights.length) {
            throw new IllegalArgumentException("terms and weights are not the same length");
        }
        
        initialize(terms, weights);
    }

    @Override
    public List<Term> topMatches(String prefix, int k) {
        if (k == 0){
            return new LinkedList<Term>();
        }
        if (prefix.length() > MAX_PREFIX){
            prefix = prefix.substring(0, MAX_PREFIX);
        }
        List<Term> all = myMap.get(prefix);
        if (all == null) return new ArrayList<>();
        List<Term> list = all.subList(0, Math.min(k, all.size()));

        return list;
    }

    @Override
    public void initialize(String[] terms, double[] weights) {
        
        myMap.clear();
        mySize = 0;
        for(int term = 0; term < terms.length; term++){
            Term t = new Term(terms[term], weights[term]);
            mySize += BYTES_PER_CHAR * t.getWord().length();
            mySize += BYTES_PER_DOUBLE;
            for(int k = 0; k <= Math.min(MAX_PREFIX, terms[term].length()); k++){
                String prefix = terms[term].substring(0, k);
                if(!myMap.containsKey(prefix)){
                    myMap.put(prefix, new ArrayList<>());
                    mySize += prefix.length() * BYTES_PER_CHAR;
                }
                myMap.get(prefix).add(t);
            }
        }

        for (String key : myMap.keySet()) {
            List<Term> list = myMap.get(key);
            Collections.sort(list, Comparator.comparing(Term::getWeight).reversed());
        }
        
    }

    @Override
    public int sizeInBytes() {
        return mySize;
    }
    
}

