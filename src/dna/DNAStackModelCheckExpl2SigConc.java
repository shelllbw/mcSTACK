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

package dna;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.lang.Math; 

import explicit.CTMC;
import explicit.CTMCModelChecker;
import parser.State;
import parser.VarList;
import parser.ast.ModulesFile;
import parser.ast.RelOp;
import parser.type.TypeDouble;
import parser.type.TypeInt;
import prism.Prism;
import prism.PrismException;
import prism.PrismFileLog;
import prism.PrismLangException;
import prism.PrismLog;
import dna.StateValueInitEditorExpl;


/**
 * An example class demonstrating how to control PRISM programmatically,
 * through the functions exposed by the class prism.Prism.
 * 
 * This shows how to load a model from a file and model check some properties,
 * either from a file or specified as a string, and possibly involving constants. 
 * 
 * See the README for how to link this to PRISM.
*/
public class DNAStackModelCheckExpl2SigConc
{

	public static void main(String[] args)
	{
		new DNAStackModelCheckExpl2SigConc().run();
	}

	public void run()
	{
		long start = System.currentTimeMillis();
		// reaction time 
		double time = 1800;
		// # of adding steps
		int n = 7;
		// # of added particles in each step  
		int nParticles = 100;
		// % loss of bead and supernatant
		double mu = 0.1;
		double pi = 0.2;
		double prob = 0.00001;
		//double prob = 0.0005;
		FileWriter myWriter = null;
		FileWriter myWriter2 = null;
		
		FileWriter myWriterprop1 = null;

		try {
			myWriter = new FileWriter("target"+pi+".txt");
			myWriter2 = new FileWriter("non-target"+pi+".txt");
			myWriterprop1 = new  FileWriter("prop1-conc.txt");
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		ArrayList<String> newModels = new ArrayList<String>(n);
		ArrayList<String> discard = new ArrayList<String>(n);
		ArrayList<int[]> newValues = new ArrayList<int[]>(n);
		int[] varNParticle =  new int[n];
		ArrayList<String> targetSpec = new ArrayList<String>(n+1);
		ArrayList<String> nonSpec = new ArrayList<String>(n+1);
		char[] newAdding = new char[n];
		int[] numNewValue = new int[n];
		
		String[] props = new String[n+1];

		
////-------------------------add more species (dna-wash-full)-----------------------------------//		
//   //  	 add x, lspx, lsSG, pSG, wSG, 
		int np=78;
		newModels.add("2.lsp_x");
		newValues.add(new int[]{np, 0, 0, 0, 0});
		numNewValue[0] = 5;
		newAdding[0] = 'x';
		targetSpec.add("lsp");
		nonSpec.add("null");
		varNParticle[0] = np;		
		// add lspxp
		int np2=68;
		newModels.add("3.lspx_p");
		newValues.add(new int[]{0});
		numNewValue[1] = 1;
		newAdding[1] = 'p';
		targetSpec.add("lspx");
		nonSpec.add("wSG");
		varNParticle[1] = np2;
		// add y, lspxpy
		int np3=61;
		newModels.add("4.lspxp_y");
		newValues.add(new int[]{np3, 0});
		numNewValue[2] = 2;
		newAdding[2] = 'y';
		targetSpec.add("lspxp");
		nonSpec.add("pSG");
		varNParticle[2] = np3;
		// add r, ry
		int np4=55;
		newModels.add("5.lspxpy_r");
		newValues.add(new int[]{np4, 0});
		numNewValue[3] = 2;
		newAdding[3] = 'r';
		targetSpec.add("lspxpy");
		nonSpec.add("wSG");
		varNParticle[3] = np4;
		// add rx, q, pq, TAUr, TAUq
		int np5=51;
		newModels.add("6.lspxp_q");
		newValues.add(new int[]{0, np5, 0, 0, 0});
		numNewValue[4] = 5;
		newAdding[4] = 'q';
		targetSpec.add("lspxp");
		nonSpec.add("null");
		varNParticle[4] = np5;
		// add none
		int np6=45;
		newModels.add("7.lspx_r");
		newValues.add(new int[]{});
		numNewValue[5] = 0;
		newAdding[5] = 'r';
		targetSpec.add("lspx");
		nonSpec.add("TAUq");
		varNParticle[5] = np6;
		// add none
		int np7=40;
		newModels.add("8.lsp_q");
		newValues.add(new int[]{});
		numNewValue[6] = 0;
		newAdding[6] = 'q';
		targetSpec.add("lsp");
		nonSpec.add("TAUr");
		varNParticle[6] = np7;
		
		targetSpec.add("ls");
		nonSpec.add("TAUq");
		
//		discard.add("wSG");
//		discard.add("pSG");
		discard.add("lsSG");
//		discard.add("TAUq");
//		discard.add("TAUr");
		discard.add("ry");
		discard.add("rx");
		discard.add("pq");				

		// prop-1 increase production yield
		props[0] = "P=?[ F[1800,1800] lsp >= 81 ]";
		props[1] = "P=?[ F[1800,1800] lspx >= 70 ]";
		props[2] = "P=?[ F[1800,1800] lspxp >= 60 ]";
		props[3] = "P=?[ F[1800,1800] lspxpy >= 54 ]";
		props[4] = "P=?[ F[1800,1800] lspxp >= 49 ]";
		props[5] = "P=?[ F[1800,1800] lspx >= 43 ]";
		props[6] = "P=?[ F[1800,1800] lsp >= 38 ]";
		props[7] = "P=?[ F[1800,1800] ls >= 33 ]";
		
//		props[0] = "P=?[ F[0,600] (ls = 0)]";
//		props[1] = "P=?[ F[0,600] (lsp = 0 & pSG <= 1 & p <= 1) ]";
//		props[2] = "P=?[ F[0,600] (lspx = 0 & wSG <= 1 & x <= 1) ]";
//		props[3] = "P=?[ F[0,600] (lspxp = 0 & pSG <= 1 & p <= 1) ]";
//		props[4] = "P=?[ F[0,600] (lspxpy = 0 & wSG <= 1 & y <= 1) ]";
//		props[5] = "P=?[ F[0,600] (lspxpyp = 0 & pSG <= 1 & p <= 1) ]";
//		props[6] = "P=?[ F[0,600] ls = 0 ]";
//		props[7] = "P=?[ F[0,600] ls = 0 ]";
//		props[8] = "P=?[ F[0,600] ls = 0 ]";
//		props[9] = "P=?[ F[0,600] ls = 0 ]";
//		props[10] = "P=?[ F[0,600] ls = 0 ]";	
//		props[11] = "P=?[ F[0,600] ls = 0 ]";		
		
		try {
			// Create a log for PRISM output (hidden or stdout)
			//PrismLog mainLog = new PrismDevNullLog();
			PrismLog mainLog = new PrismFileLog("stdout");

			// Initialise PRISM engine 
			Prism prism = new Prism(mainLog);
			prism.setCUDDMaxMem("10g");
			prism.setEngine(prism.EXPLICIT);		
			prism.initialise();
			
			ModulesFile modulesFile = prism.parseModelFile(new File("prism-examples/DNA-wash-full/1.ls_p"));
			// Parse and load a PRISM model from a file
			prism.loadPRISMModel(modulesFile);
			// Build CTMC model
			prism.buildModel();
			explicit.Model currentModelExpl = prism.getBuiltModelExplicit();
		    // Compute transient probability
			CTMCModelChecker mcCTMC = new CTMCModelChecker(prism);
			explicit.StateValues probsExpl = mcCTMC.doTransient((CTMC) currentModelExpl, time);
			probsExpl.printFiltered(mainLog, probsExpl.getBitSetFromInterval(RelOp.GT, prob));
			
			// model checking
			try {
				myWriterprop1.append(props[0] + ": \n");
				myWriterprop1.append(prism.modelCheck(props[0]).getResultString());
				System.out.println(props[0] + ": \n");
				System.out.println(prism.modelCheck(props[0]).getResultString());
				myWriterprop1.append("\n");
				myWriterprop1.flush();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//--------------------------------Next N step------------------------------------//
			for (int i = 0; i < n; i++) {
				double rem = 1 - mu;
				double bm = Math.pow(rem,i+2);
				mainLog.println("Bead mass remaining:  " +  bm);
				double vpi = pi * bm;
			    StateValueInitEditorExpl consumerI = new StateValueInitEditorExpl(probsExpl, numNewValue[i], currentModelExpl, newValues.get(i), nParticles, mu, vpi, newAdding[i], discard, targetSpec.get(i), prob, varNParticle[i]);
			    consumerI.setter();
				dump_target(probsExpl, prob, myWriter, targetSpec.get(i), currentModelExpl);
				dump_target(probsExpl, prob, myWriter2, nonSpec.get(i), currentModelExpl);
			    List <State> initialStates = consumerI.getInitialStates();
			    List <Double> initialProb = consumerI.getProb();
			    
				double totalProb = 0;
				for (int j = 0; j < initialProb.size(); j++) {
					totalProb += initialProb.get(j);
				}
				
				System.out.println("New initial states of model: " + newModels.get(i));
				for (int j = 0; j < initialStates.size(); j++) {
					System.out.println(initialStates.get(j).toString() + ": " + initialProb.get(j));
				}
				
				System.out.println();
				System.out.println("Total Probability: " + totalProb + "%");
				
				String model = "prism-examples/DNA-wash-full/" + newModels.get(i);
				ModulesFile newModulesFile = prism.parseModelFile(new File(model));
				DNAModulesFileModelGenerator modelGen = new DNAModulesFileModelGenerator(newModulesFile, prism, initialStates);
				
				mainLog.print("Load Module: ");
				mainLog.print(model);
				mainLog.println();
				
				prism.loadModelGenerator(modelGen);
			    prism.buildModel();
			    currentModelExpl = prism.getBuiltModelExplicit();
			    
			    explicit.StateValues initDist = setInitDist(initialProb, currentModelExpl);
			    
			    System.out.println("Initial Dist (" + totalProb + "%):");
				mainLog.println();
				mainLog.print("Variables: ");
				for (int j = 0; j < newModulesFile.getNumVars(); j++) {
					mainLog.print(newModulesFile.getVarName(j) + " ");
				}
				mainLog.println();
				initDist.print(mainLog);

			    // Compute transient probability
				mcCTMC = new CTMCModelChecker(prism);
				probsExpl = mcCTMC.doTransient((CTMC) currentModelExpl, time, initDist);
				
				probsExpl.printFiltered(mainLog, probsExpl.getBitSetFromInterval(RelOp.GT, prob));
				if (i == n-1) {
					dump_target(probsExpl, prob, myWriter, targetSpec.get(i+1), currentModelExpl);
					dump_target(probsExpl, prob, myWriter2, nonSpec.get(i+1), currentModelExpl);
				}
				
				try {
					myWriterprop1.append(props[i+1] + ": \n");
					myWriterprop1.append(prism.modelCheck(props[i+1]).getResultString());
					myWriterprop1.append("\n");
					myWriterprop1.flush();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				myWriter.close();
				myWriter2.close();
				myWriterprop1.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Close down PRISM
			prism.closeDown();

		} catch (FileNotFoundException e) {
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
		} catch (PrismException e) {
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
		}
		long finish = System.currentTimeMillis();
		long timeElapsed = finish - start;
		System.out.println("time = "+ timeElapsed);
	}
	
	private void dump_target(explicit.StateValues probsExpl, double mprob, FileWriter myWriter, String targetSpec, explicit.Model model) {
		VarList varList = model.getVarList();
		int n = varList.getNumVars();
		double total = 0;
		double mean = 0;
		double variance = 0;
		double mean_normal = 0;
		double variance_normal = 0;
		Map<Integer, Double> result = new TreeMap<Integer, Double>();
		
		for (int i = 0; i < probsExpl.getSize(); i++) {
			double value = (double)probsExpl.getValue(i);
			State state = model.getStatesList().get(i);
			if (value < mprob) continue;
			total += value;
			for (int j = 0; j < n; j++) {
				// integer variable
				if (varList.getType(j) instanceof TypeInt) {					
					if (varList.getName(j).equals(targetSpec)){
						int v = (int)state.varValues[j];
						if (result.containsKey(v))
							result.put(v, result.get(v) + value);
						else
							result.put(v, value);
					} 			
				}
			}
		}
		
		for (int i = 0; i < result.keySet().size(); i++) {
			int key = (int) result.keySet().toArray(new Object[result.size()])[i];
			double value = result.get(key);
			mean += value*key;
		}
		
		for (int i = 0; i < result.keySet().size(); i++) {
			int key = (int) result.keySet().toArray(new Object[result.size()])[i];
			double value = result.get(key);
			variance += (mean-key)*(mean-key)*value;
		}
		
		mean_normal = mean/total;
		variance_normal = variance/total;
		
		try {
			myWriter.append(targetSpec+ "=");
			myWriter.append("mean="+mean+"=");
			myWriter.append("variance="+variance+"=");
			myWriter.append("mnormal="+mean_normal+"=");
			myWriter.append("vnormal="+variance_normal+"=");
			myWriter.append(result.toString());
			myWriter.append("="+ total);
			myWriter.append("\n");
			myWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private explicit.StateValues setInitDist (List<Double> initialProb, explicit.Model model){
		explicit.StateValues dist = null;

		// Build an empty vector 
		try {
			dist = new explicit.StateValues(TypeDouble.getInstance(), model);
		} catch (PrismLangException e) {
			e.printStackTrace();
		}

		int i = 0;
		for (int in : model.getInitialStates()) {
			dist.setDoubleValue(in, initialProb.get(i));
			i++;
		}

		return dist;
	}
}
