package supersql.codegenerator.HTML_Flexbox;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import supersql.codegenerator.Connector;
import supersql.codegenerator.ITFE;
import supersql.codegenerator.Manager;
import supersql.common.GlobalEnv;
import supersql.extendclass.ExtList;

public class HTML_FlexboxC2 extends Connector {

	/** @deprecated use HTMLC2() instead **/
	@Deprecated
	public HTML_FlexboxC2(Manager manager, HTML_FlexboxEnv henv, HTML_FlexboxEnv henv2) {
		Dimension = 2;
	}
	
	public HTML_FlexboxC2(){
		Dimension = 2;
	}

	public Element createTableNode(ExtList<ExtList<String>> dataInfo) {
		this.setDataList(dataInfo);

		Element result = new Element(Tag.valueOf("table"), "");
		result = nodeCreationPreProcesss(result, dataInfo);
		if(result.tagName().equalsIgnoreCase("form"))
			return result;
		int i = 0;
		while (this.hasMoreItems()) {
			ITFE tfe = (ITFE) tfes.get(i);
			result.addClass(HTML_FlexboxEnv.getClassID(tfe)).addClass("nest");
			HTML_FlexboxEnv.getClassID(tfe);

			result.appendElement("tr").appendElement("td")
					.appendChild((Element) this.createNextItemNode(dataInfo));

			i++;
		}
		
		return result;
	}
	
	private Element nodeCreationPreProcesss(Element result, ExtList<ExtList<String>> dataInfo){
		if (decos.containsKey("layout")
				&& decos.getStr("layout").equalsIgnoreCase("standard")
				|| GlobalEnv.getLayout().equalsIgnoreCase("standard"))
    		result.addClass("vertical_display");
    	else
    		result.addClass("vertical");

		if (!GlobalEnv.isOpt()) {
			if (!HTML_FlexboxEnv.isOutlineMode()) {
				result.attr("frame", "void");
			}
				result.attr("class", HTML_FlexboxEnv.getClassID(this));

			if (decos.containsKey("class")) {
				result.attr("class",
						result.attr("class") + " " + decos.getStr("class"));
			}
		}
		HTML_FlexboxUtils.processDecos(result, decos);
		if (decos.containsKey("insert"))
			return createForm(dataInfo, 0, result);
		if (decos.containsKey("delete"))
			return createForm(dataInfo, 1, result);
		if (decos.containsKey("update"))
			return createForm(dataInfo, 2, result);
		if(decos.containsKey("login"))
    		return JsoupFactory.createLoginForm(this, result);
    	return result;
	}

	@Override
	public Element createNode(ExtList<ExtList<String>> dataInfo) {
		if ((GlobalEnv.getLayout().equalsIgnoreCase("table") && !decos
				.containsKey("layout"))
				|| (decos.containsKey("layout") && decos.getStr("layout")
						.equalsIgnoreCase("table")))
    		return createTableNode(dataInfo);
		this.setDataList(dataInfo);

		Element result = new Element(Tag.valueOf("div"), "");
		result.addClass("con2").addClass("box");
		result = nodeCreationPreProcesss(result, dataInfo);
		if(result.tagName().equalsIgnoreCase("form"))
			return result;
		int i = 0;

		while (this.hasMoreItems()) {
			ITFE tfe = (ITFE) tfes.get(i);
			result.addClass(HTML_FlexboxEnv.getClassID(tfe)).addClass("nest");

			HTML_FlexboxEnv.getClassID(tfe);

			result.appendChild((Element) this.createNextItemNode(dataInfo));

			i++;
		}

		return result;
	}

	private Element createForm(ExtList<ExtList<String>> dataInfo, int type, Element result) {
		String inputType;
		String submitText;
		String inputValue;
		String formContentClass;
		String formContentElementsClass;
		if (type == 0) {
			inputType = "text";
			inputValue = "";
			submitText = "insert";
			formContentClass = "vertical con2";
			formContentElementsClass = "horizontal";
		} else if (type == 1) {
			inputType = "checkbox";
			inputValue = "1";
			submitText = "delete";
			formContentClass = "vertical con2";
			formContentElementsClass = "horizontal";
		} else if (type == 2) {
			inputType = "text";
			inputValue = null;
			submitText = "update";
			formContentClass = "vertical con2";
			formContentElementsClass = "horizontal";
		} else {
			throw new IllegalArgumentException();
		}
		return JsoupFactory.createForm(this, inputType, submitText, inputValue,
				formContentClass, formContentElementsClass, result);

	}

	public String getSymbol() {
		return "HTMLC2";
	}

}
