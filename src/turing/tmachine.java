package turing;

import java.util.ArrayList;
import java.util.HashMap;

	// initialize and create the tape for the TuringMachine
  class Tape {
	private ArrayList<String> tape;
	private int tSize;
  private int current;
	// initialized string for blank
	private final static String BLANK = "_";
		//blank target tape starting
    public Tape() { this(BLANK); }
    public Tape(String input) {
    	this.tSize=input.length()+2;
    	tape = new ArrayList<String>(this.tSize);
    	char[] inputs=input.toCharArray();
    	for (int i=0;i<this.tSize;i++){
    		if (i==0||i==this.tSize-1)
    			tape.add(i, BLANK);
    		else
    			tape.add(String.valueOf(inputs[i-1]));
    	}
    	current = 1;
    }

    public int getCurrent(){
    	return this.current;
    }

    public int getTSize(){
    	return this.tSize;
    }

    public void move(byte direction){
    	if (this.current==0||this.current==this.tSize-1) return;
    	if (direction==constantfields.MOV_L)
    		this.current-=1;
    	else
    		this.current+=1;
    }

    public String read(){
    	return this.tape.get(current);
    }

    public void write(String symbol){
    	this.tape.set(current, symbol);
    }

    public String getLeft(){
    	String left="";
    	for (int i=1;i<current;i++){
    		left+=this.tape.get(i);
    	}
    	return left;
    }

    public String getRight(){
    	String right="";
    	for (int i=current+1;i<this.tSize-1;i++){
    		right+=this.tape.get(i);
    	}
    	return right;
    }
}


 interface constantfields {
	//handling states
	public static final byte isnormal = 0;
	public static final byte isaccepted = 1;
	public static final byte isrejected = 2;
	public static final byte ishalt=3;


	//encapsulating states
	public static String[] STATE_TYPE={"Normal","Accepted","Rejected","Halt"};

	//commands
	public static final String CMD_rwRt="rwRt";
	public static final String CMD_rwLt="rwLt";
	public static final String CMD_rRl="rRl";
	public static final String CMD_rLl="rLl";
	public static final String CMD_rRt="rRt";
	public static final String CMD_rLt="rLt";
	public static final String[] CMD={"rwRt","rwLt","rRl","rLl","rRt","rLt"};

  //move left and right
	public static final byte MOV_L=0;
	public static final byte MOV_R=1;
}

public class tmachine{
	private ArrayList<String> tmFileList;
	private String testWord;
	private HashMap<String,State> states;
	private HashMap<String, Transition> transitions;
	private Tape tape;
	private State state;
	private String alpha="";
	private String tapealpha="";
	private boolean canSimulate=true;
	private ArrayList<String> message;
	private String error;

	public tmachine(ArrayList<String> parsedfile,String userWord){
		this.tmFileList = parsedfile;
		this.testWord = userWord;
		states=new HashMap<String,State>();
		transitions=new HashMap<String,Transition>();
		tape=new Tape(testWord);
		message=new ArrayList<String>();

		initialize(); // initialize
	}

