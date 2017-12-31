package turing;

public class State{
	private String stateName;
	protected byte stateType;

	public State(String stateName, byte stateType) {
		this.stateName = stateName;
		this.stateType = stateType;
	}

	public String getstateName() {
		return stateName;
	}

	public byte getStateType() {
		return stateType;
	}

	public void setStateType(byte stateType){
		this.stateType=stateType;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(State Name:" + this.stateName + "\t");
		sb.append("State Type:" + constantfields.STATE_TYPE[this.stateType] + ")\n");
		return sb.toString();
	}
}
