package dna;

import java.util.List;

import parser.State;
import parser.ast.ModulesFile;
import prism.PrismComponent;
import prism.PrismException;
import simulator.ModulesFileModelGenerator;

public class DNAModulesFileModelGenerator extends ModulesFileModelGenerator
{
	List<State> initialStates;
	
	public DNAModulesFileModelGenerator(ModulesFile modulesFile, PrismComponent parent, List<State> initialStates)
			throws PrismException {
		super(modulesFile, parent);
		this.initialStates = initialStates;
	}

	@Override
	public State getInitialState() throws PrismException
	{
		return getInitialStates().get(0);
	}
	
	@Override
	public List<State> getInitialStates() throws PrismException
	{
		return initialStates;
	}

}
