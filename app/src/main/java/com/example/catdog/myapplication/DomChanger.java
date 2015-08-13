package com.example.catdog.myapplication;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by MyeongJun on 2015. 8. 7..
 */
public class DomChanger {

    public static Document byteToDom(byte[] byteArray) throws Exception {
        DocumentBuilder builder;
        DocumentBuilderFactory factory;
        Document document;

        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();

        document = builder.parse(new ByteArrayInputStream(byteArray));
        document.getDocumentElement().normalize();
        return document;
    }

    public static Document stringToDom(String str) throws Exception{
        DocumentBuilder builder;
        DocumentBuilderFactory factory;
        Document document;

        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();

        document = builder.parse(new ByteArrayInputStream(str.getBytes()));
        document.getDocumentElement().normalize();
        return document;
    }

    public static Document byteArrayOutputStreamToDom(ByteArrayOutputStream baos) throws Exception {
        DocumentBuilder builder;
        DocumentBuilderFactory factory;
        Document document;

        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();

        document = builder.parse(new ByteArrayInputStream(baos.toByteArray()));
        document.getDocumentElement().normalize();
        return document;
    }
}
