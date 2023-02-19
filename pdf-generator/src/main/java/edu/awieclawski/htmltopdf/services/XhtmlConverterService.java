package edu.awieclawski.htmltopdf.services;

import com.itextpdf.text.Font;
import com.itextpdf.tool.xml.ElementList;

public interface XhtmlConverterService {

	ElementList convert(String htmlString, Font font);
}
