package h06.util;

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
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static h06.util.Config.*;

public class Updater {

    // ----------------------------------- //
    // DO NOT CHANGE ANYTHING IN THIS FILE //
    // ----------------------------------- //

    public static final String LOCAL_VERSION = "1.0.1",
                               ASSIGNMENT_ID = "H06",
                               REPOSITORY_URL = "https://git.rwth-aachen.de/aud-tests/AuD-2021-" + ASSIGNMENT_ID + "-Student/-/";

    /**
     * Checks if the repository is newer than the local copy and updates it.
     * Messages are printed to let the user know what is happening
     * @return whether any changes have been written to disk
     * @see Config#CHECK_FOR_UPDATES
     */
    public static boolean checkForUpdates() {
        HttpResponse<String> response = getHttpResource(".test_metadata.xml");
        boolean persistentChanges = false;

        if (response == null || response.statusCode() != 200) {
            System.err.println("Unable to fetch version from repository");
            return false;
        }

        XMLReader.XMLNode root = new XMLReader(response.body()).getNode("/metadata");
        Version localVersion = new Version(LOCAL_VERSION),
                remoteVersion = new Version(root.getNode("version").toString());

        if (remoteVersion.compareTo(localVersion) <= 0) {
            System.out.println("Tests are up to date");

            if (!(CHECK_HASHES || AUTO_UPDATE))
                return false;
        } else {
            System.out.println("Update available! Local version: " + localVersion + " -- Remote version: " + remoteVersion);
            System.out.println("Changelog: " + REPOSITORY_URL + "blob/master/changelog.md");
            System.out.println(root.getNode("message"));
        }

        for (XMLReader.XMLNode node : root.getNodeList("hashes/*")) {
            String fileName = node.getAttribute("file"),
                   expectedHash = node.toString();

            if (EXCLUDED_FILES.contains(fileName))
                continue;

            if (!new File(fileName).exists()) {
                if (AUTO_UPDATE)
                    persistentChanges = updateLocal(fileName);
                else
                    System.err.println("File " + fileName + " not found");

                continue;
            }

            if (!getHash(fileName).equals(expectedHash)) {
                System.out.println("Hash mismatch for file " + fileName);

                if (AUTO_UPDATE)
                    persistentChanges = updateLocal(fileName);
            }
        }

        Set<String> constants = Config.getConfigs();

        if (AUTO_UPDATE && !root.getNodeList("configuration/constants/*")
                .stream().map(xmlNode -> xmlNode.getAttribute("name")).allMatch(constants::contains)) {
            updateConfig(root.getNode("configuration"));

            persistentChanges = true;
        }

        return persistentChanges;
    }

    /**
     * Requests a resource / file from the repository
     * @param resource the resource to get from the repository
     * @return a {@link HttpResponse<String>} object
     */
    private static HttpResponse<String> getHttpResource(String resource) {
        HttpClient client = HttpClient.newBuilder()
                                      .version(HttpClient.Version.HTTP_2)
                                      .followRedirects(HttpClient.Redirect.NORMAL)
                                      .connectTimeout(Duration.ofSeconds(20))
                                      .build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(REPOSITORY_URL + "raw/master/" + resource)).build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Calculates the MD5 hash of a file and returns it
     * @param fileName path to the file
     * @return the hash as a hexadecimal string
     */
    private static String getHash(String fileName) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            try (InputStream inputStream = new FileInputStream(fileName)) {
                String actualHash = new BigInteger(1, messageDigest.digest(inputStream.readAllBytes())).toString(16);

                return "0".repeat(32 - actualHash.length()) + actualHash;
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates (overwrites) the specified file with the contents of the file at the repository
     * @param fileName the relative path to the file
     * @return whether anything has been written to disk
     */
    private static boolean updateLocal(String fileName) {
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
                return true;
            }

            System.out.println("done");

            return true;
        }

        System.out.println("unable to fetch file from repository");

