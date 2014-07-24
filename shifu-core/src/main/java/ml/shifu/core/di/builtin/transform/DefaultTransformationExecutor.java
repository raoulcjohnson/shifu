package ml.shifu.core.di.builtin.transform;

import ml.shifu.core.di.spi.TransformationExecutor;
import ml.shifu.core.util.CommonUtils;
import org.dmg.pmml.*;

import org.jpmml.evaluator.*;
import org.jpmml.evaluator.FieldValue;
import org.w3c.dom.Element;

import java.util.*;

public class DefaultTransformationExecutor  {
  /*
    public Object transform(DerivedField derivedField, Object origin) {

        Expression expression = derivedField.getExpression();

        //TODO: finish the list
        if (expression instanceof NormContinuous) {
            return NormalizationUtil.normalize((NormContinuous) expression, Double.parseDouble(origin.toString()));
        } else if (expression instanceof Discretize) {
            return DiscretizationUtil.discretize((Discretize) expression, Double.parseDouble(origin.toString()));
        } else if (expression instanceof MapValues) {
            //return ExpressionUtil.evaluate(expression, );
            return mapValue((MapValues) expression, origin.toString());
        } else {
            throw new RuntimeException("Invalid Expression(Field: " + derivedField.getName().getValue() + ")");
        }

    }
           */
    public List<Object> transform(List<DerivedField> derivedFields, Map<String, Object> rawDataMap) {




        List<Object> result = new ArrayList<Object>();
        for (DerivedField derivedField : derivedFields) {
            result.add(transform(derivedField, rawDataMap));
        }

        return result;
    }

    public Object transform(DerivedField derivedField, Map<String, Object> rawDataMap) {
        Expression expression = derivedField.getExpression();

        //TODO: finish the list
        if (expression instanceof NormContinuous) {
            NormContinuous normContinuous = (NormContinuous) expression;
            Double value = Double.valueOf(rawDataMap.get(normContinuous.getField().getValue()).toString());
            return NormalizationUtil.normalize(normContinuous, value);
        } else if (expression instanceof Discretize) {
            Discretize discretize = (Discretize) expression;
            Double value = Double.valueOf(rawDataMap.get(discretize.getField().getValue()).toString());
            return DiscretizationUtil.discretize(discretize, value);
        } else if (expression instanceof MapValues) {
            MapValues mapValues = (MapValues) expression;


            //return ExpressionUtil.evaluate(expression, );
            return mapValue(mapValues, rawDataMap);
        } else if (expression instanceof FieldRef) {
            return rawDataMap.get(((FieldRef)expression).getField().getValue());
        } else {
            throw new RuntimeException("Invalid Expression(Field: " + derivedField.getName().getValue() + ")");
        }
    }

    /*
    public List<Object> transform(MiningSchema miningSchema, Map<FieldName, DerivedField> fieldNameToDerivedFieldMap, Map<FieldName, Integer> fieldNameToFieldNumberMap, List<Object> raw) {
        List<Object> transformed = new ArrayList<Object>();

        for (MiningField miningField : miningSchema.getMiningFields()) {

            int fieldNum = fieldNameToFieldNumberMap.get(miningField.getName());

            //if (miningField.getUsageType().equals(FieldUsageType.ACTIVE)) {
            if (fieldNameToDerivedFieldMap.containsKey(miningField.getName())) {
                DerivedField derivedField = fieldNameToDerivedFieldMap.get(miningField.getName());
                transformed.add(transform(derivedField, raw.get(fieldNum)));
            } else {
                transformed.add(raw.get(fieldNum));
            }
        }
        return transformed;
    }  */

    private String mapValue(MapValues mapValues, Map<String, Object> rawDataMap) {

        Map<String, FieldValue> values = new LinkedHashMap<String, FieldValue>();

        List<FieldColumnPair> fieldColumnPairs = mapValues.getFieldColumnPairs();
        for(FieldColumnPair fieldColumnPair : fieldColumnPairs){
            FieldValue value = FieldValueUtil.create(mapValues.getDataType(), null, rawDataMap.get(fieldColumnPair.getField().getValue()));

            //if(value == null){
            //    return FieldValueUtil.create(mapValues.getDataType(), null, mapValues.getMapMissingTo());
            //}

            values.put(fieldColumnPair.getColumn(), value);
        }

        return DiscretizationUtil.mapValue(mapValues, values).getValue().toString();

    }


}