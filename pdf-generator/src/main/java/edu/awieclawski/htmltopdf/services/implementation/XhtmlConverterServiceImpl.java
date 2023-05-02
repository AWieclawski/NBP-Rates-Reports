package edu.awieclawski.htmltopdf.services.implementation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.WritableElement;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

import edu.awieclawski.htmltopdf.defaults.PdfDefaults;
import edu.awieclawski.htmltopdf.services.XhtmlConverterService;
import edu.awieclawski.htmltopdf.tools.HtmlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
final class XhtmlConverterServiceImpl extends PdfDefaults implements XhtmlConverterService {
	private final HtmlUtils htmlUtils;
	private static final Paragraph ERROR_PARAGRAPH = new Paragraph("No document element found!",
			new Font(BASE_PL_FONT, Font.NORMAL, Font.DEFAULTSIZE));

	@Value("${font.path}")
	private String fontFullPath;

	@Value("${font.size}")
	private Integer fonfSize;

	@Value("${font.name}")
	private String fontName;

	@Value("${html.css}")
	private String cssPath;

	// values set after Spring context is built
	private Font defaultFont;
	private Font normalFont;
	private Font boldFont;
	private Font boldItalicFont;
	private Font italicFont;
	private Font underlineFont;
	private CssFile cssFile;

	@PostConstruct
	private void preSetInnerFonts() {
		if (fontFullPath != null && fonfSize != null) {
			normalFont = new Font(getFontFromPath(fontFullPath, fontName), fonfSize, Font.NORMAL);
			boldFont = new Font(getFontFromPath(fontFullPath, fontName), fonfSize, Font.BOLD);
			boldItalicFont = new Font(getFontFromPath(fontFullPath, fontName), fonfSize, Font.BOLDITALIC);
			italicFont = new Font(getFontFromPath(fontFullPath, fontName), fonfSize, Font.ITALIC);
			underlineFont = new Font(getFontFromPath(fontFullPath, fontName), fonfSize, Font.UNDERLINE);
			cssFile = getCssFile(cssPath);
		} else {
			log.error("Fonts initialisation after Spring context building failed!");
		}
	}

	@Override
	public ElementList convert(String htmlString, Font font) {
		defaultFont = font;
		final List<Element> elOperated = new ArrayList<>();
		final ElementList elements = new ElementList();
		String output = htmlUtils.makeHtmlReadable(htmlString);
		CSSResolver cssResolver = new StyleAttrCSSResolver();
		cssResolver.addCss(cssFile);
		HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
		htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
		ElementHandlerPipeline pdf = new ElementHandlerPipeline(writable -> {
			if (writable instanceof WritableElement) {
				List<Element> innElements = ((WritableElement) writable).elements();
				for (Element element : innElements) {
					elOperated.add(element);
					injectFontsToElements(element);
					elements.add(element);
				}
			}
		}, null);
		HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
		CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);
		XMLWorker worker = new XMLWorker(css, true);
		XMLParser parser = new XMLParser(worker);

		try {
			parser.parse(new ByteArrayInputStream(output.getBytes()));
		} catch (IOException | RuntimeException r) {
			log.error("Parse HTML error {} at element {}", r.getMessage(), elOperated.get(elOperated.size() - 1));
			elements.add(ERROR_PARAGRAPH);
		}

		return elements;
	}

	private void injectFontsToElements(Element element) {
		AtomicInteger fontStyle = new AtomicInteger();
		fontStyle.set(0);

		if (element.isContent()) {

			if (element instanceof PdfPTable) {
				injectFontsToTable((PdfPTable) element);
			}

			element.getChunks().stream().filter(Objects::nonNull).filter(Chunk::isContent).forEach(chunk -> {

				if (chunk.isContent() && chunk.getFont() != null) {
					fontStyle.set(chunk.getFont().getStyle());
					chunk = getFontByStyle(chunk, fontStyle.get());
				}
			});
		}
	}

	private Chunk getFontByStyle(Chunk chunk, int style) {
		Font font = getFontFromPreSet(style);

		if (font == null) {
			// number range <0 -> 4>
			if (style > Font.NORMAL && style < Font.UNDERLINE) {
				defaultFont.setStyle(style);
			} else {
				defaultFont.setStyle(Font.NORMAL);
			}
			chunk.setFont(defaultFont);

		} else {
			chunk.setFont(font);
		}

		return chunk;
	}

	private Font getFontFromPreSet(int style) {
		Font result = null;
		switch (style) {
		case Font.BOLD:
			result = boldFont;
			break;
		case Font.BOLDITALIC:
			result = boldItalicFont;
			break;
		case Font.ITALIC:
			result = italicFont;
			break;
		case Font.UNDERLINE:
			result = underlineFont;
			break;
		case Font.NORMAL:
			result = normalFont;
			break;
		case Font.UNDEFINED:
			result = normalFont;
			break;
		default:
			result = defaultFont;
		}

		return result;
	}

	private Element injectFontsToTable(PdfPTable table) {
		AtomicInteger fontStyle = new AtomicInteger();
		fontStyle.set(0);

		List<PdfPRow> rows = table.getRows();

		for (PdfPRow row : rows) {
			PdfPCell[] cells = row.getCells();

			if (cells != null) {

				for (PdfPCell cell : cells) {
					List<Element> elements = new ArrayList<>();

					try {
						elements = elementsFromColumn(cell);

						if (elements == null) {
							continue;
						}

					} catch (Exception ignore) {
						continue;
					}

					elements.stream().filter(Objects::nonNull).filter(Element::isContent)
							.forEach(element -> element.getChunks().stream().filter(Objects::nonNull)
									.filter(Chunk::isContent)
									.forEach(chunk -> chunk = assignFontInChunk(chunk, fontStyle)));
				}
			}
		}

		return table;
	}

	private List<Element> elementsFromColumn(PdfPCell cell) {
		List<Element> elements = new ArrayList<>();

		if (cell.getColumn() != null) {
			elements = cell.getColumn().getCompositeElements();
		}

		return elements;
	}

	private Chunk assignFontInChunk(Chunk chunk, AtomicInteger fontStyle) {

		if (chunk.isContent() && chunk.getFont() != null) {
			Integer styleIn = chunk.getFont().getStyle();
			fontStyle.set(styleIn);
			getFontByStyle(chunk, fontStyle.get());
		}

		return chunk;
	}

}
