package com.znv.vehicle;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class GetVehicleInfo {

	public static List<String> PicList = new ArrayList<>();

	public List<String> getPicList() {
		return PicList;
	}

	public  List<String> getPicInfo() {
		
		SAXReader reader = new SAXReader();
    	Document document = null;
		try {
			document = reader.read(new File("D:\\result.xml"));
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}  
        Element e = document.getRootElement();
        List<Element> nodes = e.elements("识别结果");
        for (Iterator<Element> it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();    
            Attribute attribute=elm.attribute("图片路径");
            PicList.add(attribute.getText());          
        }  
        
        return PicList;
	}
}
