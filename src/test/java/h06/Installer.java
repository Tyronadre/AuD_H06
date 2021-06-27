package h06;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Iterator;

public class Installer {

    // ----------------------------------- //
    // DO NOT CHANGE ANYTHING IN THIS FILE //
    // ----------------------------------- //

    private static final String ASSIGNMENT_ID = "H06";

    /**
     * Test installer
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpResponse<String> response = getHttpResource(".test_metadata.xml");

        if (response.statusCode() != 200)
            throw new RuntimeException("Unable to fetch version from repository");

        XMLReader.XMLNode root = new XMLReader(response.body()).getNode("/metadata");

        System.out.println("Installing version " + root.getNode("version") + " of tests for " + ASSIGNMENT_ID + "...");

        for (XMLReader.XMLNode node : root.getNodeList("hashes/*"))
            updateLocal(node.getAttribute("file"));
    }

    /**
     * Requests a resource / file from the repository
     * @param resource the resource to get from the repository
     * @return a {@link HttpResponse<String>} object
     */
    private static HttpResponse<String> getHttpResource(String resource) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                                      .version(HttpClient.Version.HTTP_2)
                                      .followRedirects(HttpClient.Redirect.NORMAL)
                                      .connectTimeout(Duration.ofSeconds(20))
                                      .build();
        HttpRequest request = HttpRequest.newBuilder(
                URI.create("https://git.rwth-aachen.de/aud-tests/AuD-2021-" + ASSIGNMENT_ID +"-Student/-/raw/master/" + resource)).build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Updates (overwrites) the specified file with the contents of the file at the repository
     * @param fileName the relative path to the file
     */
    private static void updateLocal(String fileName) throws IOException, InterruptedException {
        System.out.print("Downloading " + fileName + "... ");

        File file = new File(fileName);
        HttpResponse<String> response = getHttpResource(fileName);

        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();

        if (response != null && response.statusCode() == 200) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(response.body());
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("done");

            return;
        }

        System.out.println("unable to fetch file from repository");
    }

    @SuppressWarnings("SameParameterValue")
    private static class XMLReader {

        Document document;
        XPath xPath;

        private XMLReader(String content) {
            try {
                document = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder()
                        .parse(new InputSource(new StringReader(content)));
                xPath = XPathFactory.newInstance().newXPath();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private XMLReader.XMLNode getNode(String query) {
            try {
                return new XMLReader.XMLNode((Node) xPath.evaluate(query, document, XPathConstants.NODE));
            } catch (XPathExpressionException e) {
                throw new RuntimeException(e);
            }
        }

        private class XMLNode {

            Node node;

            private XMLNode(Node node) {
                this.node = node;
            }

            private String getAttribute(String attributeName) {
                return node.getAttributes().getNamedItem(attributeName).getTextContent();
            }

            private XMLReader.XMLNode getNode(String query) {
                try {
                    return new XMLReader.XMLNode((Node) xPath.evaluate(query, node, XPathConstants.NODE));
                } catch (XPathExpressionException e) {
                    throw new RuntimeException(e);
                }
            }

            private XMLReader.XMLNodeList getNodeList(String query) {
                try {
                    return new XMLReader.XMLNodeList((NodeList) xPath.evaluate(query, node, XPathConstants.NODESET));
                } catch (XPathExpressionException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String toString() {
                return node.getTextContent();
            }
        }

        private class XMLNodeList implements Iterable<XMLReader.XMLNode> {

            NodeList nodeList;

            private XMLNodeList(NodeList nodeList) {
                this.nodeList = nodeList;
            }

            @Override
            public Iterator<XMLReader.XMLNode> iterator() {
                return new Iterator<>() {

                    int currentIndex = 0;
                    final int maxIndex = nodeList.getLength();

                    @Override
                    public boolean hasNext() {
                        return currentIndex < maxIndex;
                    }

                    @Override
                    public XMLReader.XMLNode next() {
                        return new XMLReader.XMLNode(nodeList.item(currentIndex++));
                    }
                };
            }
        }
    }
}