        return false;
    }

    /**
     * Updates the configuration file, keeping all data written between the markers ">>>##" and "##<<<"
     * @param configNode a XMLNode with configuration data
     * @see Config
     */
    private static void updateConfig(XMLReader.XMLNode configNode) {
        String configStub = configNode.getNode("stub").toString().trim() + "\n";
        File configFile = new File(configNode.getAttribute("file"));
        StringBuilder configFileContents = new StringBuilder("    // >>>## UPDATE MARKER, DO NOT REMOVE, ONLY MODIFY THE LINES BELOW\n");
        Set<String> existingFields = Config.getConfigs();

        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            boolean insert = false;

            for (String line = reader.readLine(); !line.matches("\\s*// ##<<<.*"); line = reader.readLine()) {
                if (line.matches("\\s*// >>>##.*")) {
                    line = reader.readLine();
                    insert = true;
                }

                if (insert)
                    configFileContents.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        configFileContents.append(configNode.getNodeList("constants/*")
                                            .stream()
                                            .filter(xmlNode -> !existingFields.contains(xmlNode.getAttribute("name")))
                                            .map(xmlNode -> "    " + xmlNode.toString().trim() + "\n\n")
                                            .collect(Collectors.joining()));

        configFileContents.append("    // ##<<< UPDATE MARKER, DO NOT REMOVE, DO NOT CHANGE ANYTHING BELOW THIS LINE\n\n");

        configFileContents.append(configNode.getNodeList("methods/*")
                                            .stream()
                                            .map(xmlNode -> "    " + xmlNode.toString().trim())
                                            .collect(Collectors.joining("\n\n")));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(configStub.replaceFirst(">>>##<<<", configFileContents.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Version implements Comparable<Version> {

        private final int MAJOR_VERSION, MINOR_VERSION, PATCH_VERSION;

        private Version(String version) {
            String[] versions = version.split("\\.");

            MAJOR_VERSION = Integer.parseInt(versions[0]);
            MINOR_VERSION = Integer.parseInt(versions[1]);
            PATCH_VERSION = Integer.parseInt(versions[2]);
        }

        @Override
        public int compareTo(Version version) {
            int versionDiffMajor = MAJOR_VERSION - version.MAJOR_VERSION,
                versionDiffMinor = MINOR_VERSION - version.MINOR_VERSION,
                versionDiffPatch = PATCH_VERSION - version.PATCH_VERSION;

            if (versionDiffMajor != 0)
                return versionDiffMajor;
            else if (versionDiffMinor != 0)
                return versionDiffMinor;
            else
                return versionDiffPatch;
        }

        @Override
        public String toString() {
            return MAJOR_VERSION + "." + MINOR_VERSION + "." + PATCH_VERSION;
        }
    }

    @SuppressWarnings({"SameParameterValue", "unused"})
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

        private XMLNode getNode(String query) {
            try {
                return new XMLNode((Node) xPath.evaluate(query, document, XPathConstants.NODE));
            } catch (XPathExpressionException e) {
                throw new RuntimeException(e);
            }
        }

        private XMLNodeList getNodeList(String query) {
            try {
                return new XMLNodeList((NodeList) xPath.evaluate(query, document, XPathConstants.NODESET));
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

            private XMLNode getNode(String query) {
                try {
                    return new XMLNode((Node) xPath.evaluate(query, node, XPathConstants.NODE));
                } catch (XPathExpressionException e) {
                    throw new RuntimeException(e);
                }
            }

            private XMLNodeList getNodeList(String query) {
                try {
                    return new XMLNodeList((NodeList) xPath.evaluate(query, node, XPathConstants.NODESET));
                } catch (XPathExpressionException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String toString() {
                return node.getTextContent();
            }
        }

        private class XMLNodeList implements Iterable<XMLNode> {

            NodeList nodeList;

            private XMLNodeList(NodeList nodeList) {
                this.nodeList = nodeList;
            }

            @Override
            public Iterator<XMLNode> iterator() {
                return new Iterator<>() {

                    int currentIndex = 0;
                    final int maxIndex = nodeList.getLength();

                    @Override
                    public boolean hasNext() {
                        return currentIndex < maxIndex;
                    }

                    @Override
                    public XMLNode next() {
                        return new XMLNode(nodeList.item(currentIndex++));
                    }
                };
            }

            private XMLNode[] toArray() {
                XMLNode[] xmlNodes = new XMLNode[nodeList.getLength()];

                for (int i = 0; i < xmlNodes.length; i++)
                    xmlNodes[i] = new XMLNode(nodeList.item(i));

                return xmlNodes;
            }

            private Stream<XMLNode> stream() {
                return Stream.of(toArray());
            }
        }
    }
}
