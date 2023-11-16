package com.github.joelgodofwar.mmh.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class VersionChecker {
    private String pluginName;
    private final int projectID;
    private final String currentVersion;
    private final String githubURL;
    String versionType;
    private final List<String> releaseList = new ArrayList<>();
    private final List<String> developerList = new ArrayList<>();
    private String recommendedVersion = "uptodate";

    public VersionChecker(JavaPlugin plugin, int projectID, String githubURL) {
        this.projectID = projectID;
        this.currentVersion = plugin.getDescription().getVersion();
        this.githubURL = githubURL;
    }

    public VersionChecker(String plugin, int projectID, String githubURL) {
        this.currentVersion = plugin;
        this.projectID = projectID;
        this.githubURL = githubURL;
    }

    public String getReleaseUrl() {
        return "https://spigotmc.org/resources/" + projectID;
    }

    public boolean checkForUpdates() throws Exception {
        URL url = new URL(githubURL);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        connection.setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
        Document doc = db.parse(connection.getInputStream());
        doc.getDocumentElement().normalize();
        NodeList releaseNodes = doc.getElementsByTagName("release");
        for (int i = 0; i < releaseNodes.getLength(); i++) {
            Node node = releaseNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                releaseList.add(element.getElementsByTagName("version").item(0).getTextContent().replace("<version>", "").replace("</version>", ""));
                releaseList.add(element.getElementsByTagName("notes").item(0).getTextContent().replace("<notes>", "").replace("</notes>", ""));
                releaseList.add(element.getElementsByTagName("link").item(0).getTextContent().replace("<link>", "").replace("</link>", ""));
            }
        }
        NodeList developerNodes = doc.getElementsByTagName("developer");
        for (int i = 0; i < developerNodes.getLength(); i++) {
            Node node = developerNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                developerList.add(element.getElementsByTagName("version").item(0).getTextContent().replace("<version>", "").replace("</version>", ""));
                developerList.add(element.getElementsByTagName("notes").item(0).getTextContent().replace("<notes>", "").replace("</notes>", ""));
                developerList.add(element.getElementsByTagName("link").item(0).getTextContent().replace("<link>", "").replace("</link>", ""));
            }
        }

        connection.getInputStream().close();
        String releaseVersion = releaseList.get(0);
        String developerVersion = developerList.get(0);
        Bukkit.getLogger().warning(Ansi.LIGHT_YELLOW + "currentVersion=" + currentVersion + Ansi.RESET);
        Bukkit.getLogger().warning(Ansi.LIGHT_YELLOW + "releaseVersion=" + releaseVersion + Ansi.RESET);
        Bukkit.getLogger().warning(Ansi.LIGHT_YELLOW + "developerVersion=" + developerVersion + Ansi.RESET);
        if (currentVersion.compareTo(releaseVersion) < 0) {
            recommendedVersion = "release";
            return true;
        } else if (currentVersion.equals(releaseVersion)) {
            recommendedVersion = "uptodate";
            return false;
        } else if (currentVersion.contains(".D")) {
            String[] splitCurrentVersion = currentVersion.split("\\.D");
            String currentReleaseVersion = splitCurrentVersion[0];
            String[] splitDeveloperVersion = developerVersion.split("\\.D");
            String developerReleaseVersion = splitDeveloperVersion[0];
            if (currentReleaseVersion.equals(releaseVersion) && developerReleaseVersion.compareTo(currentReleaseVersion) <= 0) {
                recommendedVersion = "release";
                return true;
            } else if (developerReleaseVersion.compareTo(releaseVersion) < 0 && developerReleaseVersion.equals(currentReleaseVersion)) {
                recommendedVersion = "You are Up To Date";
                return false;
            } else if (developerVersion.compareTo(currentVersion) > 0) {
                recommendedVersion = "developer";
                return true;
            }
        }

        return false;
    }

    public List<String> getReleaseList() {
        return releaseList;
    }

    public List<String> getDeveloperList() {
        return developerList;
    }

    public String getRecommendedVersion() {
        return recommendedVersion;
    }

    public String oldVersion() {
        return currentVersion;
    }

    public String newVersion() {
        if (recommendedVersion.equalsIgnoreCase("release")) {
            return releaseList.get(0);
        } else if (recommendedVersion.equalsIgnoreCase("developer")) {
            return developerList.get(0);
        } else {
            return "UpToDate";
        }
    }

    public String newVersionNotes() {
        if (recommendedVersion.equalsIgnoreCase("release")) {
            return releaseList.get(1);
        } else if (recommendedVersion.equalsIgnoreCase("developer")) {
            return developerList.get(1);
        } else {
            return "UpToDate";
        }
    }

    public String getDownloadLink() {
        if (recommendedVersion.equalsIgnoreCase("release")) {
            return releaseList.get(2);//"https://www.spigotmc.org/resources/no-enderman-grief2.71236/history";
        } else if (recommendedVersion.equalsIgnoreCase("developer")) {
            return developerList.get(2);//"https://discord.com/channels/654087250665144321/654444482372173875";
        } else {
            return "UpToDate";
        }
    }
}
