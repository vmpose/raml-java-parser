package org.raml.parser.rule;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ConvertUtils;
import org.yaml.snakeyaml.nodes.ScalarNode;


public class SimpleRule extends DefaultTupleRule<ScalarNode, ScalarNode>
{


    private static final String EMPTY_MESSAGE = "can not be empty";
    private static final String DUPLICATE_MESSAGE = "Duplicate";
    private static final String TYPE_MISMATCH_MESSAGE = "Type mismatch: ";

    private ScalarNode keyNode;
    private ScalarNode valueNode;
    private Class<?> fieldClass;

    public SimpleRule(String fieldName, Class<?> fieldClass)
    {
        super(fieldName, new DefaultScalarTupleHandler(ScalarNode.class, fieldName));
        this.setFieldClass(fieldClass);
    }

    public static String getRuleEmptyMessage(String ruleName)
    {
        return ruleName + " " + EMPTY_MESSAGE;
    }

    public static String getDuplicateRuleMessage(String ruleName)
    {
        return DUPLICATE_MESSAGE + " " + ruleName;
    }
    
    public String getRuleTypeMisMatch(String fieldType)
    {
        return TYPE_MISMATCH_MESSAGE + getFieldName() + " must be of type " + fieldType;
    }

    @Override
    public List<ValidationResult> validateKey(ScalarNode key)
    {
        List<ValidationResult> validationResults = super.validateKey(key);
        if (wasAlreadyDefined())
        {
            validationResults.add(ValidationResult.createErrorResult(getDuplicateRuleMessage(getFieldName()), key.getStartMark(), key.getEndMark()));
        }
        setKeyNode(key);

        return validationResults;
    }

    @Override
    public List<ValidationResult> validateValue(ScalarNode node)
    {
        String value = node.getValue();
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        if (StringUtils.isEmpty(value))
        {
            validationResults.add(ValidationResult.createErrorResult(getRuleEmptyMessage(getFieldName()), keyNode.getStartMark(), keyNode.getEndMark()));
        }
        if (!ConvertUtils.canBeConverted(value, getFieldClass())) {
            validationResults.add(ValidationResult.createErrorResult(getRuleTypeMisMatch(getFieldClass().getSimpleName()), node.getStartMark(), node.getEndMark()));
        }
        validationResults.addAll(super.validateValue(node));
        if (ValidationResult.areValids(validationResults)) {
            setValueNode(node);
        }
        return validationResults;
    }

    public boolean wasAlreadyDefined()
    {
        return keyNode != null;
    }

    public void setKeyNode(ScalarNode rulePresent)
    {
        this.keyNode = rulePresent;
    }

    public ScalarNode getKeyNode()
    {
        return keyNode;
    }

    public ScalarNode getValueNode()
    {
        return valueNode;
    }

    public void setValueNode(ScalarNode valueNode)
    {
        this.valueNode = valueNode;
    }

    public Class<?> getFieldClass()
    {
        return fieldClass;
    }

    public void setFieldClass(Class<?> fieldClass)
    {
        this.fieldClass = fieldClass;
    }
}
