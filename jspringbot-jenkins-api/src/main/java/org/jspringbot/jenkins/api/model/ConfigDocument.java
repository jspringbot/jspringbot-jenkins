package org.jspringbot.jenkins.api.model;

import org.apache.commons.lang.Validate;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * config.xml wrapper class
 */
public class ConfigDocument {

    public static final String SVN_X_PATH = "//scm/locations/hudson.scm.SubversionSCM_-ModuleLocation/remote";
    public static final String GIT_X_PATH = "//scm/branches/hudson.plugins.git.BranchSpec/name";

    public static final String STRING_PARAM_DEFAULT_VALUE_X_PATH = "//hudson.model.ParametersDefinitionProperty/parameterDefinitions/hudson.model.StringParameterDefinition[%d]/defaultValue";

    public static final String SHELL_SVN_X_PATH = "//builders/hudson.tasks.Shell/command";

    private Document document;

    private boolean modified = false;

    public ConfigDocument(Document document) {
        this.document = document;
    }
    
    private Element getElement(String expression) throws TransformerException {
        return (Element) XPathAPI.selectSingleNode(document, expression);
    }
    
    private Element getSVNPathElement() throws TransformerException {
        Element el = getElement(SVN_X_PATH);

        if(el == null) {
            el = getElement(GIT_X_PATH);
        }

        if(el == null) {
            el = getElement(SHELL_SVN_X_PATH);
        }

        return el;
    }
    
    private Element getStringDefaultParameterElement(int index) throws TransformerException {
        return getElement(String.format(STRING_PARAM_DEFAULT_VALUE_X_PATH, index));
    }
    
    public String getElementTextContent(String expression) throws TransformerException {
        Element el = getElement(expression);

        if(el != null) {
            return el.getTextContent();
        }

        return null;
    }

    public void setElementTextContent(String expression, String value) throws TransformerException {
        setElementTextContent(expression, value, false);
    }

    public void setElementTextContent(String expression, String value, boolean ignoreNotFound) throws TransformerException {
        Element el = getElement(expression);

        if(ignoreNotFound && el == null) {
            return;
        }

        Validate.notNull(el, String.format("Element with expression '%s' not found.", expression));
        modified = true;
        el.setTextContent(value);
    }
    
    public String getStringDefaultParameterValue(int index) throws TransformerException {
        return getElementTextContent(String.format(STRING_PARAM_DEFAULT_VALUE_X_PATH, index));
    }

    public void setStringDefaultParameterValue(int index, String value) throws TransformerException {
        setElementTextContent(String.format(STRING_PARAM_DEFAULT_VALUE_X_PATH, index), value);
    }
    
    public void setSVNPath(String path) throws TransformerException {
        Element el = getSVNPathElement();

        Validate.notNull(el, "SVN path not found");
        modified = true;
        el.setTextContent(path);
    }
    
    public String getSVNPath() throws TransformerException {
        Element el = getSVNPathElement();

        if(el != null) {
            return el.getTextContent();
        }

        return null;
    }

    public boolean isModified() {
        return modified;
    }

    public String toXMLString() throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        StringWriter out = new StringWriter();
        StreamResult outputTarget = new StreamResult(out);
        DOMSource source = new DOMSource(document);

        transformer.transform(source, outputTarget);

        return out.toString();
    }
}
