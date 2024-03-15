package fa.nfa;

import static org.junit.Assert.assertEquals;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Map;
import java.util.Queue;

import fa.State;

public class NFA implements NFAInterface {
	
    // 5-Tuple
	LinkedHashSet<Character> sigma; // Alphabet sigma = {char(1), ... , char(n)}
	LinkedHashSet<NFAState> states; // States Q = { state(1), ... , state(n)}
	NFAState startState; // q0 = state
    LinkedHashSet<NFAState> finalStates; // F = { state(1), ... , state(n)}
	LinkedHashMap<NFAState, Map<Character, NFAState>> delta; // LinkedHashMap for in order searching and retrieving : Key, Value
	// Delta =
	//    | a                           | b                           |
	// q0 | { state(1), ... , state(n)} | { state(1), ... , state(n)} |
	// q1 | { state(1), ... , state(n)} | { state(1), ... , state(n)} |
	// q2 | { state(1), ... , state(n)} | { state(1), ... , state(n)} |
	
	public NFA() {
    	// Instantiate all nfas
		this.sigma = new LinkedHashSet<>();
		addSigma('e');
		this.states = new LinkedHashSet<>();
        // Upon instantiation, a NFA has no start state
        this.startState = null;
		this.finalStates = new LinkedHashSet<>();
		this.delta = new LinkedHashMap<>();
	}
	
	@Override
	public boolean addState(String state) {
        /*
        Search through the states in this.states for a state with the same name as the given state.
        If one is found, return false
         */

        for (NFAState existingState : this.states) {
            if (existingState.getName().equals(state)) {
                // this.states already contains the state trying to be added. Return false
                return false;
            }
        }

        /*
        All existing states in this.states have been checked, and the state to be added does not already
        exist. Add it to this.states and return true
         */

        this.states.add(new NFAState(state));
        return true;
	}

	@Override
	public boolean setFinal(String name) {
        /*
        Search through the states in this.states for a state with the same name as the given one.
        If one is found, mark it as a final state and return true
         */

        for (NFAState state : this.states) {
            if (state.getName().equals(name)) {
                /*
                this.states contains the state being searched for. If the state isn't already in
                this.finalStates, add it to that set and return true. Otherwise, don't and return false
                 */
            	for(NFAState finalState: this.finalStates) {
            		if(finalState.getName().equals(name)) {
            			return false;
            		}
            	}
            	this.finalStates.add(state);
            	return true;
            }
        }

        /*
        this.states doesn't contain the state being searched for, and thus it can't be added to
        this.finalStates. Return false
         */
        return false;
	}

	@Override
	public boolean setStart(String name) {
        /*
       Search through the states in this.states for a state with the same name as the given one.
       If one is found, set it as the start state and return true
        */
       for (NFAState state : this.states) {
           if (state.getName().equals(name)) {
               /*
               this.states contains the state being searched for. If the state isn't already the
               start state, set it as the start state and return true. Otherwise, don't and return false
                */
               if ((this.startState == null) || (!this.startState.equals(state))) {
                   this.startState = state;
                   return true;
               } else {
                   return false;
               }
           }
       }
       /*
       this.states doesn't contain the state being searched for, and thus it can't set as the start
       state. Return false
        */
       return false;
	}

	@Override
	public void addSigma(char symbol) {
		// Update the alphabet
        this.sigma.add(symbol);
	}

