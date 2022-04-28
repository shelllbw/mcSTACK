mcSTACK is a prism plug-in for probabilistic model checking of DNA nano-device system

### Getting source
<pre>
git clone https://github.com/nufeb/mcSTACK 
</pre>

### Configuration
mcSTACK requires PRISM model checker which can be downloaded from: https://github.com/prismmodelchecker

We recommand using Eclipse IDE for the Configuration.

1. Open Eclipse
2. Import mcSTACK and PRISM into Eclipse 
3. Right-click on mcSTACK project, select Property -> Jaya Build Path. Add PRISM in the Projects tab
4. To setup 'run configration', add dna.DNAStackModelCheckExpl2SigTime as Main class. Add environment variable, Name: LD_LIBRARY_PATH, Value ../prism/prism/lib
