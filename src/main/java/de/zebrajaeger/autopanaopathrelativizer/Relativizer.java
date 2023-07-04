package de.zebrajaeger.autopanaopathrelativizer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class Relativizer {

    private final FileNameTransformer fileNameTransformer;

    public void relativizeAllArgs(List<String> args) {
        args.forEach(this::relativizeArg);
    }

    public void relativizeAllFiles(List<File> args) {
        args.forEach(this::relativize);
    }

    public void relativizeArg(String arg) {
        relativize(new File(arg));
    }

    public void relativize(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                relativizeFile(file);
            }
            if (file.isDirectory()) {
                relativizeDir(file);
            }
            return;
        }
    }

    private void relativizeDir(File dir) {
        final String[] files = dir.list(new SuffixFileFilter(".pano"));

        if (files == null) {
            return;
        }

        for (String file : files) {
            relativizeFile(new File(dir, file));
        }
    }

    private void relativizeFile(File panoFile) {
        File file = panoFile;
        try {
            file = panoFile.getCanonicalFile();
        } catch (IOException e) {
            log.warn("Can not transform file to canonical file: '{}'", panoFile.getAbsolutePath());
        }

        log.info("Process pano file: '{}'", file.getAbsolutePath());
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            XPath xPath = XPathFactory.newInstance().newXPath();
            final NodeList images = (NodeList) xPath.compile("/pano/images/image/def/@filename").evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i < images.getLength(); ++i) {
                final Node node = images.item(i);
                File imgFile = new File(node.getNodeValue());
                log.info("  - img: '{}'", node.getNodeValue());
                if (!imgFile.exists()) {
                    log.warn("    - does not exist");
                }

                Path p1 = panoFile.getAbsoluteFile().toPath();
                Path p2 = Path.of(node.getNodeValue());
                if (p2.isAbsolute()) {
                    final Path newPath = p1.relativize(p2);
                    log.info("    - {}", newPath);
                    node.setNodeValue(newPath.toString());
                } else {
                    log.info("    - already relative");
                }
            }

            final String targetFilePath = fileNameTransformer.transform(file);
            FileUtils.write(new File(targetFilePath), toString(document), StandardCharsets.UTF_8);

        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException |
                 TransformerException e) {
            log.error("Failed to transform file: '{}'", panoFile);
        }
    }

    private String toString(Document doc) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 4);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        Writer out = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(out));
        return out.toString();
    }

}