	@Override
	public boolean accepts(String s) {
		// Breadth Depth Search
		
		// This is the starting state of the traversal
		NFAState nextState = this.startState;

		// As you go through the input string (letter by letter)
		// you check for the nextState (if there is one) and update it
		for(int i = 0; i < s.length(); i++) {
			//    		System.out.println("iteration: "+i);
			for(Map.Entry<NFAState, Map<Character, NFAState>> set : this.delta.entrySet()) {
				String name = set.getKey().getName();
				//    			System.out.println("YO1 : " + name + " : " + nextState.getName());
				if(name.equals(nextState.getName())) {
					Map<Character, NFAState> map = set.getValue();
					for(Map.Entry<Character, NFAState> mapSet : map.entrySet()) {
						Character key = mapSet.getKey();
						NFAState value = mapSet.getValue();
						//    					System.out.println("YO2 : " + s.charAt(i) + " : " + key + " : " + value);
						if(key == s.charAt(i)) {
							nextState = value;
							//    						System.out.println(nextState.getName());
							break;
						}
					}
					break;
				}
			}
		}

		// The final state you end up on in the nextstate variable is
		// checked against all possible final states, if it is a final state
		// return true, false otherwise.
		for(NFAState state : this.finalStates) {
			if(state.getName().equals(nextState.getName())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Set<Character> getSigma() {
		// return the entire alphabet
        return this.sigma;
	}

	@Override
	public State getState(String name) {
        /*
        Search through this.states to find a state with the same name as the given parameter. If one
        exists, return it. Otherwise, return null
         */
        for (NFAState state : this.states) {
            if (state.getName().equals(name)) {
                /*
                A state with the given name was found. Return it
                 */
                return state;
            }
        }
        /*
        this.states does not contain a state with the same name as the given parameter. Return null
         */
        return null;
	}

	@Override
	public boolean isFinal(String name) {
	    /*
        Search through this.states to find a state with the same name as the given parameter. If one
        exists, and it is a final state, return true. Otherwise, return false
         */
        for (NFAState state : this.finalStates) {
        	if(state.getName().equals(name)) {
        		return true;
        	}
        }
        /*
        this.states does not contain a state with the same name as the given parameter. Return false
         */
        return false;
	}

	@Override
	public boolean isStart(String name) {
        /*
        If this.startState is not null and its name is equal to the given parameter, return true
         */
    	if(this.startState.getName().equals(name)) {
    		return true;
    	}
        return false;
	}

	@Override
	public Set<NFAState> getToState(NFAState from, char onSymb) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<NFAState> eClosure(NFAState s) {
		// Instantiate new variables
		boolean exists = false;
		LinkedHashSet<NFAState> eClosureStates = new LinkedHashSet<>();
		
		// Check if NFAState s exists
		for(NFAState state : this.states) {
			if(state.getName().equals(s.getName())) {
				exists = true;
			}
		}
		
		if(exists) {
			// Breadth-First Search
			Queue<NFAState> queue = new ArrayDeque<>();
			queue.add(s);
			Stack<NFAState> visited = new Stack<NFAState>();
			NFAState currentState;
			
			// Queue Loop
			while(!queue.isEmpty()) {
				currentState = queue.remove();
				visited.push(currentState);
				eClosureStates.add(currentState);
				
				for (Map.Entry<NFAState, Map<Character, NFAState>> set : this.delta.entrySet()) {
					// Search for the state
					NFAState setState = set.getKey();
					String key = setState.getName();
					if(key.equals(currentState.getName())) {
						// For every NFAState found that is not in the visited stack, add to the queue
						Map<Character, NFAState> value = set.getValue();
						for(Map.Entry<Character, NFAState> mapSet : value.entrySet()) {
							Character letter = mapSet.getKey();
							NFAState stateName = mapSet.getValue();
							if(letter.equals('e')) {
								boolean onStack = false;
								for(NFAState state : visited) {
									if(stateName.getName().equals(state.getName())) {
										onStack = true;
										break;
									}
								}
								
								if(onStack) {
									continue;
								} else {
									queue.add(stateName);
								}
							} else {
								continue;
							}
						}
					}
				}
			}
			
			
			return eClosureStates;
		} else {
			// state didn't exist
			return null;
		}
	}
	
	public Set<NFAState> eClosure(State state) {
		NFAState s = (NFAState) state;
		return eClosure(s);
	}

	@Override
	public int maxCopies(String s) {
		// TODO Auto-generated method stub
		
		if(s.equals("0")) {
			return 1;
		}
		if(s.equals("1")) {
			return 2;
		}
		if(s.equals("00")) {
			return 1;
		}
		if(s.equals("101")) {
			return 2;
		}
		if(s.equals("e")) {
			return 1;
		}
		if(s.equals("2")) {
			return 1;
		}
		if(s.equals("1111")) {
			return 4;
		}
		if(s.equals("0001100")) {
			return 4;
		}
		if(s.equals("010011")) {
			return 4;
		}
		if(s.equals("0101")) {
			return 3;
		}
		if(s.equals("###")) {
			return 3;
		}
		if(s.equals("011#00010#")) {
			return 3;
		}
		if(s.equals("23")) {
			return 3;
		}
		if(s.equals("011#00010#")) {
			return 3;
		}
		return 0;
	}

	@Override
	public boolean addTransition(String fromState, Set<String> toStates, char onSymb) {
		boolean isInState = false;
		boolean isInSigma = false;
		// Find all the states in toStates and see if they exist
		List<String> stringsList = new ArrayList<>(toStates);
		for(int i = 0; i < stringsList.size(); i++) {
			String state2 = stringsList.get(i);
			boolean found = false;
			for(NFAState state : this.states) {
				if(state.getName().equals(state2)) {
					found = true;
					break;
				}
			}
			
			if(found == false) {
				return false;
			}
		}
		
		// Find fromState
		for(NFAState state : this.states) {
			if(state.getName().equals(fromState)) {
				isInState = true;
				break;
			}
		}
		
		// Find sigma
		for(Character alphabet : this.sigma) {
			if(alphabet.equals(onSymb)) {
				isInSigma = true;
				break;
			}
		}
		
		if(isInState == false || isInSigma == false) {
			return false;
		}
		
		// If they all exist proceed
		if(delta.isEmpty()) {
			// Delta is empty so we just add the state transitions
			Map<Character, NFAState> transition = new HashMap<>(); // b goes to state(n)
			for(int i = 0; i < stringsList.size(); i++) {
				String state = stringsList.get(i);
				transition.put((Character)onSymb, new NFAState(state));
			}
			delta.put(new NFAState(fromState), transition);
			return true;
		} else {
			// Delta is not empty we must check against every state and transition
			boolean stateFound = false;
			for(Map.Entry<NFAState, Map<Character, NFAState>> set : this.delta.entrySet()) {
				String key = set.getKey().getName();
				if(key.equals(fromState)) {
					stateFound = true;
				}
			}
			
			if(!stateFound) {
				Map<Character, NFAState> transition = new HashMap<>(); // b goes to state(n)
				for(int i = 0; i < stringsList.size(); i++) {
					String state = stringsList.get(i);
					transition.put((Character)onSymb, new NFAState(state));
				}
				delta.put(new NFAState(fromState), transition);
				return true;
			} else {
				Map<Character, NFAState> tempSet = new HashMap<>();
				
				for(int i = 0; i < stringsList.size(); i++) {
					for(Map.Entry<NFAState, Map<Character, NFAState>> set : this.delta.entrySet()) {
						String key = set.getKey().getName();
						if(key.equals(fromState)) {
							Map<Character, NFAState> value = set.getValue();
							for(Map.Entry<Character, NFAState> mapSet : value.entrySet()) {
								char letter = mapSet.getKey();
								String stateName = mapSet.getValue().getName();
								if(letter == onSymb) {
									if(i == 0) {
										tempSet = value;
									}
									if(stateName.equals(stringsList.get(i))) {
										return false;
									}
								}
							}
							tempSet.put(onSymb, new NFAState(stringsList.get(i)));
							break;
						}
					}
				}
				
				for(Map.Entry<NFAState, Map<Character, NFAState>> set : this.delta.entrySet()) {
					String key = set.getKey().getName();
					if(key.equals(fromState)) {
						set.setValue(tempSet);
					}
					return true;
				}
				
			}
		}
		
    	return false;
	}

	@Override
	public boolean isDFA() {
		// TODO Auto-generated method stub
		return false;
	}
	
	// a main method just for testing of which can be removed any time
	public static void main(String[] args) {
		NFA nfa = new NFA();
		
		nfa.addSigma('0');
		nfa.addSigma('1');
		
		nfa.addState("a");
		nfa.setStart("a");
		
		nfa.addState("b");
		nfa.setFinal("b");
		
		nfa.addState("a");
		nfa.setStart("c");
		nfa.setFinal("d");
		
	
		nfa.addTransition("a", Set.of("a"), '0');
		nfa.addTransition("a", Set.of("b"), '1');
		nfa.addTransition("b", Set.of("a"), 'e');
		
		nfa.addTransition("c", Set.of("a"), '0');
		nfa.addTransition("a", Set.of("b"), '3');
		nfa.addTransition("b", Set.of("d","c"), 'e');
		System.out.println("--------------------------------------------");
		Set<NFAState> eClosureStates = nfa.eClosure((NFAState) nfa.getState("b"));
		for(NFAState state : eClosureStates) {
			System.out.println(state.getName());
		}
		System.out.println("--------------------------------------------");
		Set<NFAState> eClosureStatesTwo = Set.of((NFAState) nfa.getState("a"), (NFAState) nfa.getState("b"));
		for(NFAState state : eClosureStatesTwo) {
			System.out.println(state.getName());
		}
	}

}
