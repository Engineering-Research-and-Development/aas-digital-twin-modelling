package it.unisannio.assetinterfacedescriptiondecoder.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIDProperty {
    private List<String> propertyPath;
    private String dataPointPath;
}
