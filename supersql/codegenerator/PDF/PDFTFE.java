package supersql.codegenerator.PDF;

import supersql.codegenerator.ITFE;

public abstract interface PDFTFE {
	
	public PDFValue getInstance();
	//public void setMaxWidth(PDFValue result);
	//public void debug(PDFValue result);
	//public void adjust(PDFValue result);
	public void setLabel(PDFValue result);
	
	public boolean optimizeW(float Dexcess, PDFValue result);
	public boolean optimizeH(float Dexcess, PDFValue result);
	public ITFE getNewChild();
	public boolean changeORnot();
	public void redoChange();

	public void restoreFOLD(PDFValue check);
}

