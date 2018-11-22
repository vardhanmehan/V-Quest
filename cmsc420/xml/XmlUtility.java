//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cmsc420.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlUtility {
    private static DocumentBuilder documentBuilder = null;
    private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private static final DefaultHandler defaultHandler = new DefaultHandler();
    private static final SAXParserFactory saxFactory;
    private static SchemaFactory schemaFactory;
    private static final TransformerFactory transformerFactory;
    private static Transformer transformer = null;

    static {
        documentBuilderFactory.setNamespaceAware(true);
        saxFactory = SAXParserFactory.newInstance();
        saxFactory.setValidating(true);
        transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 2);
        schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
    }

    private XmlUtility() {
    }

    public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        if (documentBuilder == null) {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        }

        return documentBuilder;
    }

    public static Transformer getTransformer() throws TransformerConfigurationException {
        if (transformer == null) {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("encoding", "UTF-8");
            transformer.setOutputProperty("indent", "yes");
        }

        return transformer;
    }

    public static boolean isValidXml(File file) {
        try {
            saxFactory.newSAXParser().parse(file, defaultHandler);
            return true;
        } catch (Exception var2) {
            return false;
        }
    }

    public static Document parse(File file) throws ParserConfigurationException, SAXException, IOException {
        return getDocumentBuilder().parse(file);
    }

    public static Document parse(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        return getDocumentBuilder().parse(inputStream);
    }

    public static Reader read(Document document) throws IOException, TransformerException {
        final PipedWriter pipedWriter = new PipedWriter();
        PipedReader pipedReader = new PipedReader(pipedWriter);
        final DOMSource source = new DOMSource(document);
        final StreamResult result = new StreamResult(pipedWriter);
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    XmlUtility.getTransformer().transform(source, result);
                    pipedWriter.close();
                } catch (Exception var2) {
                    throw new RuntimeException(var2);
                }
            }
        };
        (new Thread(runnable)).start();
        return pipedReader;
    }

    public static InputStream stream(Document document) throws IOException, TransformerException {
        final PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
        final DOMSource source = new DOMSource(document);
        final StreamResult result = new StreamResult(pipedOutputStream);
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    XmlUtility.getTransformer().transform(source, result);
                    pipedOutputStream.close();
                } catch (Exception var2) {
                    throw new RuntimeException(var2);
                }
            }
        };
        (new Thread(runnable)).start();
        return pipedInputStream;
    }

    public static void print(Document document) throws TransformerException {
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new OutputStreamWriter(System.out));
        getTransformer().transform(source, result);
    }

    public static void write(Document document, File outFile) throws TransformerException, FileNotFoundException {
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new OutputStreamWriter(new FileOutputStream(outFile)));
        getTransformer().transform(source, result);
    }

    public static void write(Document document, OutputStream outputStream) throws TransformerException {
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new OutputStreamWriter(outputStream));
        getTransformer().transform(source, result);
    }

    public static void write(File xmlFile, File xsltFile, File htmlFile) throws TransformerException, FileNotFoundException {
        StreamSource source = new StreamSource(xmlFile);
        StreamResult result = new StreamResult(new OutputStreamWriter(new FileOutputStream(htmlFile)));
        Transformer xslTransformer = transformerFactory.newTransformer(new StreamSource(xsltFile));
        xslTransformer.setOutputProperty("encoding", "UTF-8");
        xslTransformer.setOutputProperty("indent", "yes");
        xslTransformer.transform(source, result);
    }

    public static Document validate(File xmlFile, Source schemaSource) throws SAXException, IOException, ParserConfigurationException {
        Document document = parse(xmlFile);
        Schema schema = schemaFactory.newSchema(schemaSource);
        Validator validator = schema.newValidator();
        validator.validate(new DOMSource(document));
        return document;
    }

    public static Document validateNoNamespace(File xmlFile) throws SAXException, IOException, ParserConfigurationException {
        return validateNoNamespace((InputStream)(new FileInputStream(xmlFile)));
    }

    public static Document validateNoNamespace(InputStream xmlStream) throws SAXException, IOException, ParserConfigurationException {
        Document document = parse(xmlStream);
        Element root = document.getDocumentElement();
        String schemaFileName = root.getAttribute("xsi:noNamespaceSchemaLocation");
        StreamSource schemaSource;
        if (schemaFileName.startsWith("http://")) {
            URL url = new URL(schemaFileName);
            URLConnection connection = url.openConnection();
            InputStream schemaStream = connection.getInputStream();
            schemaSource = new StreamSource(schemaStream);
        } else {
            schemaSource = new StreamSource(new File(schemaFileName));
        }

        Schema schema = schemaFactory.newSchema(schemaSource);
        Validator validator = schema.newValidator();
        validator.validate(new DOMSource(document));
        return document;
    }
}
