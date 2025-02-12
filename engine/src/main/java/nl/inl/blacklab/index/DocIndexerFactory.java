package nl.inl.blacklab.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.inl.blacklab.indexers.config.ConfigInputFormat;
import nl.inl.blacklab.search.indexmetadata.MetadataFields;

/**
 * Factory responsible for creating {@link DocIndexer} instances. Through this
 * factory it is possible to register new "formats" with BlackLab. A format
 * essentially is some implementation of a DocIndexer that supports indexing a
 * specific type of file/format (such as for example plaintext or TEI).
 * <p>
 * If you have created a custom implementation of DocIndexer to index a specific
 * dialect of the TEI format for example, you can make BlackLab aware of that
 * class by registering a new DocIndexerFactory capable of creating the
 * DocIndexer with the {@link DocumentFormats} class. The factory must then
 * expose the new format through {@link DocIndexerFactory#isSupported(String)}
 * and {@link DocIndexerFactory#getFormats()}, and construct and configure an
 * appropriate DocIndexer when get() is called for the format's id. BlackLab
 * will then use the factory to create fitting DocIndexers whenever it's asked
 * to index files of that format (as specified by the user).
 * <p>
 * How formatIdentifiers map to actual DocIndexer implementations is up to the
 * factory, it's possible to map multiple formatIdentifiers to the same
 * DocIndexer, or vice versa, this is up to the implementation of the factory
 * and associated docIndexer(s).
 * <p>
 * This is used in {@link DocIndexerFactoryConfig} for example, where only a few
 * actual DocIndexer classes are used, but each of them can handle an arbitrary
 * number of external configuration files, and the factory exposes each of those
 * configuration files with its own unique formatIdentifier.
 */
public interface DocIndexerFactory {

    /**
     * Description of a supported input format
     */
    class Format {
        private static final Logger logger = LogManager.getLogger(Format.class);

        private final String formatIdentifier;

        private final String displayName;

        private final String description;

        private final String helpUrl;

        private boolean visible = true;

        private ConfigInputFormat config;

        public String getId() {
            return formatIdentifier;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public String getHelpUrl() {
            return helpUrl;
        }

        public ConfigInputFormat getConfig() {
            return config;
        }

        public boolean isConfigurationBased() {
            return config != null;
        }

        public boolean isVisible() {
            return visible;
        }

        public Format(String formatIdentifier, String displayName, String description, String helpUrl) {
            super();
            this.formatIdentifier = formatIdentifier;
            this.displayName = displayName;
            this.description = description;
            this.helpUrl = helpUrl;
        }

        public void setVisible(boolean b) {
            this.visible = b;
        }

        public void setConfig(ConfigInputFormat config) {
            this.config = config;
        }

        /**
         * Before actually using a format, check if there's any glaring problems and if so, either warn the user
         * or throw an exception.
         */
        public void checkAndWarn() {
            if (config != null && config.getCorpusConfig().getSpecialFields().get(MetadataFields.SPECIAL_FIELD_SETTING_PID) == null) {
                logger.warn("YOUR DOCUMENT IDs ARE NOT PERSISTENT! The input format " + config.getName() + " " +
                        "does not specify a persistent identifier (pid) field. This will work, but random ids will " +
                        "be assigned to your documents every time you index. So reindexing may assign totally different " +
                        "document ids, and any saved links to documents will break. " +
                        "To fix this, specify a pidField using the corpusConfig.specialFields.pidField setting of your " +
                        "input format configuration (.blf.yaml file).");
            }
        }
    }

    /**
     * Don't call manually, is called when this factory is added to the
     * DocumentFormats registry
     * ({@link DocumentFormats#registerFactory(DocIndexerFactory)}).
     */
    void init();

    /**
     * Can this factory instantiate a docIndexer for this type of format. This
     * function will always be called prior to get().
     *
     * @param formatIdentifier lowercased and never null or empty string
     * @return true if this factory is able to create a docIndexer for the requested
     *         formatIdentifier
     */
    boolean isSupported(String formatIdentifier);

    /**
     * Return all formats supported by this factory.
     * 
     * @return the list
     */
    List<Format> getFormats();

    /**
     * Get the full format from its identifier.
     *
     * @return the format
     */
    Format getFormat(String formatIdentifier);

    /**
     * Instantiating a DocIndexer from an input stream.
     *
     * @param formatIdentifier the formatIdentifier for the document
     * @param indexer indexer object
     * @param documentName name of the unit we're indexing
     * @param is data to index
     * @param cs default character set if not defined
     * @return DocIndexer instance
     * @throws UnsupportedOperationException if called with an unsupported
     *             formatIdentifier (use
     *             {@link DocIndexerFactory#isSupported(String)})
     */
    DocIndexer get(String formatIdentifier, DocWriter indexer, String documentName, InputStream is, Charset cs)
            throws UnsupportedOperationException;

    /**
     * Instantiating a DocIndexer from a file.
     *
     * @param formatIdentifier the formatIdentifier for the document
     * @param indexer indexer object
     * @param documentName name of the unit we're indexing
     * @param f file to index
     * @param cs default character set if not defined
     * @return DocIndexer instance
     * @throws FileNotFoundException if file doesn't exist
     * @throws UnsupportedOperationException if called with an unsupported
     *             formatIdentifier (use
     *             {@link DocIndexerFactory#isSupported(String)})
     */
    DocIndexer get(String formatIdentifier, DocWriter indexer, String documentName, File f, Charset cs)
            throws UnsupportedOperationException, FileNotFoundException;

    /**
     * Instantiating a DocIndexer from a byte array.
     *
     * @param formatIdentifier the formatIdentifier for the document
     * @param indexer indexer object
     * @param documentName name of the unit we're indexing
     * @param b data to index
     * @param cs default character set if not defined
     * @return DocIndexer instance
     * @throws UnsupportedOperationException if called with an unsupported
     *             formatIdentifier (use
     *             {@link DocIndexerFactory#isSupported(String)})
     */
    DocIndexer get(String formatIdentifier, DocWriter indexer, String documentName, byte[] b, Charset cs)
            throws UnsupportedOperationException;

    /**
     * If this format exists but has an error, return the error.
     * 
     * @param formatIdentifier format to check for errors
     * @return null if not found or no errors, the error otherwise  
     */
    String formatError(String formatIdentifier);
}
