package nl.inl.blacklab.indexers.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.inl.blacklab.index.annotated.AnnotationSensitivities;

/**
 * Configuration for a single annotation (formerly "property") of an annotated field.
 */
public class ConfigAnnotation {

    protected static final Logger logger = LogManager.getLogger(ConfigAnnotation.class);

    /** Annotation name, or forEach (or name XPath, if forEach) */
    private String name;

    /** If specified, all other XPath expression are relative to this */
    private String basePath = null;

    /** Where to find body text */
    private String valuePath;

    /**
     * If valuePath consists only of digits, this is the integer value. Otherwise,
     * it is Integer.MAX_VALUE
     */
    private int valuePathInt = Integer.MAX_VALUE;

    /**
     * If null: regular annotation definition. Otherwise, find all nodes matching
     * this XPath, then evaluate name and valuePath as XPaths for each matching
     * node, adding a subannotation value for each. NOTE: forEach is only supported
     * for subannotations. All subannotations need to be declared at the start, however.
     */
    private String forEachPath;

    /** How to process annotation values (if at all) */
    private final List<ConfigProcessStep> process = new ArrayList<>();

    /**
     * What sensitivity setting to use to index this annotation (optional, default
     * depends on field name)
     */
    private AnnotationSensitivities sensitivity = AnnotationSensitivities.DEFAULT;

    /** XPaths to capture the value of, to substitute for $1-$9 in valuePath */
    private final List<String> captureValuePaths = new ArrayList<>();

    /**
     * Our subannotations. Note that only 1 level of subannotations is processed
     * (i.e. there's no subsubannotations), although we could process more levels if
     * desired.
     */
    private final List<ConfigAnnotation> subannotations = new ArrayList<>();

    /** Our subannotations (except forEach's) by name. */
    private final Map<String, ConfigAnnotation> subAnnotationsByName = new LinkedHashMap<>();

    /** Should we create a forward index for this annotation? */
    private boolean forwardIndex = true;

    /** How to display the field in the interface (optional) */
    private String displayName = "";

    /** How to describe the field in the interface (optional) */
    private String description = "";

    /** What UI element to show in the interface (optional) */
    private String uiType = "";

    /** Can this annotation have multiple values at one token position? [false] */
    private boolean multipleValues = false;
    
    /** Should we allow duplicate values at one token position? (if false, performs extra checking and discards duplicates) */
    private boolean allowDuplicateValues = false;
    
    /** Should we capture the innerXml of the node instead of the text */
    private boolean captureXml = false;

    /**
     * Is this an internal annotation?
     * BlackLab always generates some internal annotations for every index, these are (usually) not values users are interested in,
     *  so they are marked with "isInternal" in the indexStructure/indexMetadata so clients can ignore them.
     * We also allow users to explicitly mark their own annotations as "internal" annotations.
     * BlackLab itself does not use this flag.
     */
    private boolean internal = false;

    /** What annotations have we warned about using special default sensitivity? */
    private final static Set<String> warnSensitivity = new HashSet<>();

    public ConfigAnnotation() {
    }

    public ConfigAnnotation(String name, String valuePath, String forEachPath) {
        setName(name);
        setValuePath(valuePath);
        setForEachPath(forEachPath);
    }

    public void validate() {
        String t = "annotation";
        ConfigInputFormat.req(name, t, isForEach() ? "namePath" : "name");
        //ConfigInputFormat.req(valuePath, t, "valuePath");
        for (ConfigAnnotation s : subannotations)
            s.validate();
        for (ConfigProcessStep step : process)
            step.validate();
    }

    public ConfigAnnotation copy() {
        ConfigAnnotation result = new ConfigAnnotation(name, valuePath, forEachPath);
        result.setProcess(process);
        result.setDisplayName(displayName);
        result.setDescription(description);
        result.setSensitivity(sensitivity);
        result.setUiType(uiType);
        result.setBasePath(basePath);
        result.captureValuePaths.addAll(captureValuePaths);
        for (ConfigAnnotation a : subannotations) {
            result.addSubAnnotation(a.copy());
        }
        result.setForwardIndex(forwardIndex);
        result.setMultipleValues(multipleValues);
        result.setAllowDuplicateValues(allowDuplicateValues);
        result.setCaptureXml(captureXml);
        return result;
    }

