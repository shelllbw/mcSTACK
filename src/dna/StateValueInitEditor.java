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
import prism.StateAndValueConsumer;

/**
 * StateValueConsumer that formats the state/value pairs
 * and prints them to a PrismLog.
 */
public class StateValueInitEditor implements StateAndValueConsumer
{
	int numNewVar;
	private VarList varList;
	List<State> initialStates;
	List<State> finalState;
	List<Double> prob;
	List<String> discard;
	int numAddedParticles;
	int[] newValue;
	char newAdding;
 	double mu;
 	double pi;
 	
	public StateValueInitEditor(int numNewVar, VarList varList, int[] newValue, int numAddedParticles, double mu, double pi, char newAdding, ArrayList<String> discard)
	{
		this.numNewVar = numNewVar;
		this.varList = varList;
		this.numAddedParticles = numAddedParticles;
		this.mu = mu;
		this.pi = pi;
		this.newAdding = newAdding;
		this.newValue = newValue;
		this.discard = discard;
		
		initialStates = new ArrayList<State>();
		finalState =  new ArrayList<State>();
		prob = new ArrayList<Double>();
	}
	
	@Override
	public void accept(int[] varValues, double value, long stateIndex) {
		// TODO Auto-generated method stub
		
		if (value > 0.001) {
			int n = varList.getNumVars();
			State initialState = new State(n+numNewVar);
			
			for (int i = 0; i < n; i++) {
				// integer variable
				if (varList.getType(i) instanceof TypeInt) {
					if (varList.getName(i).charAt(0) == 'l'){
						int v = (int)Math.round(varValues[i]*(1-mu));
						initialState.setValue(i, v);
					} else {  
						if (varList.getName(i).charAt(0) == newAdding && varList.getName(i).length() == 1) {
							initialState.setValue(i, numAddedParticles+(int)varValues[i]);
						} else {
							int v = (int)Math.round(varValues[i]*pi);
							initialState.setValue(i, v);
						}
					}
					
					for (int k = 0; k < discard.size(); k++) {
						if (varList.getName(i).equals(discard.get(k))) {
							initialState.setValue(i, 0);
							break;
						}
					}
				}
			}
			
			
			for (int i = n; i < n + numNewVar; i++) {
				initialState.setValue(i, newValue[i-n]);
			}
			
			boolean unique = true;
			for (int i = 0; i < initialStates.size(); i++) {
				if (initialState.equals(initialStates.get(i))){
					prob.set(i, value + prob.get(i));
					unique = false;
					break;
				}				
			}
			
			if (unique) {
				initialStates.add(initialState);
				prob.add(value);
			}
			
			// error correction
			int error = 0;
			System.out.println("newAdding = " + newAdding);
			if (newAdding == 'p') {
				for (int j = 0; j < n; j++) {
					// integer variable
					if (varList.getType(j) instanceof TypeInt) {
						if ((varList.getName(j).charAt(0) == 'x' || varList.getName(j).charAt(0) == 'y') && varList.getName(j).length() == 1){
							error = (int)varValues[j] > 0 ? (int)varValues[j] - 1 : 0;
						}
					}
				}
				
				for (int j = 0; j < n; j++) {
					// integer variable
					if (varList.getType(j) instanceof TypeInt) {
						if (varList.getName(j).charAt(0) == newAdding  && varList.getName(j).length() == 1){
							initialState.setValue(j, (int)varValues[j] - error);
						}
					}
				}				
			} else if (newAdding == 'x' || newAdding == 'y') {
				for (int j = 0; j < n; j++) {
					// integer variable
					if (varList.getType(j) instanceof TypeInt) {
						if (varList.getName(j).charAt(0) == 'p' || varList.getName(j).length() == 1){
							error = (int)varValues[j] > 0 ? (int)varValues[j] - 1 : 0;
						}
					}
				}
				
				for (int j = 0; j < n; j++) {
					// integer variable
					if (varList.getType(j) instanceof TypeInt) {
						if (varList.getName(j).charAt(0) == newAdding  && varList.getName(j).length() == 1){
							initialState.setValue(j, (int)varValues[j] - error);
						}
					}
				}	
			}
		}
	}
	
	public List<State> getInitialStates() {
		return initialStates;
	}
	
	public List<Double> getProb() {
		return prob;
	}
}
