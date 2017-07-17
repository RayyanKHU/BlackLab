package nl.inl.blacklab.index.xpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Configuration for a linked document. */
class ConfigLinkedDocument {

    public static enum MissingLinkPathAction {
        IGNORE,
        WARN,
        FAIL
    }

    /** Linked document type name, and field name if we're going to store it */
    private String name;

    private boolean store;

    /** Where in the document to find the information we need to locate the linked document. */
    private List<String> linkPaths;

    /** What to do if we can't find the link information: ignore, warn or fail */
    private MissingLinkPathAction ifLinkPathMissing;

    /** Format of the linked input file */
    private String inputFormat;

    /** File or URL reference to our linked document (or archive containing it) */
    private String inputFile;

    /** If input file is a TAR or ZIP archive, this is the path inside the archive */
    private String pathInsideArchive;

    /** Path to our specific document inside this file (if omitted, file must contain exactly one document) */
    private String documentPath;

    public ConfigLinkedDocument(String name) {
        this.name = name;
        store = false;
        ifLinkPathMissing = MissingLinkPathAction.FAIL;
        linkPaths = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStore() {
        return store;
    }

    public void setStore(boolean store) {
        this.store = store;
    }

    public List<String> getLinkPaths() {
        return Collections.unmodifiableList(linkPaths);
    }

    public void addLinkPath(String linkPath) {
        this.linkPaths.add(ConfigInputFormat.relXPath(linkPath));
    }

    public MissingLinkPathAction getIfLinkPathMissing() {
        return ifLinkPathMissing;
    }

    public void setIfLinkPathMissing(MissingLinkPathAction ifLinkPathMissing) {
        this.ifLinkPathMissing = ifLinkPathMissing;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getPathInsideArchive() {
        return pathInsideArchive;
    }

    public void setPathInsideArchive(String pathInsideArchive) {
        this.pathInsideArchive = pathInsideArchive;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }



}