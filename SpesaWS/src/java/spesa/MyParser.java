/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spesa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Ziz
 */
public class MyParser {

    private List liste;

    public MyParser() {
        liste = new ArrayList();
    }

    public List parseDocument(String filename) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document document;
        Element root, element;
        NodeList nodelist;
        Lista lista;
        // creazione dell’albero DOM dal documento XML
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        document = builder.parse(filename);

        root = document.getDocumentElement();
        // generazione della lista degli elementi "table"        
        nodelist = root.getElementsByTagName("lista");
        if (nodelist != null && nodelist.getLength() > 0) {
            for (int i = 0; i < nodelist.getLength(); i++) {
                // solo la prima table contiene cio che mi interessa
                element = (Element) nodelist.item(i);
                lista = getLista(element);
                liste.add(lista);
            }
        }

        return liste;
    }

    private Element getSimpleChild(Element parentElement, String childElementName) {
        NodeList nodelist = parentElement.getElementsByTagName(childElementName);
        return (Element) nodelist.item(0);
    }

    private NodeList getComplexChild(Element parentElement, String childElementName) {
        NodeList nodelist = parentElement.getElementsByTagName(childElementName);
        return nodelist;
    }

    private Lista getLista(Element element1) {
        Lista lista = null;
        try {
            int rifRichiesta = MyLibXML.getIntValue(element1, "rifRichiesta");
            int rifProdotto = MyLibXML.getIntValue(element1, "rifProdotto");
            int quantita = MyLibXML.getIntValue(element1, "quantita");

            lista = new Lista(rifRichiesta, rifProdotto, quantita);


        } catch (Exception ex) {
            
        }
        return lista;

    }

}