    public String getName() {
        return name;
    }

    public String getValuePath() {
        return valuePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValuePath(String valuePath) {
        this.valuePath = valuePath;
        if (valuePath != null && valuePath.matches("\\d+"))
            valuePathInt = Integer.parseInt(valuePath);
    }

    public boolean isValuePathInteger() {
        return valuePathInt != Integer.MAX_VALUE;
    }

    public int getValuePathInt() {
        return valuePathInt;
    }

    public List<String> getCaptureValuePaths() {
        return Collections.unmodifiableList(captureValuePaths);
    }

    public void addCaptureValuePath(String captureValuePath) {
        captureValuePaths.add(captureValuePath);
    }

    public List<ConfigAnnotation> getSubAnnotations() {
        return Collections.unmodifiableList(subannotations);
    }

    public ConfigAnnotation getSubAnnotation(String name) {
        return subAnnotationsByName.get(name);
    }

    public void addSubAnnotation(ConfigAnnotation subAnnotation) {
        subannotations.add(subAnnotation);
        if (!subAnnotation.isForEach())
            subAnnotationsByName.put(subAnnotation.getName(), subAnnotation);
    }

    public String getForEachPath() {
        return forEachPath;
    }

    public void setForEachPath(String forEachPath) {
        this.forEachPath = forEachPath;
    }

    public boolean isForEach() {
        return forEachPath != null;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUiType() {
        return uiType;
    }

    public void setUiType(String uiType) {
        this.uiType = uiType;
    }

    public AnnotationSensitivities getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(AnnotationSensitivities sensitivity) {
        this.sensitivity = sensitivity;
    }

    public List<ConfigProcessStep> getProcess() {
        return process;
    }

    public void setProcess(List<ConfigProcessStep> process) {
        this.process.clear();
        this.process.addAll(process);
    }

    public boolean createForwardIndex() {
        return forwardIndex;
    }

    public void setForwardIndex(boolean forwardIndex) {
        this.forwardIndex = forwardIndex;
    }

    public boolean isMultipleValues() {
        return multipleValues;
    }

    public void setMultipleValues(boolean multipleValues) {
        this.multipleValues = multipleValues;
    }

    public boolean isAllowDuplicateValues() {
        return allowDuplicateValues;
    }

    public void setAllowDuplicateValues(boolean allowDuplicateValues) {
        this.allowDuplicateValues = allowDuplicateValues;
    }

    public void setCaptureXml(boolean captureXml) {
        this.captureXml = captureXml;
    }
    
    public boolean isCaptureXml() {
        return this.captureXml;
    }
    
    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public boolean isInternal() {
        return this.internal;
    }

    @Override
    public String toString() {
        return "ConfigAnnotation [name=" + name + "]";
    }

    public AnnotationSensitivities getSensitivitySetting() {
        AnnotationSensitivities sensitivity = getSensitivity();
        if (sensitivity == AnnotationSensitivities.DEFAULT) {
            String name = getName();
            sensitivity = AnnotationSensitivities.defaultForAnnotation(name);
            if (sensitivity != AnnotationSensitivities.ONLY_INSENSITIVE) {
                // Historic behaviour: if no sensitivity is given, "word" and "lemma" annotations will
                // get SensitivitySetting.SENSITIVE_AND_INSENSITIVE; all others get SensitivitySetting.ONLY_INSENSITIVE.
                // Warn users about this so they can make their config files explicit before this special case is removed.
                synchronized (warnSensitivity) {
                    if (!warnSensitivity.contains(name)) {
                        warnSensitivity.add(name);
                        logger.warn("Configuration " + getName()
                                + " relies on special default sensitivity 'sensitive_insensitive' for annotation "
                                + name
                                + "; this behaviour "
                                + "is deprecated. Please update your config to explicitly declare the sensitivity setting for this annotation. In a future version, all annotations "
                                + "without explicit sensitivity will default to 'insensitive'.");
                    }
                }
            }
        }
        return sensitivity;
    }
}
