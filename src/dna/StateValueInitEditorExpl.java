//==============================================================================
//	
//	Copyright (c) 2018-
//	Authors:
//	* Joachim Klein <klein@tcs.inf.tu-dresden.de> (TU Dresden)
//	
//------------------------------------------------------------------------------
//	
//	This file is part of PRISM.
//	
//	PRISM is free software; you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation; either version 2 of the License, or
//	(at your option) any later version.
//	
//	PRISM is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with PRISM; if not, write to the Free Software Foundation,
//	Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//	
//==============================================================================

package dna;

import java.util.ArrayList;
import java.util.List;

import parser.State;
import parser.VarList;
import parser.type.TypeInt;
/**
 * StateValueConsumer that formats the state/value pairs
 * and prints them to a PrismLog.
 */
public class StateValueInitEditorExpl 
{
	int numNewVar;
	private explicit.Model model;
	explicit.StateValues probsExpl;
	List<State> initialStates;
	List<State> finalStates;
	String targetSpec;
	List<String> discard;
	List<Double> prob;
	List<Double> prob2;
	int numAddedParticles;
	int[] newValue;
	char newAdding;
 	double mu;
 	double pi;
 	double mprob;
 	int varParticle;
 	
	public StateValueInitEditorExpl(explicit.StateValues probsExpl,int numNewVar, explicit.Model model, int[] newValue, int numAddedParticles, double mu, double pi, char newAdding, ArrayList<String> discard, String targetSpec, double mprob, int varParticle)
	{
		this.numNewVar = numNewVar;
		this.model = model;
		this.numAddedParticles = numAddedParticles;
		this.mu = mu;
		this.pi = pi;
		this.newAdding = newAdding;
		this.newValue = newValue;
		this.probsExpl = probsExpl;
		this.discard = discard;
		this.targetSpec = targetSpec;
		this.mprob = mprob;
		this.varParticle = varParticle;
		
		initialStates = new ArrayList<State>();
		finalStates = new ArrayList<State>();
		prob = new ArrayList<Double>();
		prob2 = new ArrayList<Double>();
	}
	
	
	public void setter() {
		// TODO Auto-generated method stub
		VarList varList = model.getVarList();
		int n = varList.getNumVars();

		for (int i = 0; i < probsExpl.getSize(); i++) {
			double value = (double)probsExpl.getValue(i);
			if (value < mprob) continue;
			
			int error = 0;
			int error2 = 0;
			State state = model.getStatesList().get(i);

			State initialState = new State(n+numNewVar);
			State finalState = new State(n);
			
			int ntarget = 0;
			for (int j = 0; j < n; j++) {
				if (varList.getType(j) instanceof TypeInt) {					
					if (varList.getName(j).equals(targetSpec))
						ntarget = (int)state.varValues[j];
				}				
			}
			
			for (int j = 0; j < n; j++) {
				// integer variable
				if (varList.getType(j) instanceof TypeInt) {					
					if (varList.getName(j).charAt(0) == 'l'){		
						int v = (int)state.varValues[j];
						
						if (varList.getName(j).equals(targetSpec))
							v = v - error2;
					
						finalState.setValue(j, v);
						
						v = (int)Math.round(v*(1-mu));
											
						initialState.setValue(j, v);
					} else {  
						finalState.setValue(j, (int)state.varValues[j]);
						
						if (varList.getName(j).charAt(0) == newAdding && varList.getName(j).length() == 1) {
							//initialState.setValue(j, numAddedParticles+(int)state.varValues[j]);
							initialState.setValue(j, varParticle+(int)state.varValues[j]);
						} else {
							int v = (int)Math.round((int)state.varValues[j]*pi);
							initialState.setValue(j, v);
						}
					}
				
					
					for (int k = 0; k < discard.size(); k++) {
						if (varList.getName(j).equals(discard.get(k))) {
							initialState.setValue(j, 0);
							break;
						}
					}					
				}
			}
			
			for (int j = n; j < n + numNewVar; j++) {
				int v = newValue[j-n];
				//if (v == numAddedParticles) v = v-error;
				initialState.setValue(j, v);
			}
			
			//System.out.println("initialState = " + initialState.toString());
			boolean unique = true;
			for (int j = 0; j < initialStates.size(); j++) {
				if (initialState.equals(initialStates.get(j))){
					prob.set(j, value + prob.get(j));
					unique = false;
					break;
				}				
			}
			
			boolean unique2 = true;
			for (int j = 0; j < finalStates.size(); j++) {
				if (finalState.equals(finalStates.get(j))){
					prob2.set(j, value + prob2.get(j));
					unique2 = false;
					break;
				}				
			}
			
			if (unique) {
				initialStates.add(initialState);
				prob.add(value);
			}
			
			if (unique2) {
				finalStates.add(finalState);
				prob2.add(value);
			}
		}
	}
	
	public List<State> getInitialStates() {
		return initialStates;
	}
	
	public List<State> getFinalStates() {
		return finalStates;
	}
	
	public List<Double> getProb() {
		return prob;
	}
	
	public List<Double> getProb2() {
		return prob2;
	}
}