	// run machine
	private void initialize(){
		//take in the file
		for (String line:tmFileList){
			//error if false is given
			if (!this.canSimulate) break;
			//parse comments
			if (line.startsWith("--")) continue;
			//
			//
			//parsing lines for the set of states given
			//
			//
			if (line.contains("states")){
				String[] fields=line.substring(line.indexOf(":")+1,line.length()-1).split(",");
				for (String field:fields)
					states.put(field.trim(), new State(field.trim(),constantfields.isnormal));
					//
					//
					//parsing lines for the start state
					//
					//
			}else if(line.contains("start")){
				if (states!=null)
					state=states.get(line.substring(line.indexOf(":")+1,line.length()-1).trim());
				else
					setError("The tm file states must be listed before the start state!");
					 //
				 	 //
					 // checking the lines for an accept state
					 //
					 //
			}else if(line.contains("accept")){
				if (states!=null){
					State s=states.get(line.substring(line.indexOf(":")+1,line.length()-1).trim());
					if (s!=null)
					//if states are listed under accept, parse them in to the accept type
						s.setStateType(constantfields.isaccepted);
					else
					//otherwise the accept state isn't properly listed.
						setError("The accept state is not in the states!");
				}else
					setError("The tm file must have accept state after states,{accept:}!");
					//
					//
					// parsing the lines for the reject state
					//
					//
			}else if (line.contains("reject")){
				if (states!=null){
					State s=states.get(line.substring(line.indexOf(":")+1,line.length()-1).trim());
					if (s!=null)
						s.setStateType(constantfields.isrejected);
					else
						setError("The reject state is not in states!");
				}else
					setError("The tm file must have reject state after states,{reject:}!");

			}else if (line.contains("alpha") && !line.contains("tape")){ 		// need to have {alpha:}
				String[] fields=line.substring(line.indexOf(":")+1,line.length()-1).split(",");
				for (String field:fields)
					this.alpha+=field.trim();

			}else if (line.contains("tape-alpha")){ // need to have {tape-alpha:}
				String[] fields=line.substring(line.indexOf(":")+1,line.length()-1).split(",");
				for (String field:fields)
					this.tapealpha+=field.trim();
				this.tapealpha+="_";

			}else if (line.startsWith("r")){
				if (line.indexOf(";")<0) continue;
				String[] fields=line.substring(0,line.indexOf(";")).split(" ");
				if (fields.length<2 || fields.length>5) setError(line+" is the wrong algorithm.");
				if (fields.length==5)
					if (!fields[0].equals(constantfields.CMD_rwRt) && !fields[0].equals(constantfields.CMD_rwLt)) setError(line+" is the wrong algorithm.");

				if (fields.length==3)
					if (!fields[0].equals(constantfields.CMD_rRl) && !fields[0].equals(constantfields.CMD_rLl)) setError(line+" is the wrong algorithm.");

				if (fields.length==4)
					if (!fields[0].equals(constantfields.CMD_rRt) && !fields[0].equals(constantfields.CMD_rLt)) setError(line+" is the wrong algorithm.");

				if (this.states.get(fields[1])==null) setError(fields[1]+" is the wrong state.");
				if (!this.tapealpha.contains(fields[2])) setError(fields[2]+" is the wrong read.");
				if (this.canSimulate) addTransition(fields);
			}
		}

		if (this.canSimulate){
			// test input word
			for (char ch:this.testWord.toCharArray()){
				if (this.tapealpha.indexOf(ch)<0) {
					setError("The input word isn't in the tape alphabet.");
					break;
				}
			}

			if (states==null) setError("Cannot find states,{states:}!");
			if (state==null) setError("Cannot find start state,{start:}!");
			if (this.alpha==null) setError("Cannot find alphabet,{alpha:}!");
			if (this.transitions==null) setError("Cannot find the algorithm.");
			if (this.tapealpha==null) setError("Cannot find the tape alphabet. {tape-alpha:}!");
			if (this.alpha!=null && this.tapealpha!=null){
				for (char ch:this.alpha.toCharArray()){
					if (this.tapealpha.indexOf(ch)<0){
						// the alphabet that is chose from the word must be a subset of the tape alphabet excluding
						// the empty set
						setError("The alphabet must be a subset of the tape alphabet.");
						break;
					}
				}
			}
		}
	}

