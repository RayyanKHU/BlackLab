package nl.inl.blacklab.index;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.inl.blacklab.exceptions.BlackLabRuntimeException;
import nl.inl.util.UnicodeStream;

/**
 * Supports creation of several types of DocIndexers implemented directly in
 * code. Additionally will attempt to load classes if passed a fully-qualified
 * ClassName, and implementations by name in .indexers package within BlackLab.
 */
public class DocIndexerFactoryClass implements DocIndexerFactory {

    private final Map<String, Class<? extends DocIndexerLegacy>> supported = new HashMap<>();
    private final Set<String> unsupported = new HashSet<>();

    @Override
    public void init() {
        try {
            // If the legacy docindexers JAR is included on the classpath, register them
            Class<?> cls = Class.forName("nl.inl.blacklab.index.LegaDocIndexerRegisterer");
            Method m = cls.getMethod("register", DocIndexerFactoryClass.class);
            m.invoke(null, this);
        } catch (ClassNotFoundException e) {
            // OK, JAR not on classpath
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } 
    }

    @Override
    public boolean isSupported(String formatIdentifier) {
        if (!supported.containsKey(formatIdentifier) && !unsupported.contains(formatIdentifier))
            find(formatIdentifier);

        return supported.containsKey(formatIdentifier);
    }

    @Override
    public List<Format> getFormats() {
        List<Format> ret = new ArrayList<>();
        for (Entry<String, Class<? extends DocIndexerLegacy>> e : supported.entrySet()) {
            Format desc = new Format(e.getKey(), DocIndexerLegacy.getDisplayName(e.getValue()),
                    DocIndexerLegacy.getDescription(e.getValue()), "");
            desc.setVisible(DocIndexerLegacy.isVisible(e.getValue()));
            ret.add(desc);
        }
        return ret;
    }

    @Override
    public Format getFormat(String formatIdentifier) {
        if (!isSupported(formatIdentifier))
            return null;

        Class<? extends DocIndexer> docIndexerClass = supported.get(formatIdentifier);
        Format desc = new Format(formatIdentifier, DocIndexerLegacy.getDisplayName(docIndexerClass),
                DocIndexerLegacy.getDescription(docIndexerClass), "");
        desc.setVisible(DocIndexerLegacy.isVisible(docIndexerClass));
        return desc;
    }

    public void addFormat(String formatIdentifier, Class<? extends DocIndexerLegacy> docIndexerClass) {
        this.supported.put(formatIdentifier, docIndexerClass);
    }

    @SuppressWarnings("unchecked")
    private void find(String formatIdentifier) {
        // Is it a fully qualified class name?
        Class<? extends DocIndexerLegacy> docIndexerClass = null;
        try {
            docIndexerClass = (Class<? extends DocIndexerLegacy>) Class.forName(formatIdentifier);
        } catch (Exception e1) {
            try {
                // No. Is it a class in the BlackLab indexers package?
                docIndexerClass = (Class<? extends DocIndexerLegacy>) Class
                        .forName("nl.inl.blacklab.indexers." + formatIdentifier);
            } catch (Exception e) {
                // Couldn't be resolved. That's okay, maybe another factory will support this key
                // Cache the key for next time.
                unsupported.add(formatIdentifier);
            }
        }

        if (docIndexerClass != null) {
            supported.put(formatIdentifier, docIndexerClass);
        }
    }

    @Override
    public DocIndexer get(String formatIdentifier, DocWriter indexer, String documentName, InputStream is, Charset cs) {
        if (!isSupported(formatIdentifier))
            throw new UnsupportedOperationException("Unknown format '" + formatIdentifier
                    + "', call isSupported(formatIdentifier) before attempting to get()");

        try {
            // Instantiate our DocIndexer class
            Class<? extends DocIndexer> docIndexerClass = supported.get(formatIdentifier);
            Constructor<? extends DocIndexer> constructor;
            DocIndexer docIndexer;
            try {
                constructor = docIndexerClass.getConstructor();
                docIndexer = constructor.newInstance();
                docIndexer.setDocWriter(indexer);
                docIndexer.setDocumentName(documentName);
                docIndexer.setDocument(is, cs);
            } catch (NoSuchMethodException e) {
                // No, this is an older DocIndexer that takes document name and reader directly.
                constructor = docIndexerClass.getConstructor(DocWriter.class, String.class, Reader.class);
                docIndexer = constructor.newInstance(indexer, documentName, new InputStreamReader(is, cs));
            }
            return docIndexer;
        } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException e) {
            throw BlackLabRuntimeException.wrap(e);
        }
    }

    @Override
    public DocIndexer get(String formatIdentifier, DocWriter indexer, String documentName, File f, Charset cs) {
        if (!isSupported(formatIdentifier))
            throw new UnsupportedOperationException("Unknown format '" + formatIdentifier
                    + "', call isSupported(formatIdentifier) before attempting to get()");

        try {
            // Instantiate our DocIndexer class
            Class<? extends DocIndexer> docIndexerClass = supported.get(formatIdentifier);
            Constructor<? extends DocIndexer> constructor;
            DocIndexer docIndexer;
            try {
                constructor = docIndexerClass.getConstructor();
                docIndexer = constructor.newInstance();
                docIndexer.setDocWriter(indexer);
                docIndexer.setDocumentName(documentName);
                docIndexer.setDocument(f, cs);
            } catch (NoSuchMethodException e) {
                // No, this is an older DocIndexer that takes document name and reader directly.
                constructor = docIndexerClass.getConstructor(DocWriter.class, String.class, Reader.class);
                UnicodeStream is = new UnicodeStream(new FileInputStream(f), Indexer.DEFAULT_INPUT_ENCODING);
                Charset detectedCharset = is.getEncoding();
                docIndexer = constructor.newInstance(indexer, documentName, new InputStreamReader(is, detectedCharset));
            }
            return docIndexer;
        } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | IOException e) {
            throw BlackLabRuntimeException.wrap(e);
        }
    }

    @Override
    public DocIndexer get(String formatIdentifier, DocWriter indexer, String documentName, byte[] contents, Charset cs) {
        if (!isSupported(formatIdentifier))
            throw new UnsupportedOperationException("Unknown format '" + formatIdentifier
                    + "', call isSupported(formatIdentifier) before attempting to get()");

        try {
            // Instantiate our DocIndexer class
            Class<? extends DocIndexer> docIndexerClass = supported.get(formatIdentifier);
            Constructor<? extends DocIndexer> constructor;
            DocIndexer docIndexer;
            try {
                constructor = docIndexerClass.getConstructor();
                docIndexer = constructor.newInstance();
                docIndexer.setDocWriter(indexer);
                docIndexer.setDocumentName(documentName);
                docIndexer.setDocument(contents, cs);
            } catch (NoSuchMethodException e) {
                // No, this is an older DocIndexer that takes document name and reader directly.
                constructor = docIndexerClass.getConstructor(DocWriter.class, String.class, Reader.class);
                docIndexer = constructor.newInstance(indexer, documentName,
                        new InputStreamReader(new ByteArrayInputStream(contents), cs));
            }
            return docIndexer;
        } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException e) {
            throw BlackLabRuntimeException.wrap(e);
        }
    }

    @Override
    public String formatError(String formatIdentifier) {
        return null;
    }
}
