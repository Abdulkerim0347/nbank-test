package models.comparison;

import org.assertj.core.api.AbstractAssert;

public class ModelAssertions extends AbstractAssert<ModelAssertions, Object> {

    private final Object request;
    private final Object response;

    private ModelAssertions(Object request, Object response) {
        super(request, ModelAssertions.class);
        this.request = request;
        this.response = response;
    }

    public static ModelAssertions assertThatModels(Object request, Object response) {
        return new ModelAssertions(request, response);
    }

    public ModelAssertions match() {
        ModelComparisonConfigLoader configLoader = new ModelComparisonConfigLoader("model-comparison.properties");
        ModelComparisonConfigLoader.ComparisonRule rule = configLoader.getRuleFor(request.getClass());

        if (rule != null) {
            ModelComparator.ComparisonResult result = ModelComparator.compareFields(
                    request,
                    response,
                    rule.getFieldMappings()
            );

            if (!result.isSuccess()) {
                failWithMessage("Model comparison failed with mismatched fields:\n%s", result);
            }
        } else {
            failWithMessage("No comparison rule found for class %s", request.getClass().getSimpleName());
        }

        return this;
    }
}