	// Add TM transitions
	private void addTransition(String[] cmd) {
		State fromState=null, nextState=null;
		String read="",write="";
		byte direction=0;

		fromState=this.states.get(cmd[1]);
		read=cmd[2];

		// rwRt Q0 0 x Q1 || rwLt Q4 1 x Q5 from state read write new_state tape move right|left
		if (cmd[0].equals(constantfields.CMD_rwRt) || cmd[0].equals(constantfields.CMD_rwLt)){
			write=cmd[3];
			nextState=this.states.get(cmd[4]);
			direction=cmd[0].equals(constantfields.CMD_rwRt)? constantfields.MOV_R : constantfields.MOV_L;

		// 	rRl Q1 1 || rLl Q6 0 from state read tape move right|left
	}else if (cmd[0].equals(constantfields.CMD_rRl) || cmd[0].equals(constantfields.CMD_rLl)){
			write=read;
			nextState=fromState;
			direction=cmd[0].equals(constantfields.CMD_rRl)? constantfields.MOV_R : constantfields.MOV_L;

		// rRt Q0 # Q7 || rLt Q0 # Q7 from state read new_state tape move right|left
	}else if (cmd[0].equals(constantfields.CMD_rRt) || cmd[0].equals(constantfields.CMD_rLt)){
			write=read;
			nextState=this.states.get(cmd[3]);
			direction=cmd[0].equals(constantfields.CMD_rRt)? constantfields.MOV_R : constantfields.MOV_L;
		}

		//System.out.println(fromState.getstateName() + "|" + read);

        if (transitions.get(fromState.getstateName() + "|" + read) != null)
        	setError("Nondeterministic Turing machine");
        else
        	transitions.put(fromState.getstateName()+"|"+read, new Transition(fromState,nextState,read,write, direction));

	}

	public boolean getCanSimulate(){
		return this.canSimulate;
	}

	public String getError(){
		return this.error;
	}

	private void setError(String error){
		this.canSimulate=false;
		this.error=error;
	}

	public String getResult(){
		String result="";
		for (String str:message)
			result+=str+"\n";
		return result;
	}

	public void simulate(){
		while (state.getStateType()!=constantfields.isaccepted &&
			   state.getStateType()!=constantfields.isrejected &&
			   state.getStateType()!=constantfields.ishalt){
			String read=tape.read();
			message.add(tape.getLeft()+"["+state.getstateName()+"]"+("_".equals(read) ? "":read)+tape.getRight());
			System.out.println(tape.getLeft()+"["+state.getstateName()+"]"+read+tape.getRight());
			Transition transition = transitions.get(this.state.getstateName()+"|"+read);
			if (transition!=null){
				tape.write(transition.getWrite());		// write
				tape.move(transition.getDirection());  	// move tape
				state=transition.getNextState();		// next state

				if (state.getStateType()!=constantfields.isaccepted &&
						tape.getCurrent()==0 && transition.getDirection()==constantfields.MOV_L)
					state.stateType=constantfields.ishalt; // set next state type

				if (state.getStateType()!=constantfields.isaccepted &&
						tape.getCurrent()==tape.getTSize() && transition.getDirection()==constantfields.MOV_R)
					state.stateType=constantfields.ishalt; // set next state type

			}else{
				state.setStateType(constantfields.ishalt);	// there is nowhere to go
			}
		}

		if (state.getStateType()==constantfields.isaccepted){
			message.add(tape.getLeft()+"["+state.getstateName()+"]"+("_".equals(tape.read()) ? "":tape.read())+tape.getRight());
			message.add("Accepted!");
			}
		else
			message.add("Rejected!");
	}
}

  class Transition {
	private State fromS, nextS;
  private String read, write;
	private byte direction;

    public Transition(State from, State next, String read, String write,byte direction) {
        this.fromS 		 = from;
        this.nextS 		 = next;
        this.read 		 = read;
        this.write		 = write;
        this.direction = direction;
    }

    public byte getDirection(){
    	return this.direction;
    }

    public State getNextState(){
    	return this.nextS;
    }

    public String getWrite(){
    	return this.write;
    }

    @Override
    public String toString(){
		return "From state: " + this.fromS + " read: " + this.read + " write: " + this.write + " Next State:" + this.nextS;
    }
}
