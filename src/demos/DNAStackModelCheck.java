//==============================================================================
//	
//	Copyright (c) 2017-
//	Authors:
//	* Dave Parker <d.a.parker@cs.bham.ac.uk> (University of Birmingham)
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

package demos;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import jdd.JDD;
import jdd.JDDNode;
import dna.DNAModulesFileModelGenerator;
import dna.StateValuesProbEditor;
import parser.State;
import parser.ast.ModulesFile;
import prism.Model;
import prism.Prism;
import prism.PrismException;
import prism.PrismFileLog;
import prism.PrismLog;
import prism.PrismNotSupportedException;
import prism.StateValues;
import prism.StateValuesDV;
import prism.StateValuesMTBDD;
import prism.StochModelChecker;
import dna.StateValueInitEditor;


/**
 * An example class demonstrating how to control PRISM programmatically,
 * through the functions exposed by the class prism.Prism.
 * 
 * This shows how to load a model from a file and model check some properties,
 * either from a file or specified as a string, and possibly involving constants. 
 * 
 * See the README for how to link this to PRISM.
*/
public class DNAStackModelCheck
{

	public static void main(String[] args)
	{
		new DNAStackModelCheck().run();
	}

	public void run()
	{
		// reaction time 
		double time = 1800;
		// # of adding steps
		int n = 7;
		// # of added particles in each step  
		int nParticles = 30;
		// % lss of sepharose bead mass
		double mu = 0.02;
		ArrayList<String> newModels = new ArrayList<String>(n);
		ArrayList<int[]> newValues = new ArrayList<int[]>(n);
		char[] newAdding = new char[n];
		int[] numNewValue = new int[n];
		// add x, lspx
		newModels.add("2.lsp_x");
		newValues.add(new int[]{nParticles, 0});
		numNewValue[0] = 2;
		newAdding[0] = 'x';
		// add lspx
		newModels.add("3.lspx_p");
		newValues.add(new int[]{0});
		numNewValue[1] = 1;
		newAdding[1] = 'p';
		// add y, lspy, lspxpy
		newModels.add("4.lspxp_y");
		newValues.add(new int[]{nParticles, 0, 0});
		numNewValue[2] = 3;
		newAdding[2] = 'y';
		// add r, rx, ry
		newModels.add("5.lspxpy_r");
		newValues.add(new int[]{nParticles, 0, 0});
		numNewValue[3] = 3;
		newAdding[3] = 'r';
		// add q, pq
		newModels.add("6.lspxp_q");
		newValues.add(new int[]{nParticles, 0});
		numNewValue[4] = 2;
		newAdding[4] = 'q';
		// add r
		newModels.add("7.lspx_r");
		newValues.add(new int[]{});
		numNewValue[5] = 0;
		newAdding[5] = 'r';
		// add q
		newModels.add("8.lsp_q");
		newValues.add(new int[]{});
		numNewValue[6] = 0;
		newAdding[6] = 'q';
	
		try {
			// Create a log for PRISM output (hidden or stdout)
			//PrismLog mainLog = new PrismDevNullLog();
			PrismLog mainLog = new PrismFileLog("stdout");

			// Initialise PRISM engine 
			Prism prism = new Prism(mainLog);
			prism.initialise();

			ModulesFile modulesFile = prism.parseModelFile(new File("prism-examples/DNA-allwash/1.ls_p"));
			// Parse and load a PRISM model from a file
			prism.loadPRISMModel(modulesFile);
			// Build CTMC model
			prism.buildModel();
		    Model currentModel = prism.getBuiltModel();
		    // Compute transient probability
		    StochModelChecker mc = new StochModelChecker(prism, currentModel, null);
		    StateValues prob = ((StochModelChecker) mc).doTransient(time);
			
			//--------------------------------Next N step------------------------------------//
			for (int i = 0; i < n; i++) {
			    StateValueInitEditor consumerI = new StateValueInitEditor(numNewValue[i], currentModel.getVarList(), newValues.get(i), nParticles, mu, newAdding[i]);
			    prob.iterate(consumerI, true);
			    List <State> initialStates = consumerI.getInitialStates();
			    List <Double> initialProb = consumerI.getProb();
			    
				double totalProb = 0;
				for (int j = 0; j < initialProb.size(); j++) {
					totalProb += initialProb.get(j);
				}
				
				String model = "prism-examples/DNA-allwash/" + newModels.get(i);
				ModulesFile newModulesFile = prism.parseModelFile(new File(model));
				DNAModulesFileModelGenerator modelGen = new DNAModulesFileModelGenerator(newModulesFile, prism, initialStates);
				
				prism.loadModelGenerator(modelGen);
			    prism.buildModel();
			    currentModel = prism.getBuiltModel();
			    
			    StateValues probDist = null;
			    probDist = InitDist(probDist, currentModel, prism);
			    
			    
			    StateValuesProbEditor consumerP = new StateValuesProbEditor ();
			    probDist.iterate(consumerP, true);
			    
			    setInitDist(probDist, initialProb, consumerP.getIndex());
			    System.out.println("Initial Dist (" + totalProb + "%):");
				mainLog.println();
				mainLog.print("Variables: ");
				for (int j = 0; j < newModulesFile.getNumVars(); j++) {
					mainLog.print(newModulesFile.getVarName(j) + " ");
				}
				mainLog.println();
			    //probDist.print(mainLog);

			    // Compute transient probability
			    mc = new StochModelChecker(prism, currentModel, null);
			    prob = ((StochModelChecker) mc).doTransient(time, probDist);
			}


		    //prob1.print(mainLog);
		    
			// Close down PRISM
			prism.closeDown();

		} catch (FileNotFoundException e) {
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
		} catch (PrismException e) {
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
		}
	}
	
	private StateValues InitDist(StateValues initDist, Model model, Prism prism) throws PrismException
	{
		// mtbdd stuff
		JDDNode start, init;
		// other stuff
		StateValues initDistNew = null;

		// build initial distribution (if not specified)
		if (initDist == null) {
			// first construct as MTBDD
			// get initial states of model
			start = model.getStart();
			// compute initial probability distribution (equiprobable over all start states)
			JDD.Ref(start);
			init = JDD.Apply(JDD.DIVIDE, start, JDD.Constant(JDD.GetNumMinterms(start, model.getAllDDRowVars().n())));
			// if using MTBDD engine, distribution needs to be an MTBDD
			if (prism.getEngine() == Prism.MTBDD) {
				initDistNew = new StateValuesMTBDD(init, model);
			}
			// for sparse/hybrid engines, distribution needs to be a double vector
			else {
				initDistNew = new StateValuesDV(init, model);
				JDD.Deref(init);
			}
		} else {
			initDistNew = initDist;
		}

		return initDistNew;
	}
	
	private void setInitDist (StateValues initialDist, List<Double> initialProb, List<Integer> index){
		for (int i = 0; i < initialProb.size(); i++) {
			if (initialDist instanceof StateValuesMTBDD) {
					try {
						((StateValuesMTBDD)initialDist).setElement(index.get(i), initialProb.get(i));
					} catch (PrismNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			} else if (initialDist instanceof StateValuesDV) {
				((StateValuesDV)initialDist).getDoubleVector().setElement(index.get(i), initialProb.get(i));
			}
		}
	}
}