package com.acuo.collateral.transform.services;

import com.acuo.collateral.transform.TransformerContext;
import com.acuo.collateral.transform.trace.transformer_assets.FromSettlementDateOutputWrapper;
import com.acuo.collateral.transform.trace.transformer_assets.Reuters;
import com.acuo.collateral.transform.trace.transformer_assets.ToSettlementDateOutputWrapper;
import com.acuo.collateral.transform.trace.utils.TraceUtils;
import com.acuo.common.util.ArgChecker;
import com.tracegroup.transformer.exposedservices.MomException;
import com.tracegroup.transformer.exposedservices.RuleException;
import com.tracegroup.transformer.exposedservices.StructureException;
import com.tracegroup.transformer.exposedservices.UnrecognizedMessageException;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SettlementDateTransformer<INPUT, OUTPUT> extends BaseTransformer<INPUT, OUTPUT> {

    @Inject
    private Reuters reuters = null;

    @Override
    public String serialise(List<INPUT> value, TransformerContext context) {
        try {
            ToSettlementDateOutputWrapper outputWrapper = reuters.toSettlementDate(value.toArray(), context);
            return outputWrapper.getOutput();
        } catch (MomException | RuleException | UnrecognizedMessageException | StructureException e) {
            String msg = String.format("error occurred while mapping the data %s to a list of assets", value);
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    @Override
    public List<OUTPUT> deserialiseToList(String values) {
        ArgChecker.notNull(values, "values");
        values = TraceUtils.replaceNewLineToWindows(values);
        try {
            FromSettlementDateOutputWrapper output = reuters.fromSettlementDate(values);
            return Arrays.stream(output.getOutput()).map(value -> (OUTPUT)value).collect(Collectors.toList());
        } catch (MomException | RuleException | UnrecognizedMessageException | StructureException e) {
            String msg = String.format("error occurred while mapping the data %s to a list of assets", values);
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}