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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.lang.Math; 

import jdd.JDD;
import jdd.JDDNode;
import dna.DNAModulesFileModelGenerator;
import dna.StateValuesProbEditor;
import explicit.CTMC;
import explicit.CTMCModelChecker;
import explicit.DTMC;
import explicit.DTMCModelChecker;
import parser.State;
import parser.VarList;
import parser.ast.ModulesFile;
import parser.ast.RelOp;
import parser.type.TypeDouble;
import parser.type.TypeInt;
import prism.Model;
import prism.Prism;
import prism.PrismException;
import prism.PrismFileLog;
import prism.PrismLangException;
import prism.PrismLog;
import prism.PrismNotSupportedException;
import prism.StateValues;
import prism.StateValuesDV;
import prism.StateValuesMTBDD;
import prism.StochModelChecker;
import dna.StateValueInitEditor;
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
public class DNAStackModelCheckExplTime
{

	public static void main(String[] args)
	{
		new DNAStackModelCheckExplTime().run();
	}

	public void run()
	{
		// reaction time 
		double time = 1800;
		// # of adding steps
		int n = 11;
		// # of added particles in each step  
		int nParticles = 50;
		// % loss of bead and supernatant
		double mu = 0.1;
		double pi = 0.33;
		double prob = 0.00025;
		//double prob = 0.0005;
		FileWriter myWriter = null;
		FileWriter myWriter2 = null;
		
		FileWriter myWriterprop1 = null;
		FileWriter myWriterprop2 = null;
		FileWriter myWriterprop3 = null;
		FileWriter myWriterprop4 = null;
		FileWriter myWriterprop5 = null;
		FileWriter myWriterprop6 = null;
		FileWriter myWriterprop7 = null;

		try {
			myWriter = new FileWriter("target"+pi+".txt");
			myWriter2 = new FileWriter("non-target"+pi+".txt");
			myWriterprop1 = new  FileWriter("prop2-5min.txt");
			myWriterprop2 = new  FileWriter("prop2-10min.txt");
			myWriterprop3 = new  FileWriter("prop2-15min.txt");
			myWriterprop4 = new  FileWriter("prop2-20min.txt");
			myWriterprop5 = new  FileWriter("prop2-25min.txt");
			myWriterprop6 = new  FileWriter("prop2-3min.txt");
			myWriterprop7 = new  FileWriter("prop2-1min.txt");
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
		String[] props1 = new String[n+1];
		String[] props2 = new String[n+1];
		String[] props3 = new String[n+1];
		String[] props4 = new String[n+1];
		String[] props5 = new String[n+1];
		String[] props6 = new String[n+1];
		
		
////-------------------------add more species (dna-wash-full)-----------------------------------//		
//   //  	 add x, lspx, lsSG, pSG, wSG, 
//		newModels.add("2.lsp_x");
//		newValues.add(new int[]{nParticles, 0, 0, 0, 0});
//		numNewValue[0] = 5;
//		newAdding[0] = 'x';
//		targetSpec.add("lsp");
//		nonSpec.add("null");
//		
//		// add lspxp
//		newModels.add("3.lspx_p");
//		newValues.add(new int[]{0});
//		numNewValue[1] = 1;
//		newAdding[1] = 'p';
//		targetSpec.add("lspx");
//		nonSpec.add("wSG");
//		// add y, lspxpy
//		newModels.add("4.lspxp_y");
//		newValues.add(new int[]{nParticles, 0});
//		numNewValue[2] = 2;
//		newAdding[2] = 'y';
//		targetSpec.add("lspxp");
//		nonSpec.add("pSG");
//		// add r, ry
//		newModels.add("5.lspxpy_r");
//		newValues.add(new int[]{nParticles, 0});
//		numNewValue[3] = 2;
//		newAdding[3] = 'r';
//		targetSpec.add("lspxpy");
//		nonSpec.add("wSG");
//		// add rx, q, pq, TAUr, TAUq
//		newModels.add("6.lspxp_q");
//		newValues.add(new int[]{0, nParticles, 0, 0, 0});
//		numNewValue[4] = 5;
//		newAdding[4] = 'q';
//		targetSpec.add("lspxp");
//		nonSpec.add("null");
//		// add none
//		newModels.add("7.lspx_r");
//		newValues.add(new int[]{});
//		numNewValue[5] = 0;
//		newAdding[5] = 'r';
//		targetSpec.add("lspx");
//		nonSpec.add("TAUq");
//		// add none
//		newModels.add("8.lsp_q");
//		newValues.add(new int[]{});
//		numNewValue[6] = 0;
//		newAdding[6] = 'q';
//		targetSpec.add("lsp");
//		nonSpec.add("TAUr");
//		
//		targetSpec.add("ls");
//		nonSpec.add("TAUq");
//		
//		discard.add("wSG");
//		discard.add("pSG");
//		discard.add("lsSG");
//		discard.add("TAUq");
//		discard.add("TAUr");
//		discard.add("ry");
//		discard.add("rx");
//		discard.add("pq");			

////-------------------------add more species (3signals-xyx)-----------------------------------//		
//   //  	 add x, lspx, lsSG, pSG, wSG, 
//		newModels.add("2.lsp_x");
//		newValues.add(new int[]{nParticles, 0, 0, 0, 0});
//		numNewValue[0] = 5;
//		newAdding[0] = 'x';
//		targetSpec.add("lsp");
//		nonSpec.add("null");
//		// add lspxp
//		newModels.add("3.lspx_p");
//		newValues.add(new int[]{0});
//		numNewValue[1] = 1;
//		newAdding[1] = 'p';
//		targetSpec.add("lspx");
//		nonSpec.add("wSG");
//		// add y, lspxpy
//		newModels.add("4.lspxp_y");
//		newValues.add(new int[]{nParticles, 0});
//		numNewValue[2] = 2;
//		newAdding[2] = 'y';
//		targetSpec.add("lspxp");
//		nonSpec.add("pSG");
//		// add lspxpyp
//		newModels.add("5.lspxpy_p");
//		newValues.add(new int[]{0});
//		numNewValue[3] = 1;
//		newAdding[3] = 'p';
//		targetSpec.add("lspxpy");
//		nonSpec.add("wSG");
//		// add lspxpypx
//		newModels.add("6.lspxpyp_x");
//		newValues.add(new int[]{0});
//		numNewValue[4] = 1;
//		newAdding[4] = 'x';
//		targetSpec.add("lspxpyp");
//		nonSpec.add("pSG");
//		// add r, rx
//		newModels.add("7.lspxpypx_r");
//		newValues.add(new int[]{nParticles, 0});
//		numNewValue[5] = 2;
//		newAdding[5] = 'r';
//		targetSpec.add("lspxpypx");
//		nonSpec.add("wSG");
//		// add ry, q, pq, TAUr, TAUq
//		newModels.add("8.lspxpyp_q");
//		newValues.add(new int[]{0, nParticles, 0, 0, 0});
//		numNewValue[6] = 5;
//		newAdding[6] = 'q';
//		targetSpec.add("lspxpyp");
//		nonSpec.add("null");
//		// add none
//		newModels.add("9.lspxpy_r");
//		newValues.add(new int[]{});
//		numNewValue[7] = 0;
//		newAdding[7] = 'r';
//		targetSpec.add("lspxpy");
//		nonSpec.add("TAUq");
//		// add none
//		newModels.add("10.lspxp_q");
//		newValues.add(new int[]{});
//		numNewValue[8] = 0;
//		newAdding[8] = 'q';
//		targetSpec.add("lspxp");
//		nonSpec.add("TAUr");
//		// add none
//		newModels.add("11.lspx_r");
//		newValues.add(new int[]{});
//		numNewValue[9] = 0;
//		newAdding[9] = 'r';
//		targetSpec.add("lspx");
//		nonSpec.add("TAUq");
//		// add none
//		newModels.add("12.lsp_q");
//		newValues.add(new int[]{});
//		numNewValue[10] = 0;
//		newAdding[10] = 'q';
//		targetSpec.add("lsp");
//		nonSpec.add("TAUr");
//		
//		targetSpec.add("ls");
//		nonSpec.add("TAUq");
//		
//		//discard.add("wSG");
//		//discard.add("pSG");
//		discard.add("lsSG");
//		//discard.add("TAUq");
//		//discard.add("TAUr");
//		discard.add("ry");p
//		discard.add("rx");
//		discard.add("pq");			

//-------------------------add more species (3signals-xyz)-----------------------------------//		
   //  	 add x, lspx, lsSG, pSG, wSG, //78
		//int np = 234;
		int np = 50;
		newModels.add("2.lsp_x");
		newValues.add(new int[]{np, 0, 0, 0, 0});
		numNewValue[0] = 5;
		newAdding[0] = 'x';
		targetSpec.add("lsp");
		nonSpec.add("null");
		varNParticle[0] = np;
		// add lspxp
		//int np1 = 207;
		int np1 = 50;
		newModels.add("3.lspx_p");
		newValues.add(new int[]{0});
		numNewValue[1] = 1;
		newAdding[1] = 'p';
		targetSpec.add("lspx");
		nonSpec.add("wSG");
		varNParticle[1] = np1;
		// add y, lspxpy
		//int np2= 186;
		int np2 = 50;
		newModels.add("4.lspxp_y");
		newValues.add(new int[]{np2, 0});
		numNewValue[2] = 2;
		newAdding[2] = 'y';
		targetSpec.add("lspxp");
		nonSpec.add("pSG");
		varNParticle[2] = np2;
		// add lspxpyp
		//int np3= 165;
		int np3 = 50;
		newModels.add("5.lspxpy_p");
		newValues.add(new int[]{0});
		numNewValue[3] = 1;
		newAdding[3] = 'p';
		targetSpec.add("lspxpy");
		nonSpec.add("wSG");
		varNParticle[3] = np3;
		// add z lspxpypz 
		//int np4= 150;
		int np4= 50;
		newModels.add("6.lspxpyp_z");
		newValues.add(new int[]{np4, 0});
		numNewValue[4] = 2;
		newAdding[4] = 'z';
		targetSpec.add("lspxpyp");
		nonSpec.add("pSG");
		varNParticle[4] = np4;
		// add r, rz
		//int np5= 135;
		int np5= 50;
		newModels.add("7.lspxpypz_r");
		newValues.add(new int[]{np5, 0});
		numNewValue[5] = 2;
		newAdding[5] = 'r';
		targetSpec.add("lspxpypz");
		nonSpec.add("wSG");
		varNParticle[5] = np5;
		// add ry, q, pq, TAUr, TAUq
		//int np6= 123;
		int np6= 50;
		newModels.add("8.lspxpyp_q");
		newValues.add(new int[]{0, np6, 0, 0, 0});
		numNewValue[6] = 5;
		newAdding[6] = 'q';
		targetSpec.add("lspxpyp");
		nonSpec.add("null");
		varNParticle[6] = np6;
		// add rx
		//int np7= 111;
		int np7= 50;
		newModels.add("9.lspxpy_r");
		newValues.add(new int[]{0});
		numNewValue[7] = 1;
		newAdding[7] = 'r';
		targetSpec.add("lspxpy");
		nonSpec.add("TAUq");
		varNParticle[7] = np7;
		// add none
		//int np8= 99;
		int np8= 50;
		newModels.add("10.lspxp_q");
		newValues.add(new int[]{});
		numNewValue[8] = 0;
		newAdding[8] = 'q';
		targetSpec.add("lspxp");
		nonSpec.add("TAUr");
		varNParticle[8] = np8;
		// add none
		//int np9= 87;
		int np9= 50;
		newModels.add("11.lspx_r");
		newValues.add(new int[]{});
		numNewValue[9] = 0;
		newAdding[9] = 'r';
		targetSpec.add("lspx");
		nonSpec.add("TAUq");
		varNParticle[9] = np9;
		// add none
		//int np10= 75;
		int np10= 50;
		newModels.add("12.lsp_q");
		newValues.add(new int[]{});
		numNewValue[10] = 0;
		newAdding[10] = 'q';
		targetSpec.add("lsp");
		nonSpec.add("TAUr");
		varNParticle[10] = np10;
		
		targetSpec.add("ls");
		nonSpec.add("TAUq");
		
		//discard.add("wSG");
		//discard.add("pSG");
		discard.add("lsSG");
		//discard.add("TAUq");
		//discard.add("TAUr");
		discard.add("ry");
		discard.add("rx");
		discard.add("pq");			

		// prop-1 increase production yield
//		props[0] = "P=?[ F[1800,1800] lsp >= 81 ]";
//		props[1] = "P=?[ F[1800,1800] lspx >= 70 ]";
//		props[2] = "P=?[ F[1800,1800] lspxp >= 62 ]";
//		props[3] = "P=?[ F[1800,1800] lspxpy >= 55 ]";
//		props[4] = "P=?[ F[1800,1800] lspxpyp >= 49 ]";
//		props[5] = "P=?[ F[1800,1800] lspxpypz >= 43 ]";
//		props[6] = "P=?[ F[1800,1800] lspxpyp >= 39 ]";
//		props[7] = "P=?[ F[1800,1800] lspxpy >= 34 ]";
//		props[8] = "P=?[ F[1800,1800] lspxp >= 30 ]";			
//		props[9] = "P=?[ F[1800,1800] lspx >= 26 ]";	
//		props[10] = "P=?[ F[1800,1800] lsp >= 22 ]";	
//		props[11] = "P=?[ F[1800,1800] ls >= 19 ]";	
		
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
		
		// prop-2 reduce reaction time 5mins
		props[0] = "P=?[ G>=300 ls = 0 ]";
		props[1] = "P=?[ G>=300 lsp = 0 & p = 0 & pSG = 0 ]";
		props[2] = "P=?[ G>=300 lspx = 0 & x = 0 & wSG = 0 ]";
		props[3] = "P=?[ G>=300 lspxp = 0 & p = 0 & pSG = 0 ]";
		props[4] = "P=?[ G>=300 lspxpy = 0 & y = 0 & wSG = 0 ]";
		props[5] = "P=?[ G>=300 lspxpyp = 0 & p = 0 & pSG = 0 ]";
		props[6] = "P=?[ G>=300 lspxpypz = 0 & z = 0 ]";
		props[7] = "P=?[ G>=300 lspxpyp = 0 & r = 0 & TAUr = 0 ]";
		props[8] = "P=?[ G>=300 lspxpy = 0 & q = 0 & TAUq = 0 ]";			
		props[9] = "P=?[ G>=300 lspxp = 0 & r = 0 & TAUr = 0 ]";	
		props[10] = "P=?[ G>=300 lspx = 0 & q = 0 & TAUq = 0 ]";	
		props[11] = "P=?[ G>=300 lsp = 0 ]";	

		// prop-2 reduce reaction time 10mins
		props1[0] = "P=?[ G[600,1800] ls = 0]";
		props1[1] = "P=?[ G[600,1800] lsp = 0 & p = 0 & pSG = 0 ]";
		props1[2] = "P=?[ G[600,1800] lspx = 0 & x = 0 & wSG = 0  ]";
		props1[3] = "P=?[ G[600,1800] lspxp = 0 & p = 0 & pSG = 0 ]";
		props1[4] = "P=?[ G[600,1800] lspxpy = 0 & y = 0 & wSG = 0 ]";
		props1[5] = "P=?[ G[600,1800] lspxpyp = 0 & p = 0 & pSG = 0 ]";
		props1[6] = "P=?[ G[600,1800] lspxpypz = 0 & z = 0 ]";
		props1[7] = "P=?[ G[600,1800] lspxpyp = 0 & r = 0 & TAUr = 0 ]";
		props1[8] = "P=?[ G[600,1800] lspxpy = 0 & q = 0 & TAUq = 0 ]";			
		props1[9] = "P=?[ G[600,1800] lspxp = 0 & r = 0 & TAUr = 0 ]";	
		props1[10] = "P=?[ G[600,1800] lspx = 0 & q = 0 & TAUq = 0 ]";	
		props1[11] = "P=?[ G[600,1800] lsp = 0 ]";	
		
		// prop-2 reduce reaction time 15mins
		props2[0] = "P=?[ G[900,1800] ls = 0]";
		props2[1] = "P=?[ G[900,1800] lsp = 0 & p = 0 & pSG = 0 ]";
		props2[2] = "P=?[ G[900,1800] lspx = 0 & x = 0 & wSG = 0 ]";
		props2[3] = "P=?[ G[900,1800] lspxp = 0 & p = 0 & pSG = 0 ]";
		props2[4] = "P=?[ G[900,1800] lspxpy = 0 & y = 0 & wSG = 0 ]";
		props2[5] = "P=?[ G[900,1800] lspxpyp = 0 & p = 0 & pSG = 0 ]";
		props2[6] = "P=?[ G[900,1800] lspxpypz = 0 & z = 0 ]";
		props2[7] = "P=?[ G[900,1800] lspxpyp = 0 & r = 0 & TAUr = 0 ]";
		props2[8] = "P=?[ G[900,1800] lspxpy = 0 & q = 0 & TAUq = 0 ]";			
		props2[9] = "P=?[ G[900,1800] lspxp = 0 & r = 0 & TAUr = 0 ]";	
		props2[10] = "P=?[ G[900,1800] lspx = 0 & q = 0 & TAUq = 0 ]";	
		props2[11] = "P=?[ G[900,1800] lsp = 0 ]";	
		
		// prop-2 reduce reaction time 20mins
		props3[0] = "P=?[ G[1200,1800] ls = 0]";
		props3[1] = "P=?[ G[1200,1800] lsp = 0 & p = 0 & pSG = 0 ]";
		props3[2] = "P=?[ G[1200,1800] lspx = 0 & x = 0 & wSG = 0 ]";
		props3[3] = "P=?[ G[1200,1800] lspxp = 0 & p = 0 & pSG = 0 ]";
		props3[4] = "P=?[ G[1200,1800] lspxpy = 0 & y = 0 & wSG = 0 ]";
		props3[5] = "P=?[ G[1200,1800] lspxpyp = 0 & p = 0 & pSG = 0 ]";
		props3[6] = "P=?[ G[1200,1800] lspxpypz = 0 & z = 0 ]";
		props3[7] = "P=?[ G[1200,1800] lspxpyp = 0 & r = 0 & TAUr = 0 ]";
		props3[8] = "P=?[ G[1200,1800] lspxpy = 0 & q = 0 & TAUq = 0 ]";			
		props3[9] = "P=?[ G[1200,1800] lspxp = 0 & r = 0 & TAUr = 0 ]";	
		props3[10] = "P=?[ G[1200,1800] lspx = 0 & q = 0 & TAUq = 0 ]";	
		props3[11] = "P=?[ G[1200,1800] lsp = 0 ]";	
		
		// prop-2 reduce reaction time 25mins
		props4[0] = "P=?[ G[1500,1800] ls = 0]";
		props4[1] = "P=?[ G[1500,1800] lsp = 0 & p = 0 & pSG = 0 ]";
		props4[2] = "P=?[ G[1500,1800] lspx = 0 & x = 0 & wSG = 0 ]";
		props4[3] = "P=?[ G[1500,1800] lspxp = 0 & p = 0 & pSG = 0 ]";
		props4[4] = "P=?[ G[1500,1800] lspxpy = 0 & y = 0 & wSG = 0 ]";
		props4[5] = "P=?[ G[1500,1800] lspxpyp = 0 & p = 0 & pSG = 0 ]";
		props4[6] = "P=?[ G[1500,1800] lspxpypz = 0 & z = 0 ]";
		props4[7] = "P=?[ G[1500,1800] lspxpyp = 0 & r = 0 & TAUr = 0 ]";
		props4[8] = "P=?[ G[1500,1800] lspxpy = 0 & q = 0 & TAUq = 0 ]";			
		props4[9] = "P=?[ G[1500,1800] lspxp = 0 & r = 0 & TAUr = 0 ]";	
		props4[10] = "P=?[ G[1500,1800] lspx = 0 & q = 0 & TAUq = 0 ]";	
		props4[11] = "P=?[ G[1500,1800] lsp = 0 ]";	

		
		// prop-2 reduce reaction time 3mins
		props5[0] = "P=?[ G[180,1800] ls = 0]";
		props5[1] = "P=?[ G[180,1800] lsp = 0 & p = 0 & pSG = 0 ]";
		props5[2] = "P=?[ G[180,1800] lspx = 0 & x = 0 & wSG = 0 ]";
		props5[3] = "P=?[ G[180,1800] lspxp = 0 & p = 0 & pSG = 0 ]";
		props5[4] = "P=?[ G[180,1800] lspxpy = 0 & y = 0 & wSG = 0 ]";
		props5[5] = "P=?[ G[180,1800] lspxpyp = 0 & p = 0 & pSG = 0 ]";
		props5[6] = "P=?[ G[180,1800] lspxpypz = 0 & z = 0 ]";
		props5[7] = "P=?[ G[180,1800] lspxpyp = 0 & r = 0 & TAUr = 0 ]";
		props5[8] = "P=?[ G[180,1800] lspxpy = 0 & q = 0 & TAUq = 0 ]";			
		props5[9] = "P=?[ G[180,1800] lspxp = 0 & r = 0 & TAUr = 0 ]";	
		props5[10] = "P=?[ G[180,1800] lspx = 0 & q = 0 & TAUq = 0 ]";	
		props5[11] = "P=?[ G[180,1800] lsp = 0 ]";
		
		// prop-2 reduce reaction time 1mins
		props6[0] = "P=?[ G[60,1800] ls = 0]";
		props6[1] = "P=?[ G[60,1800] lsp = 0 & p = 0 & pSG = 0 ]";
		props6[2] = "P=?[ G[60,1800] lspx = 0 & x = 0 & wSG = 0 ]";
		props6[3] = "P=?[ G[60,1800] lspxp = 0 & p = 0 & pSG = 0 ]";
		props6[4] = "P=?[ G[60,1800] lspxpy = 0 & y = 0 & wSG = 0 ]";
		props6[5] = "P=?[ G[60,1800] lspxpyp = 0 & p = 0 & pSG = 0 ]";
		props6[6] = "P=?[ G[60,1800] lspxpypz = 0 & z = 0 ]";
		props6[7] = "P=?[ G[60,1800] lspxpyp = 0 & r = 0 & TAUr = 0 ]";
		props6[8] = "P=?[ G[60,1800] lspxpy = 0 & q = 0 & TAUq = 0 ]";			
		props6[9] = "P=?[ G[60,1800] lspxp = 0 & r = 0 & TAUr = 0 ]";	
		props6[10] = "P=?[ G[60,1800] lspx = 0 & q = 0 & TAUq = 0 ]";	
		props6[11] = "P=?[ G[60,1800] lsp = 0 ]";

		
		try {
			// Create a log for PRISM output (hidden or stdout)
			//PrismLog mainLog = new PrismDevNullLog();
			PrismLog mainLog = new PrismFileLog("stdout");

			// Initialise PRISM engine 
			Prism prism = new Prism(mainLog);
			prism.setCUDDMaxMem("10g");
			prism.setEngine(prism.EXPLICIT);		
			prism.initialise();
			
			ModulesFile modulesFile = prism.parseModelFile(new File("prism-examples/DNA-wash-xyz/1.ls_p"));
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
				
				myWriterprop2.append(props1[0] + ": \n");
				myWriterprop2.append(prism.modelCheck(props1[0]).getResultString());
				System.out.println(props1[0] + ": \n");
				System.out.println(prism.modelCheck(props1[0]).getResultString());
				myWriterprop2.append("\n");
				myWriterprop2.flush();
				
				myWriterprop3.append(props2[0] + ": \n");
				myWriterprop3.append(prism.modelCheck(props2[0]).getResultString());
				System.out.println(props2[0] + ": \n");
				System.out.println(prism.modelCheck(props2[0]).getResultString());
				myWriterprop3.append("\n");
				myWriterprop3.flush();
				
				myWriterprop4.append(props3[0] + ": \n");
				myWriterprop4.append(prism.modelCheck(props3[0]).getResultString());
				System.out.println(props3[0] + ": \n");
				System.out.println(prism.modelCheck(props3[0]).getResultString());
				myWriterprop4.append("\n");
				myWriterprop4.flush();
				
				myWriterprop5.append(props4[0] + ": \n");
				myWriterprop5.append(prism.modelCheck(props4[0]).getResultString());
				System.out.println(props4[0] + ": \n");
				System.out.println(prism.modelCheck(props4[0]).getResultString());
				myWriterprop5.append("\n");
				myWriterprop5.flush();
				
				myWriterprop6.append(props5[0] + ": \n");
				myWriterprop6.append(prism.modelCheck(props5[0]).getResultString());
				myWriterprop6.append("\n");
				myWriterprop6.flush();
				
				myWriterprop7.append(props6[0] + ": \n");
				myWriterprop7.append(prism.modelCheck(props6[0]).getResultString());
				myWriterprop7.append("\n");
				myWriterprop7.flush();

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
				
				String model = "prism-examples/DNA-wash-xyz/" + newModels.get(i);
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
					
					myWriterprop2.append(props1[i+1] + ": \n");
					myWriterprop2.append(prism.modelCheck(props1[i+1]).getResultString());
					myWriterprop2.append("\n");
					myWriterprop2.flush();
					
					myWriterprop3.append(props2[i+1] + ": \n");
					myWriterprop3.append(prism.modelCheck(props2[i+1]).getResultString());
					myWriterprop3.append("\n");
					myWriterprop3.flush();
					
					myWriterprop4.append(props3[i+1] + ": \n");
					myWriterprop4.append(prism.modelCheck(props3[i+1]).getResultString());
					myWriterprop4.append("\n");
					myWriterprop4.flush();
					
					myWriterprop5.append(props4[i+1] + ": \n");
					myWriterprop5.append(prism.modelCheck(props4[i+1]).getResultString());
					myWriterprop5.append("\n");
					myWriterprop5.flush();
					
					myWriterprop6.append(props5[i+1] + ": \n");
					myWriterprop6.append(prism.modelCheck(props5[i+1]).getResultString());
					myWriterprop6.append("\n");
					myWriterprop6.flush();
					
					myWriterprop7.append(props6[i+1] + ": \n");
					myWriterprop7.append(prism.modelCheck(props6[i+1]).getResultString());
					myWriterprop7.append("\n");
					myWriterprop7.flush();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				myWriter.close();
				myWriter2.close();
				myWriterprop1.close();
				myWriterprop2.close();
				myWriterprop3.close();				
				myWriterprop4.close();
				myWriterprop5.close();
				
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
	}
	
	private void dump_target(explicit.StateValues probsExpl, double mprob, FileWriter myWriter, String targetSpec, explicit.Model model) {
		VarList varList = model.getVarList();
		int n = varList.getNumVars();
		double total = 0;
		double mean = 0;
		double mean_normal = 0;
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
		
		try {
			myWriter.append(targetSpec+ "=");
			myWriter.append("mean="+mean+"=");
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
